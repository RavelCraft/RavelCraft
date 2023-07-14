package com.connexal.ravelcraft.shared.messaging;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.util.Lock;
import com.connexal.ravelcraft.shared.util.server.RavelServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;

public abstract class Messager {
    private final Map<String, CompletableFuture<String[]>> responseFutures = new HashMap<>();
    private final Map<MessagingCommand, BiFunction<RavelServer, String[], String[]>> commandHandlers = new HashMap<>();

    public boolean attemptConnect() {
        return this.attemptConnect(0);
    }

    protected abstract boolean attemptConnect(int attempts);

    public abstract DataOutputStream getServerOutputStream(RavelServer server);

    public abstract Lock getWriteLock(RavelServer server);

    private void processRead(RavelServer destination, RavelServer source, MessageType type, String responseId, MessagingCommand command, String[] arguments) {
        if (this.isServer()) {
            if (destination != null && destination != RavelInstance.getServer()) {
                this.writeStream(destination, source, type, responseId, command, arguments);
                return;
            }
        } else {
            if (destination != null) {
                RavelInstance.getLogger().warning("Received message with redirect but this is not the server! Ignoring.");
                return;
            }
        }

        if (type == MessageType.RESPONSE) {
            CompletableFuture<String[]> future = this.responseFutures.remove(responseId);
            if (future == null) {
                RavelInstance.getLogger().warning("Received response with invalid response ID: " + responseId);
                return;
            }

            future.complete(arguments);
            return;
        }

        try {
            this.runCommand(source, command, arguments, responseId);
        } catch (IllegalArgumentException e) {
            RavelInstance.getLogger().warning("Unknown command received from " + source + ": " + command);
        }
    }

    protected void readStream(DataInputStream input) throws IOException {
        int dataParts = input.readInt();
        String[] strings;
        String[] arguments;

        if (dataParts < 1) { //Ignore, can be used as a ping
            return;
        }

        strings = new String[input.readInt()];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = input.readUTF();
        }

        if (dataParts > 1) {
            if (dataParts != 2) {
                RavelInstance.getLogger().warning("Received message with too many data parts");
                return;
            }

            arguments = new String[input.readInt()];
            for (int i = 0; i < arguments.length; i++) {
                arguments[i] = input.readUTF();
            }
        } else {
            arguments = new String[0];
        }

        if (strings.length < MessageFormat.length() - 1) {
            RavelInstance.getLogger().warning("Received message with too few arguments: " + Arrays.toString(strings));
            return;
        }

        RavelServer destination = null;
        if (this.isServer()) {
            String destinationString = strings[MessageFormat.DESTINATION.index()];

            try {
                destination = RavelServer.valueOf(destinationString);
            } catch (IllegalArgumentException e) {
                RavelInstance.getLogger().warning("Unknown destination server " + destinationString);
                return;
            }
        }

        RavelServer source;
        String sourceString = strings[MessageFormat.SOURCE.index()];
        try {
            source = RavelServer.valueOf(sourceString);
        } catch (IllegalArgumentException e) {
            RavelInstance.getLogger().warning("Unknown source server " + sourceString);
            return;
        }

        MessageType type;
        String typeString = strings[MessageFormat.TYPE.index()];
        try {
            type = MessageType.valueOf(typeString);
        } catch (IllegalArgumentException e) {
            RavelInstance.getLogger().warning("Unknown message type received from " + sourceString + ": " + typeString);
            type = null;
        }

        MessagingCommand command;
        String commandString = strings[MessageFormat.COMMAND.index()];
        try {
            command = MessagingCommand.valueOf(commandString);
        } catch (IllegalArgumentException e) {
            RavelInstance.getLogger().warning("Unknown command received from " + source + ": " + commandString);
            return;
        }

        String responseId = null;
        if (strings.length == MessageFormat.length()) {
            responseId = strings[MessageFormat.RESPONSE_ID.index()];
        }

        //RavelInstance.getLogger().info("Received message " + command + " with response ID " + responseId + " and args " + Arrays.toString(arguments) + " from " + source);

