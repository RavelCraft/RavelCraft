package com.connexal.ravelcraft.proxy.cross;

import com.connexal.ravelcraft.proxy.cross.servers.ServerManager;

public class RavelProxyInstance {
    private static ServerManager serverManager;

    public static void init(ServerManager serverManager) {
        RavelProxyInstance.serverManager = serverManager;

        serverManager.init();
    }

    public static ServerManager getServerManager() {
        return serverManager;
    }
}
