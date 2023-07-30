package com.connexal.ravelcraft.proxy.java.players;

import com.connexal.ravelcraft.proxy.cross.players.ProxyPlayerManager;
import com.connexal.ravelcraft.proxy.java.JeProxy;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.players.RavelRank;
import com.connexal.ravelcraft.shared.util.ChatColor;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class VelocityPlayerManager extends ProxyPlayerManager {
    @Override
    protected void playerJoinedProxyCommand(UUID uuid, String name) {
        this.playerJoinedInternal(new VelocityBedrockRavelPlayer(uuid, name));
    }

    @Override
    protected void playerLeftProxyCommand(UUID uuid) {
        this.playerLeftInternal(uuid);
    }

    @Override
    protected boolean transferPlayerInternal(RavelPlayer player, RavelServer server) {
        Optional<Player> optionalPlayer = JeProxy.getServer().getPlayer(player.getUniqueID());
        if (optionalPlayer.isEmpty()) {
            RavelInstance.getLogger().error("Unable to transfer player to server! The player doesn't exist on the proxy!");
            return false;
        }

        RegisteredServer registeredServer = JeProxy.getServer().getServer(server.getIdentifier()).orElse(null);
        if (registeredServer == null) {
            RavelInstance.getLogger().error("Unable to transfer player to server! The server doesn't exist!");
            return false;
        }

        Player velocityPlayer = optionalPlayer.get();
        CompletableFuture<ConnectionRequestBuilder.Result> future = velocityPlayer.createConnectionRequest(registeredServer).connect();
        return future.join().isSuccessful();
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
            Player bedrockPlayer = ((VelocityJavaRavelPlayer) player).getPlayer();
            bedrockPlayer.disconnect(Component.text(ChatColor.RED + "You were kicked: " + reason));
            RavelInstance.getLogger().info("Kicked " + player.getName() + " from the proxy");

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