        this.processRead(destination, source, type, responseId, command, arguments);
    }

    private boolean writeStream(RavelServer destination, RavelServer source, MessageType type, String responseId, MessagingCommand command, String[] arguments) {
        if (destination == RavelInstance.getServer()) {
            RavelInstance.getLogger().error("Attempted to send message to self!");
            return false;
        }

        DataOutputStream output = this.getServerOutputStream(destination);
        if (output == null) {
            RavelInstance.getLogger().error("Attempted to send message to " + destination + " but no connection exists!");
            return false;
        }

        if (type == MessageType.RESPONSE || type == MessageType.BIDIRECTIONAL) {
            if (responseId == null) {
                RavelInstance.getLogger().error("There must be a response ID attached if you are sending a bidirectional or response message!");
                return false;
            } else if (type == MessageType.BIDIRECTIONAL) {
                if (!this.responseFutures.containsKey(responseId)) {
                    RavelInstance.getLogger().error("Tried to send bidirectional message with unregistered response ID!");
                    return false;
                }
            }
        } else {
            if (responseId != null) {
                RavelInstance.getLogger().error("There must not be a response ID if you are sending a single direction message!");
                return false;
            }
        }

        try {
            int dataCount = MessageFormat.length();
            if (responseId == null) {
                dataCount--;
            }
            String[] data = new String[dataCount];

            data[MessageFormat.SOURCE.index()] = source.name();
            data[MessageFormat.DESTINATION.index()] = destination.name();
            data[MessageFormat.TYPE.index()] = type.name();
            data[MessageFormat.COMMAND.index()] = command.name();
            if (responseId != null) {
                data[MessageFormat.RESPONSE_ID.index()] = responseId;
            }

            Lock writeLock = this.getWriteLock(destination);
            writeLock.lock();

            boolean sendArgs = true;
            if (arguments == null || arguments.length == 0) {
                sendArgs = false;
                output.writeInt(1);
            } else {
                output.writeInt(2);
            }

            output.writeInt(data.length);
            for (String string : data) {
                output.writeUTF(string);
            }

            if (sendArgs) {
                output.writeInt(arguments.length);
                for (String string : arguments) {
                    output.writeUTF(string);
                }
            }

            output.flush();
            writeLock.unlock();
        } catch (IOException e) {
            RavelInstance.getLogger().error("Failed to send message to server", e);
            return false;
        }

        //RavelInstance.getLogger().info("Sent message " + command + " with response ID " + responseId + " and args " + Arrays.toString(arguments) + " to " + destination);

        return true;
    }

    public void registerCommandHandler(MessagingCommand command, BiFunction<RavelServer, String[], String[]> handler) {
        if (this.commandHandlers.remove(command) != null) {
            RavelInstance.getLogger().warning("Overriding command handler for " + command);
        }

        this.commandHandlers.put(command, handler);
    }

    protected void runCommand(RavelServer source, MessagingCommand command, String[] args, String responseId) {
        BiFunction<RavelServer, String[], String[]> handler = this.commandHandlers.get(command);
        if (handler == null) {
            throw new IllegalArgumentException("No handler registered for command " + command);
        }

        String[] retData = handler.apply(source, args);
        if (responseId == null) {
            if (retData != null) {
                throw new IllegalArgumentException("Command " + command + " returned data but was not expecting a response!");
            }
        } else {
            if (retData == null) {
                throw new IllegalArgumentException("Command " + command + " expected a response but did not return any data!");
            }
            this.sendResponse(source, responseId, retData);
        }
    }

    public CompletableFuture<String[]> sendCommandWithResponse(RavelServer server, MessagingCommand command, String... args) {
        String responseId = System.currentTimeMillis() + String.valueOf(Math.random()).substring(2);
        CompletableFuture<String[]> future = new CompletableFuture<>();

        this.responseFutures.put(responseId, future);

        if (!this.writeStream(server, RavelInstance.getServer(), MessageType.BIDIRECTIONAL, responseId, command, args)) {
            this.responseFutures.remove(responseId);
            return null;
        }

        //Add a timeout
        new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                return;
            }

            if (this.responseFutures.containsKey(responseId)) {
                this.responseFutures.remove(responseId);
                future.completeExceptionally(new TimeoutException("Response timed out"));
            }
        }).start();

        return future;
    }

    public void sendCommand(RavelServer server, MessagingCommand command, String... args) {
        this.writeStream(server, RavelInstance.getServer(), MessageType.SINGLE_DIRECTION, null, command, args);
    }

    private void sendResponse(RavelServer server, String responseId, String... args) {
        this.writeStream(server, RavelInstance.getServer(), MessageType.RESPONSE, responseId, MessagingCommand.RESPONSE, args);
    }

    public abstract void close();

    public abstract boolean isServer();
}
