package com.connexal.ravelcraft.proxy.java;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import net.kyori.adventure.text.Component;

public class JeEventListener {
    @Subscribe(order = PostOrder.FIRST)
    public void onLoginEvent(LoginEvent event) {
        if (!RavelInstance.getMessager().attemptConnect()) {
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text("Network IPC connection establishment failed. Contact the server administrator.")));
            return;
        }

        //TODO: Check if player is banned

        //TODO: Check if player is whitelisted

        //TODO: Tell player manager about join
        //TODO: Tell bedrock proxy about join
    }
}
