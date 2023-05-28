package com.connexal.ravelcraft.shared.messaging;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.util.RavelServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MessagingServer extends Messager {
    private ServerSocket serverSocket;
    private final Map<RavelServer, ClientData> clients = new HashMap<>();
    private boolean listening = false;

    public MessagingServer() {
        RavelInstance.getLogger().info("Starting plugin messaging server on port " + MessagingConstants.PORT + "...");

        try {
            this.serverSocket = new ServerSocket(MessagingConstants.PORT);
        } catch (IOException e) {
            RavelInstance.getLogger().error("Unable to start plugin messaging server!", e);
            return;
        }

        new Thread(this::listen).start();
    }

    private void listen() {
        if (this.listening) {
            RavelInstance.getLogger().warning("Plugin messaging socket already open, can't open again!");
            return;
        }

        RavelInstance.getLogger().info("Plugin messaging socket open and listening.");

        this.listening = true;
        while (this.listening) {
            ClientData client;

            try {
                Socket socket = this.serverSocket.accept();
                client = new ClientData(socket);
            } catch (IOException e) {
                continue;
            }

            if (!this.listening) {
                break;
            }

            new Thread(() -> {
                this.clientListen(client);
            }).start();
        }

        RavelInstance.getLogger().info("Plugin messaging socket closed.");
    }

    private void clientListen(ClientData client) {
        DataOutputStream outputStream = client.getOutputStream();
        DataInputStream inputStream = client.getInputStream();

        RavelServer server = null;
        String serverName = "Not found";

        try {
            //--- Connection handshake ---
            outputStream.writeUTF(MessagingConstants.MAGIC + "\n");
            outputStream.flush();

            serverName = inputStream.readUTF();
            try {
                server = RavelServer.valueOf(serverName);
            } catch (IllegalArgumentException e) {
                RavelInstance.getLogger().warning("Unable to find server " + serverName);
                outputStream.writeBoolean(false);
                client.close();
                return;
            }
            serverName = server.getName();

            if (this.clients.containsKey(server)) {
                RavelInstance.getLogger().warning("Server " + serverName + " already connected but tried to connect again!");
                outputStream.writeBoolean(false);
                client.close();
                return;
            }

            this.clients.put(server, client);
            RavelInstance.getLogger().info("Server connected: " + serverName);

            outputStream.writeBoolean(true);
            outputStream.flush();

            //--- Main loop ---

            while (this.listening) {
                this.readStream(inputStream);
            }
        } catch (NullPointerException ignored) {
        } catch (IOException e) {
            RavelInstance.getLogger().warning("Server " + serverName + " disconnected: " + e.getMessage());
        }

        client.close();
        if (server != null) {
            this.clients.remove(server);
        }
    }

    @Override
    public boolean attemptConnect(int attempts) {
        // Do nothing, no need to connect to ourselves
        return true;
    }

    @Override
    public DataOutputStream getServerOutputStream(RavelServer server) {
        if (!this.listening || !this.clients.containsKey(server)) {
            return null;
        }

        return this.clients.get(server).getOutputStream();
    }

    @Override
    public void runCommand(MessagingCommand command, String[] args, String responseId) {
        switch (command) {
            default: {
                RavelInstance.getLogger().warning("Unable to do anything with received command: " + command);
                break;
            }
        }
    }

    @Override
    public void close() {
        this.listening = false;

        try {
            this.serverSocket.close();
        } catch (IOException e) {
            RavelInstance.getLogger().error("Unable to close plugin messaging server socket", e);
        }

        for (ClientData socket : this.clients.values()) {
            socket.close();
        }
        this.clients.clear();
    }

    @Override
    public boolean isServer() {
        return true;
    }

    private static class ClientData {
        private final Socket socket;
        private final DataOutputStream outputStream;
        private final DataInputStream inputStream;

        public ClientData(Socket socket) throws IOException {
            this.socket = socket;

            this.outputStream = new DataOutputStream(socket.getOutputStream());
            this.inputStream = new DataInputStream(socket.getInputStream());
        }

        public DataOutputStream getOutputStream() {
            return this.outputStream;
        }

        public DataInputStream getInputStream() {
            return this.inputStream;
        }

        public void close() {
            try {
                InputStream inputStream = this.socket.getInputStream();
                OutputStream outputStream = this.socket.getOutputStream();
                this.socket.close();

                outputStream.close();
                this.outputStream.close();

                inputStream.close();
                this.inputStream.close();
            } catch (IOException ignored) {
            }
        }
    }
}
