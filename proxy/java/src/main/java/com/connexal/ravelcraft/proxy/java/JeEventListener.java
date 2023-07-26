package com.connexal.ravelcraft.proxy.java;

import com.connexal.ravelcraft.proxy.java.players.VelocityJavaRavelPlayer;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import net.kyori.adventure.text.Component;

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

        //TODO: Check if player is banned

        //TODO: Check if player is whitelisted

        RavelInstance.getPlayerManager().playerJoined(player);
    }

    @Subscribe
    public void onDisconnectEvent(DisconnectEvent event) {
        RavelInstance.getPlayerManager().playerLeft(event.getPlayer().getUniqueId());
    }
}
