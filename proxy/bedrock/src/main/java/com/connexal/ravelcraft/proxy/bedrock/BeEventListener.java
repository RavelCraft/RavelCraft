package com.connexal.ravelcraft.proxy.bedrock;

import com.connexal.ravelcraft.proxy.bedrock.players.BedrockRavelPlayerImpl;
import com.connexal.ravelcraft.proxy.cross.players.ProxyRavelPlayer;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.UUIDTools;
import com.connexal.ravelcraft.shared.util.text.Text;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.event.defaults.PlayerAuthenticatedEvent;
import dev.waterdog.waterdogpe.event.defaults.PlayerDisconnectedEvent;
import dev.waterdog.waterdogpe.event.defaults.PlayerLoginEvent;
import dev.waterdog.waterdogpe.event.defaults.PreClientDataSetEvent;

import java.util.UUID;

public class BeEventListener {
    public BeEventListener() {
        ProxyServer server = BeProxy.getServer();

        server.getEventManager().subscribe(PreClientDataSetEvent.class, this::onPreLogin);
        server.getEventManager().subscribe(PlayerAuthenticatedEvent.class, this::onPlayerAuthenticate);
        server.getEventManager().subscribe(PlayerLoginEvent.class, this::onPlayerJoin);
        server.getEventManager().subscribe(PlayerDisconnectedEvent.class, this::onPlayerLeave);
    }

    private void onPreLogin(PreClientDataSetEvent event) {
        //Change username to a valid Java username
        String username = RavelPlayer.BEDROCK_PREFIX + event.getExtraData().get("displayName").getAsString();
        if (username.contains(" ")) {
            username = username.replace(" ", RavelPlayer.BEDROCK_SPACE_REPLACEMENT);
        }

        event.getClientData().remove("ThirdPartyName");
        event.getClientData().addProperty("ThirdPartyName", username);
        event.getExtraData().remove("displayName");
        event.getExtraData().addProperty("displayName", username);

        //TODO: Note to self, client data contains a property called "ServerAddress" that contains something like "address:port". Can be used for forced hosts.
        //TODO: Disallow players from joining on the wrong address
    }

    private void onPlayerAuthenticate(PlayerAuthenticatedEvent event) {
        if (!RavelInstance.getMessager().attemptConnect()) {
            event.setCancelReason("Network IPC connection establishment failed. Contact the server administrator.");
            event.setCancelled(true);
        }
    }

    private void onPlayerJoin(PlayerLoginEvent event) {
        ProxyRavelPlayer player = new BedrockRavelPlayerImpl(event.getPlayer());

        //TODO: Check if player is banned

        //TODO: Check if player is whitelisted

        RavelInstance.getPlayerManager().playerJoined(player);
    }

    private void onPlayerLeave(PlayerDisconnectedEvent event) {
        long xuid;
        try {
            xuid = Long.parseLong(event.getPlayer().getXuid());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Unable to parse Bedrock player's XUID", e);
        }
        UUID uuid = UUIDTools.getJavaUUIDFromXUID(xuid);

        RavelInstance.getPlayerManager().playerLeft(uuid);
    }
}
