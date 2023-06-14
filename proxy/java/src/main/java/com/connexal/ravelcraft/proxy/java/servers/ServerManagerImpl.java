package com.connexal.ravelcraft.proxy.java.servers;

import com.connexal.ravelcraft.proxy.cross.servers.ServerManager;
import com.connexal.ravelcraft.proxy.java.JeProxy;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ServerManagerImpl extends ServerManager {
    private ServerInfo constructInfo(RavelServer server) {
        return new ServerInfo(server.getIdentifier(), new InetSocketAddress(server.getAddress(), server.getPort()));
    }

    @Override
    public void registerServer(RavelServer server) {
        JeProxy.getServer().registerServer(this.constructInfo(server));
    }

    @Override
    public void unregisterServer(RavelServer server) {
        JeProxy.getServer().getServer(server.getIdentifier())
                .ifPresent(registeredServer -> JeProxy.getServer().unregisterServer(registeredServer.getServerInfo()));
    }

    @Override
    public void unregisterAllServers() {
        List<ServerInfo> removeList = new ArrayList<>();
        for (RegisteredServer server : JeProxy.getServer().getAllServers()) {
            removeList.add(server.getServerInfo());
        }

        for (ServerInfo server : removeList) {
            JeProxy.getServer().unregisterServer(server);
        }
    }
}
