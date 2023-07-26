package com.connexal.ravelcraft.proxy.bedrock;

import com.connexal.ravelcraft.proxy.bedrock.players.WaterdogBedrockRavelPlayer;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.uuid.UUIDTools;
import com.nimbusds.jwt.SignedJWT;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.event.defaults.PlayerAuthenticatedEvent;
import dev.waterdog.waterdogpe.event.defaults.PlayerDisconnectedEvent;
import dev.waterdog.waterdogpe.event.defaults.PlayerLoginEvent;
import dev.waterdog.waterdogpe.event.defaults.PreClientDataSetEvent;
import dev.waterdog.waterdogpe.network.protocol.user.HandshakeUtils;

import java.util.List;
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
        //Upload player skin to the Geyser global API
        SignedJWT signedClientData = HandshakeUtils.createExtraData(event.getKeyPair(), event.getExtraData());
        SignedJWT signedExtraData = HandshakeUtils.encodeJWT(event.getKeyPair(), event.getClientData());
        BeProxy.getSkinUploadManager().uploadSkin(List.of(signedClientData), signedExtraData.getParsedString());

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
        RavelPlayer player = new WaterdogBedrockRavelPlayer(event.getPlayer());
        RavelInstance.getPlayerManager().applyPlayerRank(player, player.getRank());

        //TODO: Check if player is banned

        //TODO: Check if player is whitelisted

        RavelInstance.getPlayerManager().playerJoined(player);
    }

    private void onPlayerLeave(PlayerDisconnectedEvent event) {
        UUID uuid = UUIDTools.getJavaUUIDFromXUID(event.getPlayer().getXuid());

        RavelInstance.getPlayerManager().playerLeft(uuid);
    }
}
