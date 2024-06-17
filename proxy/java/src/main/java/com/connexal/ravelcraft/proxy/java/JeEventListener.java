package com.connexal.ravelcraft.proxy.java;

import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.proxy.cross.servers.ban.BanManager;
import com.connexal.ravelcraft.proxy.cross.servers.maintenance.MaintenanceManager;
import com.connexal.ravelcraft.proxy.cross.servers.whitelist.WhitelistManager;
import com.connexal.ravelcraft.proxy.java.players.VelocityJavaRavelPlayer;
import com.connexal.ravelcraft.proxy.java.website.endpoints.api.AuthEndpoint;
import com.connexal.ravelcraft.shared.BuildConstants;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.Messager;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.ChatColor;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import com.connexal.ravelcraft.shared.util.text.InitText;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import com.velocitypowered.api.util.GameProfile;
import net.kyori.adventure.text.Component;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.*;

public class JeEventListener {
    private final Map<UUID, RavelServer> playerServerMap = new HashMap<>();
    private final List<UUID> verifiedCrackedUsers = new ArrayList<>();

    private final ServerPing.SamplePlayer[] samplePlayers;
    private final Favicon emptyFavicon;
    private Favicon serverFavicon;

    public JeEventListener() {
        UUID emptyUUID = new UUID(0, 0);
        emptyFavicon = Favicon.create(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));

        try {
            serverFavicon = Favicon.create(Paths.get("server-icon.png"));
        } catch (IOException e) {
            RavelInstance.getLogger().error("Failed to load server icon.", e);
            serverFavicon = emptyFavicon;
        }

