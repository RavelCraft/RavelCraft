package com.connexal.ravelcraft.shared.messaging;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.util.RavelServer;

public interface Messager {
    void attemptConnect();

    void runCommand(MessagingCommand command, String[] args);

    void sendCommand(RavelServer server, MessagingCommand command, String... args);

    default void sendCommand(MessagingCommand command, String... args) {
        if (RavelInstance.getServer() == RavelServer.JE_PROXY) {
            RavelInstance.getLogger().error("Server proxy attempted to send command to self. Failed.");
        } else {
            this.sendCommand(RavelServer.JE_PROXY, command, args);
        }
    }

    void close();

    boolean isServer();
}
