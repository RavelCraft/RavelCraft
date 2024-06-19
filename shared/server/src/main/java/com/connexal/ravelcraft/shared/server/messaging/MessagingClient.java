package com.connexal.ravelcraft.shared.server.messaging;

import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.all.util.Lock;
import com.connexal.ravelcraft.shared.server.util.server.RavelServer;

import java.io.*;
import java.net.Socket;

public class MessagingClient extends Messager {
    private final String serverHostname;

    private boolean connected = false;
    private Socket socket;
    private DataOutputStream output;
    private DataInputStream input;
    private final Lock writeLock = new Lock();

    private static final int MAX_CONNECT_ATTEMPTS = 3;

    public MessagingClient(String hostname) {
        this.serverHostname = hostname;
        this.attemptConnect();

        RavelInstance.scheduleRepeatingTask(() -> {
            this.attemptConnect(MAX_CONNECT_ATTEMPTS - 1);

            String[] response = this.sendCommandWithResponse(MessagingConstants.MESSAGING_SERVER, MessagingCommand.HEARTBEAT, "PING");
            if (response != null && (response.length == 0 || !response[0].equals("PONG"))) {
                this.close();
            }
        }, MessagingConstants.HEARTBEAT_INTERVAL);
    }

    @Override
    public boolean attemptConnect(int attempts) {
        if (this.connected) {
            return true;
        }

        if (attempts >= MAX_CONNECT_ATTEMPTS) {
            RavelInstance.getLogger().error("Too many attempts to connect to plugin messaging.");
            return false;
        }

        this.connected = true;

        try {
            this.socket = new Socket(this.serverHostname, MessagingConstants.PORT);
            this.socket.setKeepAlive(true);
            this.socket.setTcpNoDelay(true);
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
        return true;
    }

    @Override
    public boolean otherProxyConnected() {
        return this.connected;
    }

    @Override
    public DataOutputStream getServerOutputStream(RavelServer server) {
        if (!this.connected) {
            return null;
        }

        return this.output;
    }

    @Override
    public Lock getWriteLock(RavelServer server) {
        return this.writeLock;
    }

    @Override
    public void close() {
        this.disconnectedFromMessaging(RavelInstance.getServer());

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
