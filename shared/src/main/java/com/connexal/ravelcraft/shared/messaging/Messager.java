package com.connexal.ravelcraft.shared.messaging;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.util.RavelServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class Messager {
    private final Map<String, CompletableFuture<String[]>> responseFutures = new HashMap<>();

    public boolean attemptConnect() {
        return this.attemptConnect(0);
    }

    protected abstract boolean attemptConnect(int attempts);

    public abstract DataOutputStream getServerOutputStream(RavelServer server);

    private synchronized void processRead(RavelServer destination, MessageType type, String responseId, MessagingCommand command, String[] arguments) {
        if (this.isServer()) {
            if (destination != null && destination != RavelInstance.getServer()) {
                this.writeStream(destination, type, responseId, command, arguments);
                return;
            }
        } else {
            if (destination != null) {
                RavelInstance.getLogger().warning("Received message with redirect from proxy! Ignoring.");
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
            this.runCommand(command, arguments, responseId);
        } catch (IllegalArgumentException e) {
            RavelInstance.getLogger().warning("Unknown command received from proxy: " + command);
        }
    }

    protected synchronized void readStream(DataInputStream input) throws IOException {
        String destinationString = input.readUTF();

        String typeString = input.readUTF();
        MessageType type;
        try {
            type = MessageType.valueOf(typeString);
        } catch (IllegalArgumentException e) {
            RavelInstance.getLogger().warning("Unknown message type received from proxy: " + typeString);
            type = null;
        }

        String responseId = null;
        if (type == MessageType.RESPONSE || type == MessageType.BIDIRECTIONAL) {
            responseId = input.readUTF();
        }

        String commandString = input.readUTF();
        int argumentCount = input.readInt();

        String[] arguments = new String[argumentCount];
        for (int i = 0; i < argumentCount; i++) {
            arguments[i] = input.readUTF();
        }

        if (type == null) {
            return;
        }

        RavelServer destination = null;
        if (this.isServer()) {
            try {
                destination = RavelServer.valueOf(destinationString);
            } catch (IllegalArgumentException e) {
                RavelInstance.getLogger().warning("Unknown destination server " + destinationString);
                return;
            }
        }

        MessagingCommand command;
        try {
            command = MessagingCommand.valueOf(commandString);
        } catch (IllegalArgumentException e) {
            RavelInstance.getLogger().warning("Unknown command received from proxy: " + commandString);
            return;
        }

        this.processRead(destination, type, responseId, command, arguments);
    }

    private synchronized boolean writeStream(RavelServer destination, MessageType type, String responseId, MessagingCommand command, String[] arguments) {
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
            output.writeUTF(destination.name());
            output.writeUTF(type.name());
            if (type == MessageType.RESPONSE || type == MessageType.BIDIRECTIONAL) {
                output.writeUTF(responseId);
            }
            output.writeUTF(command.name());
            output.writeInt(arguments.length);
            for (String argument : arguments) {
                output.writeUTF(argument);
            }

            output.flush();
        } catch (IOException e) {
            RavelInstance.getLogger().error("Failed to send message to server", e);
            return false;
        }

        return true;
    }

    protected abstract void runCommand(MessagingCommand command, String[] args, String responseId);

    public CompletableFuture<String[]> sendCommandWithResponse(RavelServer server, MessagingCommand command, String... args) {
        String responseId = System.currentTimeMillis() + String.valueOf(Math.random()).substring(2);
        CompletableFuture<String[]> future = new CompletableFuture<>();

        this.responseFutures.put(responseId, future);

        if (!this.writeStream(server, MessageType.BIDIRECTIONAL, responseId, command, args)) {
            this.responseFutures.remove(responseId);
            return null;
        }

        return future;
    }

    public void sendCommand(RavelServer server, MessagingCommand command, String... args) {
        this.writeStream(server, MessageType.SINGLE_DIRECTION, null, command, args);
    }

    public void sendResponse(RavelServer server, String responseId, String... args) {
        this.writeStream(server, MessageType.RESPONSE, responseId, MessagingCommand.RESPONSE, args);
    }

    public abstract void close();

    public abstract boolean isServer();
}
