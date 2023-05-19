package com.connexal.ravelcraft.jeproxy.messaging;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.data.Server;
import com.connexal.ravelcraft.shared.messaging.MessagingConstants;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MessagingServer {
    private ServerSocket serverSocket;
    private final Map<Server, MessagingClientData> clients = new HashMap<>();
    private boolean listening = false;

    public MessagingServer() {
        try {
            this.serverSocket = new ServerSocket(MessagingConstants.PORT);
        } catch (IOException e) {
            RavelInstance.getLogger().warning("Unable to start plugin messaging on port " + MessagingConstants.PORT);
            return;
        }

        new Thread(this::listen).start();
    }

    private void listen() {
        while (this.listening) {
            MessagingClientData client;

            try {
                Socket socket = this.serverSocket.accept();
                client = new MessagingClientData(socket);
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
    }

    private void clientListen(MessagingClientData client) {
        DataOutputStream outputStream = client.getOutputStream();
        DataInputStream inputStream = client.getInputStream();

        Server server = null;
        String serverName = "Not found";

        try {
            //--- Connection handshake ---
            outputStream.writeUTF(MessagingConstants.MAGIC + "\n");
            outputStream.flush();

            serverName = inputStream.readUTF();
            try {
                server = Server.valueOf(serverName);
            } catch (IllegalArgumentException e) {
                RavelInstance.getLogger().warning("Unable to find server " + serverName);
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
                String command = inputStream.readUTF();
                int argumentCount = inputStream.readInt();

                String[] arguments = new String[argumentCount];
                for (int i = 0; i < argumentCount; i++) {
                    arguments[i] = inputStream.readUTF();
                }

                try {
                    this.runCommand(MessagingCommand.valueOf(command), arguments);
                } catch (IllegalArgumentException e) {
                    RavelInstance.getLogger().warning("Unknown command received from server " + serverName + ": " + command);
                }
            }
        } catch (NullPointerException ignored) {
        } catch (IOException e) {
            RavelInstance.getLogger().warning("Server disconnected: " + serverName);
        }

        client.close();
        if (server != null) {
            this.clients.remove(server);
        }
    }

    private void runCommand(MessagingCommand command, String[] args) throws IOException {
        switch (command) {
            default: {
                RavelInstance.getLogger().warning("Unknown command received from server: " + command);
                break;
            }
        }
    }

    public void sendCommand(Server server, String command, String... args) {
        try {
            MessagingClientData socket = this.clients.get(server);
            if (socket == null) {
                RavelInstance.getLogger().warning("Unable to find socket for server " + server.getName());
                return;
            }

            DataOutputStream outputStream = socket.getOutputStream();
            outputStream.writeUTF(command);
            outputStream.writeInt(args.length);
            for (String arg : args) {
                outputStream.writeUTF(arg);
            }
            outputStream.flush();
        } catch (NullPointerException e) {
            RavelInstance.getLogger().warning("Unable to find server to send command to");
        } catch (IOException e) {
            RavelInstance.getLogger().warning("Unable to send command to server");
        }
    }

    public void close() {
        this.listening = false;

        try {
            this.serverSocket.close();
        } catch (IOException e) {
            RavelInstance.getLogger().error("Unable to close plugin messaging server socket", e);
        }

        for (MessagingClientData socket : this.clients.values()) {
            socket.close();
        }
        this.clients.clear();
    }

    private static class MessagingClientData {
        private final Socket socket;
        private final DataOutputStream outputStream;
        private final DataInputStream inputStream;

        public MessagingClientData(Socket socket) throws IOException {
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
