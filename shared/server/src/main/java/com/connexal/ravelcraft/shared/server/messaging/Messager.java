package com.connexal.ravelcraft.shared.server.messaging;

import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.all.util.Lock;
import com.connexal.ravelcraft.shared.server.util.server.RavelServer;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class Messager {
    private static final Gson gson = new Gson();

    private final Map<String, CompletableFuture<String[]>> responseFutures = new HashMap<>();
    private final Map<MessagingCommand, BiFunction<RavelServer, String[], String[]>> commandHandlers = new HashMap<>();

    private final List<Consumer<RavelServer>> disconnectHandlers = new ArrayList<>();

    public boolean attemptConnect() {
        return this.attemptConnect(0);
    }

    protected abstract boolean attemptConnect(int attempts);

    public abstract boolean otherProxyConnected();

    protected void disconnectedFromMessaging(RavelServer server) {
        for (Consumer<RavelServer> handler : this.disconnectHandlers) {
            try {
                handler.accept(server);
            } catch (Exception e) {
                RavelInstance.getLogger().error("Not sure what to do, more errors because of messaging disconnect", e);
            }
        }
    }

    public void registerDisconnectHandler(Consumer<RavelServer> handler) {
        this.disconnectHandlers.add(handler);
    }

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
        String data;
        try {
            data = input.readUTF();
        } catch (SocketTimeoutException e) {
            return;
        }

        MessageData format;
        try {
            format = MessageData.fromJSON(data);
        } catch (Exception e) {
            RavelInstance.getLogger().error("Failed to parse message from server", e);
            return;
        }

        final RavelServer destination;
        if (this.isServer()) {
            destination = format.getDestination();
        } else {
            destination = null;
        }

        /*if (destination == null || destination == RavelInstance.getServer()) {
            RavelInstance.getLogger().info("Received message " + format.getCommand() + " with response ID " + format.getResponseId() + " and args " + Arrays.toString(format.getArguments()) + " from " + format.getSource() + " redirect? " + destination);
        }*/

        RavelInstance.scheduleTask(() -> {
            this.processRead(destination, format.getSource(), format.getType(), format.getResponseId(), format.getCommand(), format.getArguments());
        });
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

        //Only check the messages that come from us, the other server will have done its own checks
        if (source == RavelInstance.getServer()) {
            if (type == MessageType.RESPONSE || type == MessageType.BIDIRECTIONAL) {
                if (responseId == null) {
                    RavelInstance.getLogger().error("There must be a response ID attached if you are sending a bidirectional or response message!");
                    return false;
                } else if (type == MessageType.BIDIRECTIONAL && !this.responseFutures.containsKey(responseId)) {
                    RavelInstance.getLogger().error("Tried to send bidirectional message with no registered completion future!");
                    return false;
                }
            } else {
                if (responseId != null) {
                    RavelInstance.getLogger().error("There must not be a response ID if you are sending a single direction message!");
                    return false;
                }
            }
        }

        MessageData format = new MessageData(source, destination, type, command, responseId, arguments);

        Lock writeLock = this.getWriteLock(destination);
        writeLock.lock();

        try {
            output.writeUTF(format.toString());
            output.flush();
        } catch (IOException e) {
            RavelInstance.getLogger().error("Failed to send message to server", e);
            writeLock.unlock();
            return false;
        }

        writeLock.unlock();

        /*if (source == RavelInstance.getServer()) {
            RavelInstance.getLogger().info("Sent message " + command + " with response ID " + responseId + " and args " + Arrays.toString(arguments) + " to " + destination + " from " + source);
        }*/

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

    public String[] sendCommandWithResponse(RavelServer server, MessagingCommand command, String... args) {
        String responseId = System.currentTimeMillis() + String.valueOf(Math.random()).substring(2);
        CompletableFuture<String[]> future = new CompletableFuture<>();

        this.responseFutures.put(responseId, future);

        if (!this.writeStream(server, RavelInstance.getServer(), MessageType.BIDIRECTIONAL, responseId, command, args)) {
            this.responseFutures.remove(responseId);
            return null;
        }

        //Add a timeout
        RavelInstance.scheduleTask(() -> {
            if (this.responseFutures.containsKey(responseId)) {
                this.responseFutures.remove(responseId);
                future.completeExceptionally(new TimeoutException("Response timed out"));
            }
        }, 5);

        try {
            return future.join();
        } catch (CompletionException e) {
            RavelInstance.getLogger().error("Timeout while waiting for response from proxy server!");
            return null;
        }
    }

    public void sendCommand(RavelServer server, MessagingCommand command, String... args) {
        this.writeStream(server, RavelInstance.getServer(), MessageType.SINGLE_DIRECTION, null, command, args);
    }

    private void sendResponse(RavelServer server, String responseId, String... args) {
        this.writeStream(server, RavelInstance.getServer(), MessageType.RESPONSE, responseId, MessagingCommand.RESPONSE, args);
    }

    public abstract void close();

    public abstract boolean isServer();

    private static class MessageFormat {
        public String source;
        public String destination;
        public String type;
        public String responseId;
        public String command;
        public String[] arguments;
    }

    private static class MessageData {
        private final RavelServer source;
        private final RavelServer destination;
        private final MessageType type;
        private final MessagingCommand command;
        private final String responseId;
        private final String[] arguments;

        private MessageData(MessageFormat format) {
            try {
                this.source = RavelServer.valueOf(format.source);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid source server: " + format.source);
            }

            try {
                this.destination = RavelServer.valueOf(format.destination);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid destination server: " + format.destination);
            }

            try {
                this.type = MessageType.valueOf(format.type);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid message type: " + format.type);
            }

            try {
                this.command = MessagingCommand.valueOf(format.command);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unknown command: " + format.command);
            }

            this.responseId = format.responseId;
            this.arguments = format.arguments;
        }

        public MessageData(RavelServer source, RavelServer destination, MessageType type, MessagingCommand command, String responseId, String[] arguments) {
            this.source = source;
            this.destination = destination;
            this.type = type;
            this.command = command;
            this.responseId = responseId;
            this.arguments = arguments;
        }

        public RavelServer getSource() {
            return this.source;
        }

        public RavelServer getDestination() {
            return this.destination;
        }

        public MessageType getType() {
            return this.type;
        }

        public MessagingCommand getCommand() {
            return this.command;
        }

        public String getResponseId() {
            return this.responseId;
        }

        public String[] getArguments() {
            return this.arguments;
        }

        public static MessageData fromJSON(String json) {
            MessageFormat format = gson.fromJson(json, MessageFormat.class);
            return new MessageData(format);
        }

        @Override
        public String toString() {
            MessageFormat format = new MessageFormat();
            format.source = this.source.name();
            format.destination = this.destination.name();
            format.type = this.type.name();
            format.command = this.command.name();
            format.responseId = this.responseId;
            format.arguments = this.arguments;

            return gson.toJson(format);
        }
    }
}
