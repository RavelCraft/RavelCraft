package com.connexal.ravelcraft.proxy.bedrock;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.event.defaults.PlayerAuthenticatedEvent;
import dev.waterdog.waterdogpe.event.defaults.PreClientDataSetEvent;

import java.util.UUID;

public class BeEventListener {
    public BeEventListener() {
        ProxyServer server = BeProxy.getServer();

        server.getEventManager().subscribe(PreClientDataSetEvent.class, this::onPreLogin);
        server.getEventManager().subscribe(PlayerAuthenticatedEvent.class, this::onPlayerAuthenticate);
    }

    private void onPreLogin(PreClientDataSetEvent event) {
        String username = RavelPlayer.BEDROCK_PREFIX + event.getExtraData().get("displayName").getAsString();

        event.getClientData().remove("ThirdPartyName");
        event.getClientData().addProperty("ThirdPartyName", username);
        event.getExtraData().remove("displayName");
        event.getExtraData().addProperty("displayName", username);

        //TODO: Note to self, client data contains a property called "ServerAddress" that contains something like "address:port". Can be used for forced hosts.
    }

    private void onPlayerAuthenticate(PlayerAuthenticatedEvent event) {
        if (!RavelInstance.getMessager().attemptConnect()) {
            event.setCancelReason("Network IPC connection establishment failed. Contact the server administrator.");
            event.setCancelled(true);
            return;
        }

        UUID uuid = event.getLoginData().getUuid();

        //TODO: Check if player is banned

        //TODO: Check if player is whitelisted

        //TODO: Tell player manager about join
        //TODO: Tell java proxy about join
    }
}
