package com.connexal.ravelcraft.shared.messaging;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.data.Server;

public interface Messager {
    void attemptConnect();

    void runCommand(MessagingCommand command, String[] args);

    void sendCommand(Server server, MessagingCommand command, String... args);

    default void sendCommand(MessagingCommand command, String... args) {
        if (RavelInstance.getServer() == Server.JE_PROXY) {
            RavelInstance.getLogger().error("Server proxy attempted to send command to self. Failed.");
        } else {
            this.sendCommand(Server.JE_PROXY, command, args);
        }
    }

    void close();
}
