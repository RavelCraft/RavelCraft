package com.connexal.ravelcraft.proxy.bedrock;

import com.connexal.ravelcraft.proxy.bedrock.players.WaterdogBedrockRavelPlayer;
import com.connexal.ravelcraft.proxy.bedrock.util.Motd;
import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.proxy.cross.servers.ban.BanManager;
import com.connexal.ravelcraft.proxy.cross.servers.maintenance.MaintenanceManager;
import com.connexal.ravelcraft.proxy.cross.servers.whitelist.WhitelistManager;
import com.connexal.ravelcraft.shared.all.Ravel;
import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.messaging.Messager;
import com.connexal.ravelcraft.shared.server.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.server.players.RavelPlayer;
import com.connexal.ravelcraft.shared.server.util.server.RavelServer;
import com.connexal.ravelcraft.shared.all.text.RavelTextHardcoded;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import com.connexal.ravelcraft.shared.server.util.uuid.UUIDTools;
import com.nimbusds.jwt.SignedJWT;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.event.defaults.*;
import dev.waterdog.waterdogpe.network.protocol.user.HandshakeUtils;
import dev.waterdog.waterdogpe.network.protocol.user.LoginData;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;

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
        //Change username to a valid Java username
        String username = RavelPlayer.BEDROCK_PREFIX + event.getExtraData().get("displayName").getAsString();
        if (username.contains(" ")) {
            username = username.replace(" ", RavelPlayer.BEDROCK_SPACE_REPLACEMENT);
        }

        event.getClientData().remove("ThirdPartyName");
        event.getClientData().addProperty("ThirdPartyName", username);
        event.getExtraData().remove("displayName");
        event.getExtraData().addProperty("displayName", username);
    }

    private void onPlayerAuthenticate(PlayerAuthenticatedEvent event) {
        Messager messager = RavelInstance.getMessager();
        if (!messager.attemptConnect() || !messager.otherProxyConnected()) {
            event.setCancelReason("Network IPC connection establishment failed. Contact the server administrator.");
            event.setCancelled(true);
        }

        LoginData loginData = event.getLoginData();

        //Check join address - kick if not allowed
        String joinAddress = loginData.getClientData().get("ServerAddress").getAsString();
        if (joinAddress.contains(":")) {
            joinAddress = joinAddress.split(":")[0];
        }
        RavelServer selectedServer = RavelServer.getServerByAddress(joinAddress);
        if (selectedServer == null) {
            event.setCancelReason("This server is private!\nIf you think you should be able to join, you know how to contact us!");
            event.setCancelled(true);
            return;
        }

        //TODO: Support forced hosts

        //Upload player skin to the Geyser global API
        SignedJWT signedClientData = HandshakeUtils.createExtraData(loginData.getKeyPair(), loginData.getExtraData());
        SignedJWT signedExtraData = HandshakeUtils.encodeJWT(loginData.getKeyPair(), loginData.getClientData());
        BeProxy.getSkinUploader().uploadSkin(List.of(signedClientData), signedExtraData.getParsedString());
    }

    private void onPlayerJoin(PlayerLoginEvent event) {
        RavelPlayer player = new WaterdogBedrockRavelPlayer(event.getPlayer());

        //Whitelist check first
        if (!RavelProxyInstance.getWhitelistManager().isWhitelisted(player.getUniqueID())) {
            event.setCancelReason(RavelTextHardcoded.NOT_WHITELISTED);
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
                event.setCancelReason(RavelTextHardcoded.MAINTENANCE);
                event.setCancelled(true);
                return;
            }
        }

        //Finally, check if the server is full
        if (RavelInstance.getPlayerManager().getOnlineCount() >= Ravel.MAX_PLAYERS) {
            event.setCancelReason(RavelTextHardcoded.SERVER_FULL);
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
                player.sendMessage(RavelText.PLAYERS_NOT_WHITELISTED_BACKEND, server.getName());
                return;
            }
        }

        MaintenanceManager maintenanceManager = RavelProxyInstance.getMaintenanceManager();
        if (maintenanceManager.isEnabled(server)) {
            UUID uuid = UUIDTools.getJavaUUIDFromXUID(event.getPlayer().getXuid());
            RavelPlayer player = RavelInstance.getPlayerManager().getPlayer(uuid);

            if (!maintenanceManager.canBypass(player)) {
                event.setCancelled(true);
                player.sendMessage(RavelText.PLAYERS_MAINTENANCE);
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
        event.setMaximumPlayerCount(Ravel.MAX_PLAYERS);

        event.setMotd(Motd.FIRST_LINE);
        event.setSubMotd(RavelProxyInstance.getMotdManager().getMotd());
    }

    private void onPlayerQuery(ProxyQueryEvent event) {
        event.setHasWhitelist(true);
        this.onPlayerPing(event);
    }
}
