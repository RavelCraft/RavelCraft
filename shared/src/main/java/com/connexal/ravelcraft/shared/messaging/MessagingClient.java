package com.connexal.ravelcraft.shared.messaging;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.data.Server;

import java.io.*;
import java.net.Socket;

public class MessagingClient implements Messager {
    private final String serverHostname;

    private boolean connected = false;
    private Socket socket;
    private DataOutputStream output;
    private DataInputStream input;

    public MessagingClient(String hostname) {
        this.serverHostname = hostname;
        this.attemptConnect();
    }

    @Override
    public void attemptConnect() {
        if (this.connected) {
            return;
        }

        new Thread(() -> {
            this.connected = true;

            try {
                this.socket = new Socket(this.serverHostname, MessagingConstants.PORT);
            } catch (IOException e) {
                this.connected = false;
                RavelInstance.getLogger().warning("Unable to connect to plugin messaging server at " + this.serverHostname + "! " + e.getMessage());
                return;
            }

            try {
                this.output = new DataOutputStream(this.socket.getOutputStream());
                this.input = new DataInputStream(this.socket.getInputStream());

                String magic = this.input.readUTF();
                if (!magic.equals(MessagingConstants.MAGIC)) {
                    this.connected = false;
                    RavelInstance.getLogger().warning("Unable to connect to plugin messaging server at " + this.serverHostname + "! Invalid magic.");
                    return;
                }

                this.output.writeUTF(RavelInstance.getServer().name());
                this.output.flush();

                if (!this.input.readBoolean()) {
                    this.connected = false;
                    RavelInstance.getLogger().warning("Unable to connect to plugin messaging server at " + this.serverHostname + "! Server refused connection.");
                    return;
                }
            } catch (IOException e) {
                this.connected = false;
                RavelInstance.getLogger().warning("Unable to connect to plugin messaging server at " + this.serverHostname + "! " + e.getMessage());
                return;
            }

            RavelInstance.getLogger().info("Connected to plugin messaging server at " + this.serverHostname + ".");

            try {
                while (this.connected) {
                    String command = this.input.readUTF();
                    int argumentCount = this.input.readInt();

                    String[] arguments = new String[argumentCount];
                    for (int i = 0; i < argumentCount; i++) {
                        arguments[i] = this.input.readUTF();
                    }

                    try {
                        this.runCommand(MessagingCommand.valueOf(command), arguments);
                    } catch (IllegalArgumentException e) {
                        RavelInstance.getLogger().warning("Unknown command received from proxy: " + command);
                    }
                }
            } catch (IOException e) {
                RavelInstance.getLogger().warning("Got disconnected from plugin messaging server! " + e.getMessage());
            }

            this.close();
        }).start();
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
        try {
            this.output.writeUTF(command.name());
            this.output.writeInt(args.length);
            for (String arg : args) {
                this.output.writeUTF(arg);
            }

            this.output.flush();
        } catch (IOException e) {
            RavelInstance.getLogger().warning("Unable to send command to server");
        }
    }

    @Override
    public void close() {
        this.connected = false;
        if (this.socket == null) {
            return;
        }

        try {
            InputStream inputStream = this.socket.getInputStream();
            OutputStream outputStream = this.socket.getOutputStream();
            this.socket.close();

            inputStream.close();
            this.input.close();

            outputStream.close();
            this.output.close();
        } catch (IOException e) {
            RavelInstance.getLogger().error("Unable to close plugin messaging socket");
        }

        this.socket = null;
    }
}
