package com.connexal.ravelcraft.proxy.bedrock.players;

import com.connexal.ravelcraft.proxy.bedrock.BeProxy;
import com.connexal.ravelcraft.proxy.cross.players.ProxyPlayerManager;
import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.players.RavelPlayer;
import com.connexal.ravelcraft.shared.server.players.RavelRank;
import com.connexal.ravelcraft.shared.all.util.ChatColor;
import com.connexal.ravelcraft.shared.server.util.server.RavelServer;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

import java.util.UUID;

public class WaterdogPlayerManager extends ProxyPlayerManager {
    @Override
    protected void playerJoinedProxyCommand(UUID uuid, String name) {
        this.playerJoinedInternal(new WaterdogJavaRavelPlayer(uuid, name));
    }

    @Override
    protected void playerLeftProxyCommand(UUID uuid) {
        this.playerLeftInternal(uuid);
    }

    @Override
    protected boolean transferPlayerInternal(RavelPlayer player, RavelServer server) {
        ProxiedPlayer proxyPlayer = ((WaterdogBedrockRavelPlayer) player).getPlayer();
        if (proxyPlayer == null) {
            RavelInstance.getLogger().error("Unable to transfer player to server! The player doesn't exist on the proxy!");
            return false;
        }

        ServerInfo serverInfo = BeProxy.getServer().getServerInfo(server.getIdentifier());
        if (serverInfo == null) {
            RavelInstance.getLogger().error("Unable to transfer player to server! The server \"" + server.getIdentifier() + "\" doesn't exist!");
            return false;
        }

        //Don't use redirect here, because we want to keep the player on the proxy
        proxyPlayer.connect(serverInfo);
        return true;
    }

    @Override
    public void applyPlayerRank(RavelPlayer player, RavelRank rank) {
        //Nothing. I think?
    }

    @Override
    protected void playerRankChanged(RavelPlayer player, RavelRank rank) {
        //Nothing. I think?
    }

    @Override
    public boolean kickInternal(RavelPlayer player, String reason) {
        try {
            ProxiedPlayer bedrockPlayer = ((WaterdogBedrockRavelPlayer) player).getPlayer();
            bedrockPlayer.disconnect(ChatColor.RED + "You were kicked: " + reason);
            RavelInstance.getLogger().info("Kicked " + player.getName() + " from the proxy");

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
