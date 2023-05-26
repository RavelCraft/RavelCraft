package com.connexal.ravelcraft.proxy.cross.servers;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.util.RavelServer;

public abstract class ServerManager {
    public void init() {
        this.unregisterAllServers();

        int registered = 0;
        for (RavelServer server : RavelServer.values()) {
            if (server.isProxy()) {
                continue;
            }

            this.registerServer(server);
            registered++;
        }

        RavelInstance.getLogger().info("Registered " + registered + " servers!");
    }

    public abstract void registerServer(RavelServer server);

    public abstract void unregisterServer(RavelServer server);

    public abstract void unregisterAllServers();
}
