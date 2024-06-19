package com.connexal.ravelcraft.mod.server.listeners;

import com.connexal.ravelcraft.shared.server.RavelInstance;

public class Listeners {
    //The order in which these are registered is probably important
    public static void register() {
        ConnectionListener.register();

        if (RavelInstance.getServer().isLobby()) {
            RavelInstance.getLogger().info("Detected lobby server, registering lobby protections.");
            LobbyListener.register();
        }
        //Note that if enabled, 1984 also registers some event listeners

        CustomisationListener.register();
        ChatMessageCatcher.register();
    }
}
