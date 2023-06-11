package com.connexal.ravelcraft.shared.messaging;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.PlayerManager;
import com.connexal.ravelcraft.shared.util.RavelServer;

import java.io.*;
import java.net.Socket;

public class MessagingClient extends Messager {
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
    public boolean attemptConnect(int attempts) {
        if (attempts >= 2) {
            RavelInstance.getLogger().error("Too many attempts to connect to plugin messaging.");
            return false;
        }

        if (this.connected) {
            return true;
        }

        this.connected = true;

        try {
            this.socket = new Socket(this.serverHostname, MessagingConstants.PORT);
        } catch (IOException e) {
            this.connected = false;
            RavelInstance.getLogger().error("Unable to connect to plugin messaging server at " + this.serverHostname, e);
            return this.attemptConnect(attempts + 1);
        }

        try {
            this.output = new DataOutputStream(this.socket.getOutputStream());
            this.input = new DataInputStream(this.socket.getInputStream());

            String magic = this.input.readUTF();
            if (!magic.equals(MessagingConstants.MAGIC + "\n")) {
                this.connected = false;
                RavelInstance.getLogger().error("Unable to connect to plugin messaging server at " + this.serverHostname + "! Invalid magic.");
                return this.attemptConnect(attempts + 1);
            }

            this.output.writeUTF(RavelInstance.getServer().name());
            this.output.flush();

            if (!this.input.readBoolean()) {
                this.connected = false;
                RavelInstance.getLogger().error("Unable to connect to plugin messaging server at " + this.serverHostname + "! Server refused connection.");
                return this.attemptConnect(attempts + 1);
            }
        } catch (IOException e) {
            this.connected = false;
            RavelInstance.getLogger().error("Unable to connect to plugin messaging server at " + this.serverHostname, e);
            return this.attemptConnect(attempts + 1);
        }

        new Thread(() -> {
            try {
                while (this.connected) {
                    this.readStream(this.input);
                }
            } catch (IOException e) {
                RavelInstance.getLogger().error("Got disconnected from plugin messaging server! ", e);
            }

            this.close();
            this.attemptConnect();
        }).start();

        RavelInstance.getLogger().info("Connected to plugin messaging server at " + this.serverHostname + ".");
        PlayerManager playerManager = RavelInstance.getPlayerManager();
        if (playerManager != null) {
            playerManager.messagingConnected(MessagingConstants.MESSAGING_SERVER);
        }

        return true;
    }

    @Override
    public DataOutputStream getServerOutputStream(RavelServer server) {
        if (server != MessagingConstants.MESSAGING_SERVER || !this.connected) {
            return null;
        }

        return this.output;
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

    @Override
    public boolean isServer() {
        return false;
    }
}
