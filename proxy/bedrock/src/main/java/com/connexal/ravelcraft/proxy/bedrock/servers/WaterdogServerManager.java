package com.connexal.ravelcraft.proxy.bedrock.servers;

import com.connexal.ravelcraft.proxy.bedrock.BeProxy;
import com.connexal.ravelcraft.proxy.cross.servers.ServerManager;
import com.connexal.ravelcraft.shared.server.util.server.RavelServer;
import dev.waterdog.waterdogpe.network.connection.handler.IReconnectHandler;
import dev.waterdog.waterdogpe.network.connection.handler.ReconnectReason;
import dev.waterdog.waterdogpe.network.serverinfo.BedrockServerInfo;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class WaterdogServerManager extends ServerManager {
    private ServerInfo getServerInfo(RavelServer server) {
        return new BedrockServerInfo(server.getIdentifier(), new InetSocketAddress(server.getAddress(), server.getPort()), null);
    }

    @Override
    protected void additionalInit() {
        BeProxy.getServer().setJoinHandler(proxiedPlayer -> this.getServerInfo(RavelServer.DEFAULT_SERVER));

        BeProxy.getServer().setReconnectHandler(new IReconnectHandler() {
            @Override
            public ServerInfo getFallbackServer(ProxiedPlayer player, ServerInfo oldServer, ReconnectReason reason, String kickMessage) {
                if (oldServer.getServerName().equals(RavelServer.DEFAULT_SERVER.getIdentifier())) { //Kicked from the lobby
                    return null;
                }

                return getServerInfo(RavelServer.DEFAULT_SERVER);
            }
        });
    }

    @Override
    public void registerServer(RavelServer server) {
        ServerInfo info = this.getServerInfo(server);
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