        samplePlayers = new ServerPing.SamplePlayer[5];
        samplePlayers[0] = new ServerPing.SamplePlayer(ChatColor.GREEN + "---------- " + BuildConstants.NAME + " ----------", emptyUUID);
        samplePlayers[1] = new ServerPing.SamplePlayer(ChatColor.YELLOW + "          A friendly community server!", emptyUUID);
        samplePlayers[2] = new ServerPing.SamplePlayer("", emptyUUID);
        samplePlayers[3] = new ServerPing.SamplePlayer(ChatColor.YELLOW + "            Maintained with " + ChatColor.RED + "❤" + ChatColor.YELLOW + " by Alex!", emptyUUID);
        samplePlayers[4] = new ServerPing.SamplePlayer(ChatColor.GREEN + "-------------------------------------", emptyUUID);
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onPreLoginEvent(PreLoginEvent event) {
        InetSocketAddress connectSocket = event.getConnection().getVirtualHost().orElse(null);
        String joinAddress = connectSocket == null ? BuildConstants.SERVER_IP : connectSocket.getHostString();

        RavelServer selectedServer = RavelServer.getServerByAddress(joinAddress);
        if (selectedServer == null) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied(Component.text("This server is private!\nIf you think you should be able to join, you know how to contact us!")));
            return;
        }
        playerServerMap.put(event.getUniqueId(), selectedServer);

        if (event.getUsername().startsWith("*")) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.forceOfflineMode());
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onGameProfileRequest(GameProfileRequestEvent event) {
        if (event.getUsername().startsWith("*")) {
            GameProfile gameProfile = AuthEndpoint.getCrackedProfile(event.getUsername());
            if (gameProfile != null) {
                this.verifiedCrackedUsers.add(gameProfile.getId());
            }
            event.setGameProfile(gameProfile);
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onLoginEvent(LoginEvent event) {
        Messager messager = RavelInstance.getMessager();
        if (!messager.attemptConnect() || !messager.otherProxyConnected()) {
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text("Network IPC connection establishment failed. Contact the server administrator.")));
            return;
        }

        //Cracked player check
        if (event.getPlayer().getUsername().startsWith("*")) {
            if (!this.verifiedCrackedUsers.remove(event.getPlayer().getUniqueId())) {
                event.setResult(ResultedEvent.ComponentResult.denied(Component.text("Failed to login! Please try again later.")));
                return;
            }
        }

        RavelPlayer player = new VelocityJavaRavelPlayer(event.getPlayer());

        //Whitelist check first
        if (!RavelProxyInstance.getWhitelistManager().isWhitelisted(player.getUniqueID())) {
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text(InitText.NOT_WHITELISTED)));
            return;
        }

        //Then a ban check
        BanManager.BanData banData = RavelProxyInstance.getBanManager().isBanned(player.getUniqueID());
        if (banData != null) {
            String message = BanManager.generateBanString(banData.end(), banData.reason());
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text(message)));
            return;
        }

        //And a maintenance check
        if (RavelProxyInstance.getMaintenanceManager().isEnabled()) {
            if (!RavelProxyInstance.getMaintenanceManager().canBypass(player)) {
                event.setResult(ResultedEvent.ComponentResult.denied(Component.text(InitText.MAINTENANCE)));
                return;
            }
        }

        //Finally, check if the server is full
        if (RavelInstance.getPlayerManager().getOnlineCount() >= BuildConstants.MAX_PLAYERS) {
            event.setResult(ResultedEvent.ComponentResult.denied(Component.text(InitText.SERVER_FULL)));
            return;
        }

        RavelInstance.getPlayerManager().applyPlayerRank(player, player.getRank());
        RavelInstance.getPlayerManager().playerJoined(player);
        event.setResult(ResultedEvent.ComponentResult.allowed());
    }

    @Subscribe
    public void onDisconnectEvent(DisconnectEvent event) {
        RavelInstance.getPlayerManager().playerLeft(event.getPlayer().getUniqueId());
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onPlayerKickEvent(KickedFromServerEvent event) {
        Player player = event.getPlayer();

        if (event.getServer().getServerInfo().getName().equals(RavelServer.DEFAULT_SERVER.getIdentifier())) { //Kicked from lobby anyway
            return;
        }

        Optional<RegisteredServer> lobby = JeProxy.getServer().getServer(RavelServer.DEFAULT_SERVER.getIdentifier());
        if (lobby.isPresent()) {
            RavelInstance.getLogger().info("Redirecting " + player.getUsername() + " to lobby, because of kick.");

            event.setResult(KickedFromServerEvent.RedirectPlayer.create(lobby.get()));
        }
    }


    @Subscribe
    public void onPlayerAskTransfer(ServerPreConnectEvent event) {
        //Pre transfer checks...

        RavelServer server = playerServerMap.remove(event.getPlayer().getUniqueId());
        if (server == null) {
            Optional<RegisteredServer> optionalServer = event.getResult().getServer();
            if (optionalServer.isEmpty()) {
                RavelInstance.getLogger().error("Failed to find player server!");
                return;
            }
            RegisteredServer serverInfo = optionalServer.get();

            try {
                server = RavelServer.valueOf(serverInfo.getServerInfo().getName().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                RavelInstance.getLogger().error("Failed to find player server!", e);
                return;
            }
        } else {
            Optional<RegisteredServer> optional = JeProxy.getServer().getServer(server.getIdentifier());
            if (optional.isPresent()) {
                event.setResult(ServerPreConnectEvent.ServerResult.allowed(optional.get()));
            } else {
                RavelInstance.getLogger().error("Failed to find server: " + server.getIdentifier());
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
            }
        }

        //Check if player is whitelisted on the backend server
        WhitelistManager whitelistManager = RavelProxyInstance.getWhitelistManager();
        if (whitelistManager.isEnabled(server)) {
            if (!whitelistManager.isWhitelisted(event.getPlayer().getUniqueId(), server)) {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                RavelPlayer player = RavelInstance.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
                player.sendMessage(Text.PLAYERS_NOT_WHITELISTED_BACKEND, server.getName());
                return;
            }
        }

        MaintenanceManager maintenanceManager = RavelProxyInstance.getMaintenanceManager();
        if (maintenanceManager.isEnabled(server)) {
            RavelPlayer player = RavelInstance.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());

            if (!maintenanceManager.canBypass(player)) {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                player.sendMessage(Text.PLAYERS_MAINTENANCE);
            }
        }
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


    @Subscribe(order = PostOrder.FIRST)
    public void onProxyPingEvent(ProxyPingEvent event) {
        String host = RavelServer.JE_PROXY.getAddress();
        if (event.getConnection().getVirtualHost().isPresent()) {
            host = event.getConnection().getVirtualHost().get().getHostString();
        }

        boolean isTestServer = false;
        for (String server : BuildConstants.TEST_IPS) {
            if (host.equals(server)) {
                isTestServer = true;
                break;
            }
        }

        RavelServer pingedServer = RavelServer.getServerByAddress(host);
        if (!isTestServer) {
            if (pingedServer == null) {
                ServerPing.Builder serverPing = ServerPing.builder()
                        .onlinePlayers(0)
                        .maximumPlayers(0)
                        .description(Component.text("If you think you should be able to join, you know how to contact us!"))
                        .favicon(emptyFavicon)
                        .version(new ServerPing.Version(0, ChatColor.RED + "You can't join this server!"));

                event.setPing(serverPing.build());
                return;
            }
        }

        if (RavelProxyInstance.getMaintenanceManager().isEnabled()) {
            ServerPing.Builder serverPing = ServerPing.builder()
                    .onlinePlayers(RavelInstance.getPlayerManager().getOnlineCount())
                    .maximumPlayers(BuildConstants.MAX_PLAYERS)
                    .samplePlayers(samplePlayers)
                    .version(new ServerPing.Version(0, ChatColor.RED + "You need permissions to join!"))
                    .favicon(serverFavicon)
                    .description(Component.text(ChatColor.AQUA + "The server is currently under maintenance...\nIf this message stays here too long, please talk!"));

            event.setPing(serverPing.build());
            return;
        }

        ServerPing.Builder serverPing = ServerPing.builder()
                .onlinePlayers(RavelInstance.getPlayerManager().getOnlineCount())
                .maximumPlayers(BuildConstants.MAX_PLAYERS)
                .version(new ServerPing.Version(event.getPing().getVersion().getProtocol(), ChatColor.RED + "1.8.9 to 1.20.x+"))
                .favicon(serverFavicon)
                .samplePlayers(samplePlayers);

        if (isTestServer) {
            serverPing.description(Component.text(ChatColor.AQUA + "RavelCraft test instance!\n" + ChatColor.YELLOW + "v" + BuildConstants.VERSION + " @" + host));
        } else if (pingedServer.isProxy()) {
            serverPing.description(Component.text(ChatColor.GREEN + "Welcome to the " + ChatColor.RED + BuildConstants.NAME + "!\n" + ChatColor.YELLOW + RavelProxyInstance.getMotdManager().getMotd()));
        } else {
            serverPing.description(Component.text(ChatColor.GREEN + BuildConstants.NAME + " " + ChatColor.RED + pingedServer.getName() + " server!\n" + ChatColor.YELLOW + RavelProxyInstance.getMotdManager().getMotd()));
        }

        event.setPing(serverPing.build());
    }
}
