package com.connexal.ravelcraft.proxy.cross.servers;

import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.util.server.RavelServer;

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

        this.additionalInit();

        RavelInstance.getLogger().info("Registered " + registered + " servers!");
    }

    protected void additionalInit() {
        //Override this if you need to do anything else
    }

    public abstract void registerServer(RavelServer server);

    public abstract void unregisterServer(RavelServer server);

    public abstract void unregisterAllServers();
}
