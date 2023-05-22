package com.connexal.ravelcraft.shared.messaging;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.data.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MessagingServer implements Messager {
    private ServerSocket serverSocket;
    private final Map<Server, ClientData> clients = new HashMap<>();
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
            RavelInstance.getLogger().warning("Server " + serverName + " disconnected: " + e.getMessage());
        }

        client.close();
        if (server != null) {
            this.clients.remove(server);
        }
    }

    @Override
    public void attemptConnect() {
        // Do nothing, we can't connect to ourselves
    }

    @Override
    public void runCommand(MessagingCommand command, String[] args) {
        switch (command) {
            default: {
                RavelInstance.getLogger().warning("Unable to do anything with received command: " + command);
                break;
            }
        }
    }

    @Override
    public void sendCommand(Server server, MessagingCommand command, String... args) {
        if (server == RavelInstance.getServer()) {
            RavelInstance.getLogger().warning("Attempted to send command to self. It went through, but this is probably a mistake.");
            this.runCommand(command, args);
            return;
        }

        try {
            ClientData socket = this.clients.get(server);
            if (socket == null) {
                RavelInstance.getLogger().warning("Unable to find socket for server " + server.getName());
                return;
            }

            DataOutputStream outputStream = socket.getOutputStream();
            outputStream.writeUTF(command.name());
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

        for (ClientData socket : this.clients.values()) {
            socket.close();
        }
        this.clients.clear();
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
