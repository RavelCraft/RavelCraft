package com.connexal.ravelcraft.proxy.java.players;

import com.connexal.ravelcraft.proxy.cross.players.ProxyPlayerManagerImpl;
import com.connexal.ravelcraft.proxy.java.JeProxy;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.players.RavelRank;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerManagerImpl extends ProxyPlayerManagerImpl {
    @Override
    protected void playerJoinedProxyCommand(UUID uuid, String name) {
        this.playerJoinedInternal(new BedrockRavelPlayerImpl(uuid, name));
    }

    @Override
    protected void playerLeftProxyCommand(UUID uuid) {
        this.playerLeftInternal(uuid);
    }

    @Override
    protected boolean setServerInternal(RavelPlayer player, RavelServer server) {
        Optional<Player> optionalPlayer = JeProxy.getServer().getPlayer(player.getUniqueID());
        if (optionalPlayer.isEmpty()) {
            return false;
        }

        RegisteredServer registeredServer = JeProxy.getServer().getServer(server.getIdentifier()).orElse(null);
        if (registeredServer == null) {
            return false;
        }

        Player velocityPlayer = optionalPlayer.get();
        CompletableFuture<ConnectionRequestBuilder.Result> future = velocityPlayer.createConnectionRequest(registeredServer).connect();

        return future.join().isSuccessful();
    }

    @Override
    protected void playerRankChanged(RavelPlayer player, RavelRank rank) {
        //Nothing. I think?
    }
}
