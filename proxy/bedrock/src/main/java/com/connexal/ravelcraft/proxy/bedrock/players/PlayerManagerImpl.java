package com.connexal.ravelcraft.proxy.bedrock.players;

import com.connexal.ravelcraft.proxy.bedrock.BeProxy;
import com.connexal.ravelcraft.proxy.cross.players.ProxyPlayerManagerImpl;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.players.RavelRank;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

import java.util.UUID;

public class PlayerManagerImpl extends ProxyPlayerManagerImpl {
    @Override
    protected void playerJoinedProxyCommand(UUID uuid, String name) {
        this.playerJoinedInternal(new JavaRavelPlayerImpl(uuid, name));
    }

    @Override
    protected void playerLeftProxyCommand(UUID uuid) {
        this.playerLeftInternal(uuid);
    }

    @Override
    protected boolean transferPlayerInternal(RavelPlayer player, RavelServer server) {
        ProxiedPlayer proxyPlayer = BeProxy.getServer().getPlayer(player.getUniqueID());
        if (proxyPlayer == null) {
            return false;
        }

        ServerInfo serverInfo = BeProxy.getServer().getServerInfo(server.getIdentifier());
        if (serverInfo == null) {
            return false;
        }

        proxyPlayer.redirectServer(serverInfo);
        player.setServer(server);
        return true;
    }

    @Override
    protected void playerRankChanged(RavelPlayer player, RavelRank rank) {
        //Nothing. I think?
    }

    @Override
    public void kick(RavelPlayer player, String reason, boolean network) {
        //TODO: Implement
    }
}
