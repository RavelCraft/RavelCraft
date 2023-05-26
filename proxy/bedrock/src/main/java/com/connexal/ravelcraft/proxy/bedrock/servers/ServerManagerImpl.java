package com.connexal.ravelcraft.proxy.bedrock.servers;

import com.connexal.ravelcraft.proxy.bedrock.BeProxy;
import com.connexal.ravelcraft.proxy.cross.servers.ServerManager;
import com.connexal.ravelcraft.shared.util.RavelServer;
import dev.waterdog.waterdogpe.network.serverinfo.BedrockServerInfo;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ServerManagerImpl extends ServerManager {
    @Override
    public void registerServer(RavelServer server) {
        ServerInfo info = new BedrockServerInfo(server.getIdentifier(), new InetSocketAddress(server.getAddress(), server.getPort()), null);
        BeProxy.getServer().registerServerInfo(info);
    }

    @Override
    public void unregisterServer(RavelServer server) {
        BeProxy.getServer().removeServerInfo(server.getIdentifier());
    }

    @Override
    public void unregisterAllServers() {
        List<ServerInfo> removeList = new ArrayList<>(BeProxy.getServer().getServers());

        for (ServerInfo server : removeList) {
            BeProxy.getServer().removeServerInfo(server.getServerName());
        }
    }
}
