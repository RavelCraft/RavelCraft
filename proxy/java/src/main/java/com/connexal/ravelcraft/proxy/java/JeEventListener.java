package com.connexal.ravelcraft.proxy.java;

import com.connexal.ravelcraft.proxy.java.players.VelocityJavaRavelPlayer;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import com.connexal.ravelcraft.shared.util.uuid.UUIDTools;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import net.kyori.adventure.text.Component;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class JeEventListener {
    @Subscribe(order = PostOrder.FIRST)
    public void onPreLoginEvent(PreLoginEvent event) {
        //TODO: Disallow players from joining on the wrong address
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onLoginEvent(LoginEvent event) {
        if (!RavelInstance.getMessager().attemptConnect()) {
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text("Network IPC connection establishment failed. Contact the server administrator.")));
            return;
        }

        RavelPlayer player = new VelocityJavaRavelPlayer(event.getPlayer());
        RavelInstance.getPlayerManager().applyPlayerRank(player, player.getRank());

        //TODO: Check if player is banned

        //TODO: Check if player is whitelisted

        RavelInstance.getPlayerManager().playerJoined(player);
    }

    @Subscribe
    public void onDisconnectEvent(DisconnectEvent event) {
        RavelInstance.getPlayerManager().playerLeft(event.getPlayer().getUniqueId());
    }

    @Subscribe
    public void onPlayerTransfer(ServerPostConnectEvent event) {
        RavelPlayer player = RavelInstance.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());

        RavelServer server;
        try {
            Optional<ServerConnection> serverOptional = event.getPlayer().getCurrentServer();
            if (!serverOptional.isPresent()) {
                RavelInstance.getLogger().error("Failed to find player server!");
                return;
            }

            server = RavelServer.valueOf(serverOptional.get().getServerInfo().getName().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            RavelInstance.getLogger().error("Failed to find player server!", e);
            return;
        }

        player.setServer(server);
        RavelInstance.getMessager().sendCommand(RavelServer.BE_PROXY, MessagingCommand.PROXY_TRANSFER_PLAYER_COMPLETE, player.getUniqueID().toString(), server.name());
    }
}
