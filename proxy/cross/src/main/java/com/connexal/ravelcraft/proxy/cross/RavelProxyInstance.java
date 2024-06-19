package com.connexal.ravelcraft.proxy.cross;

import com.connexal.ravelcraft.proxy.cross.servers.MotdManager;
import com.connexal.ravelcraft.proxy.cross.servers.ServerManager;
import com.connexal.ravelcraft.proxy.cross.servers.ban.BanManager;
import com.connexal.ravelcraft.proxy.cross.servers.maintenance.MaintenanceManager;
import com.connexal.ravelcraft.proxy.cross.servers.whitelist.WhitelistManager;
import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.util.server.ProxyType;
import com.connexal.ravelcraft.shared.server.util.server.RavelServer;

public class RavelProxyInstance {
    private static ServerManager serverManager;
    private static MotdManager motdManager;
    private static WhitelistManager whitelistManager;
    private static BanManager banManager;
    private static MaintenanceManager maintenanceManager;

    private static ProxyType proxyType;
    private static RavelServer otherProxy;

    public static void init(ServerManager serverManager) {
        RavelProxyInstance.serverManager = serverManager;

        if (!RavelInstance.getServer().isProxy()) {
            throw new IllegalStateException("RavelProxyInstance.init() called on non-proxy server");
        }
        proxyType = RavelInstance.getServer().isJavaProxy() ? ProxyType.JAVA : ProxyType.BEDROCK;
        otherProxy = RavelInstance.getServer().isJavaProxy() ? RavelServer.BE_PROXY : RavelServer.JE_PROXY;

        serverManager.init();

        motdManager = new MotdManager();
        whitelistManager = WhitelistManager.create();
        banManager = BanManager.create();
        maintenanceManager = MaintenanceManager.create();
    }

    public static ProxyType getProxyType() {
        return proxyType;
    }

    public static RavelServer getOtherProxy() {
        return otherProxy;
    }


    public static ServerManager getServerManager() {
        return serverManager;
    }

    public static MotdManager getMotdManager() {
        return motdManager;
    }

    public static WhitelistManager getWhitelistManager() {
        return whitelistManager;
    }

    public static BanManager getBanManager() {
        return banManager;
    }

    public static MaintenanceManager getMaintenanceManager() {
        return maintenanceManager;
    }
}
