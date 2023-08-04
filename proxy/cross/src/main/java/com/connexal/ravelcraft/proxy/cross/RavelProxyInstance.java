package com.connexal.ravelcraft.proxy.cross;

import com.connexal.ravelcraft.proxy.cross.servers.MotdManager;
import com.connexal.ravelcraft.proxy.cross.servers.ServerManager;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.util.server.ProxyType;

public class RavelProxyInstance {
    private static ServerManager serverManager;
    private static ProxyType proxyType;
    private static MotdManager motdManager;

    public static void setup() {
        //No-op
    }

    public static void init(ServerManager serverManager) {
        RavelProxyInstance.serverManager = serverManager;

        if (!RavelInstance.getServer().isProxy()) {
            throw new IllegalStateException("RavelProxyInstance.init() called on non-proxy server");
        }
        proxyType = RavelInstance.getServer().isJavaProxy() ? ProxyType.JAVA : ProxyType.BEDROCK;

        serverManager.init();

        motdManager = new MotdManager();
    }

    public static ServerManager getServerManager() {
        return serverManager;
    }

    public static ProxyType getProxyType() {
        return proxyType;
    }

    public static MotdManager getMotdManager() {
        return motdManager;
    }
}
