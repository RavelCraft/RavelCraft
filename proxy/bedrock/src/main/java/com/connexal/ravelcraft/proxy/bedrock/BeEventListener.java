package com.connexal.ravelcraft.proxy.bedrock;

import com.connexal.ravelcraft.shared.RavelInstance;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.event.defaults.PlayerLoginEvent;

public class BeEventListener {
    public BeEventListener() {
        ProxyServer server = BeProxy.getServer();
        server.getEventManager().subscribe(PlayerLoginEvent.class, this::onPlayerLogin);
    }

    private void onPlayerLogin(PlayerLoginEvent event) {
        if (!RavelInstance.getMessager().attemptConnect()) {
            event.getPlayer().disconnect("Network IPC connection establishment failed. Contact the server administrator.");
            event.setCancelled(true);
            return;
        }

        //TODO: Check if player is banned

        //TODO: Check if player is whitelisted

        //TODO: Tell player manager about join
        //TODO: Tell java proxy about join
    }
}
