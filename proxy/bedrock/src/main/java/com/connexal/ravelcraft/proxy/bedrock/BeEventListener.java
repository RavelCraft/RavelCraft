package com.connexal.ravelcraft.proxy.bedrock;

import com.connexal.ravelcraft.proxy.bedrock.players.WaterdogBedrockRavelPlayer;
import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.proxy.cross.servers.ban.BanManager;
import com.connexal.ravelcraft.proxy.cross.servers.maintenance.MaintenanceManager;
import com.connexal.ravelcraft.proxy.cross.servers.whitelist.WhitelistManager;
import com.connexal.ravelcraft.shared.BuildConstants;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.Messager;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.StringUtils;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import com.connexal.ravelcraft.shared.util.text.InitText;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.connexal.ravelcraft.shared.util.uuid.UUIDTools;
import com.nimbusds.jwt.SignedJWT;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.event.defaults.*;
import dev.waterdog.waterdogpe.network.protocol.user.HandshakeUtils;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class BeEventListener {
    public BeEventListener() {
        ProxyServer server = BeProxy.getServer();

        server.getEventManager().subscribe(PreClientDataSetEvent.class, this::onPreLogin);
        server.getEventManager().subscribe(PlayerAuthenticatedEvent.class, this::onPlayerAuthenticate);
        server.getEventManager().subscribe(PlayerLoginEvent.class, this::onPlayerJoin);
        server.getEventManager().subscribe(PlayerDisconnectedEvent.class, this::onPlayerLeave);
        server.getEventManager().subscribe(ServerTransferRequestEvent.class, this::onPlayerAskTransfer);
        server.getEventManager().subscribe(TransferCompleteEvent.class, this::onPlayerTransfer);
        server.getEventManager().subscribe(ProxyPingEvent.class, this::onPlayerPing);
        server.getEventManager().subscribe(ProxyQueryEvent.class, this::onPlayerQuery);
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

        //Note to self, client data contains a property called "ServerAddress" that contains something like "address:port". Can be used for forced hosts.
        //TODO: Disallow players from joining on the wrong address
        //TODO: Add forced hosts support
    }

    private void onPlayerAuthenticate(PlayerAuthenticatedEvent event) {
        Messager messager = RavelInstance.getMessager();
        if (!messager.attemptConnect() || !messager.otherProxyConnected()) {
            event.setCancelReason("Network IPC connection establishment failed. Contact the server administrator.");
            event.setCancelled(true);
        }
    }

    private void onPlayerJoin(PlayerLoginEvent event) {
        RavelPlayer player = new WaterdogBedrockRavelPlayer(event.getPlayer());

        //Whitelist check first
        if (!RavelProxyInstance.getWhitelistManager().isWhitelisted(player.getUniqueID())) {
            event.setCancelReason(InitText.NOT_WHITELISTED);
            event.setCancelled(true);
            return;
        }

        //Then a ban check
        BanManager.BanData banData = RavelProxyInstance.getBanManager().isBanned(player.getUniqueID());
        if (banData != null) {
            event.setCancelReason(BanManager.generateBanString(banData.end(), banData.reason()));
            event.setCancelled(true);
            return;
        }

        //And a maintenance check
        if (RavelProxyInstance.getMaintenanceManager().isEnabled()) {
            if (!RavelProxyInstance.getMaintenanceManager().canBypass(player)) {
                event.setCancelReason(InitText.MAINTENANCE);
                event.setCancelled(true);
                return;
            }
        }

        //Finally, check if the server is full
        if (RavelInstance.getPlayerManager().getOnlineCount() >= BuildConstants.MAX_PLAYERS) {
            event.setCancelReason(InitText.SERVER_FULL);
            event.setCancelled(true);
            return;
        }

        RavelInstance.getPlayerManager().applyPlayerRank(player, player.getRank());
        RavelInstance.getPlayerManager().playerJoined(player);
    }

    private void onPlayerLeave(PlayerDisconnectedEvent event) {
        UUID uuid = UUIDTools.getJavaUUIDFromXUID(event.getPlayer().getXuid());

        RavelInstance.getPlayerManager().playerLeft(uuid);
    }

    private void onPlayerAskTransfer(ServerTransferRequestEvent event) {
        ServerInfo serverInfo = event.getTargetServer();

        RavelServer server;
        try {
            server = RavelServer.valueOf(serverInfo.getServerName().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            RavelInstance.getLogger().error("Failed to find player server!", e);
            return;
        }

        WhitelistManager whitelistManager = RavelProxyInstance.getWhitelistManager();
        if (whitelistManager.isEnabled(server)) {
            UUID uuid = UUIDTools.getJavaUUIDFromXUID(event.getPlayer().getXuid());
            if (!whitelistManager.isWhitelisted(uuid, server)) {
                event.setCancelled(true);
                RavelPlayer player = RavelInstance.getPlayerManager().getPlayer(uuid);
                player.sendMessage(Text.PLAYERS_NOT_WHITELISTED_BACKEND, server.getName());
                return;
            }
        }

        MaintenanceManager maintenanceManager = RavelProxyInstance.getMaintenanceManager();
        if (maintenanceManager.isEnabled(server)) {
            UUID uuid = UUIDTools.getJavaUUIDFromXUID(event.getPlayer().getXuid());
            RavelPlayer player = RavelInstance.getPlayerManager().getPlayer(uuid);

            if (!maintenanceManager.canBypass(player)) {
                event.setCancelled(true);
                player.sendMessage(Text.PLAYERS_MAINTENANCE);
                return;
            }
        }
    }

    private void onPlayerTransfer(TransferCompleteEvent event) {
        UUID uuid = UUIDTools.getJavaUUIDFromXUID(event.getPlayer().getXuid());
        RavelPlayer player = RavelInstance.getPlayerManager().getPlayer(uuid);

        RavelServer server;
        try {
            server = RavelServer.valueOf(event.getTargetServer().getServerName().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            RavelInstance.getLogger().error("Failed to find player server!", e);
            return;
        }

        player.setServer(server);
        RavelInstance.getMessager().sendCommand(RavelServer.JE_PROXY, MessagingCommand.PROXY_TRANSFER_PLAYER_COMPLETE, player.getUniqueID().toString(), server.name());
    }

    private void onPlayerPing(ProxyPingEvent event) {
        event.setPlayers(Collections.emptyList());
        event.setPlayerCount(RavelInstance.getPlayerManager().getOnlineCount());
        event.setMaximumPlayerCount(BuildConstants.MAX_PLAYERS);

        event.setMotd("\uE015 ʀᴀᴠᴇʟᴄʀᴀғᴛ");
        event.setSubMotd("Super secret second line!");
    }

    private void onPlayerQuery(ProxyQueryEvent event) {
        event.setHasWhitelist(true);
        this.onPlayerPing(event);
    }
}
