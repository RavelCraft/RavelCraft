package com.connexal.ravelcraft.proxy.cross.players;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.messaging.MessagingConstants;
import com.connexal.ravelcraft.shared.players.PlayerManager;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import com.connexal.ravelcraft.shared.util.text.Text;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;

public abstract class ProxyPlayerManagerImpl extends PlayerManager {
    @Override
    public void init() {
        super.init();

        this.messager.registerCommandHandler(MessagingCommand.PROXY_PLAYER_JOINED, this::playerJoinedProxyCommand);
        this.messager.registerCommandHandler(MessagingCommand.PROXY_PLAYER_LEFT, this::playerLeftProxyCommand);
        this.messager.registerCommandHandler(MessagingCommand.PROXY_QUERY_CONNECTED, this::proxyQueryConnected);
        this.messager.registerCommandHandler(MessagingCommand.PROXY_SEND_MESSAGE, this::proxySendMessage);
        this.messager.registerCommandHandler(MessagingCommand.PROXY_TRANSFER_PLAYER, this::proxyTransferPlayer);
    }

    @Override
    public void messagingConnected(RavelServer server) {
        if (!server.isProxy() || this.messager == null) {
            return;
        }

        RavelServer otherServer;
        if (RavelInstance.getServer().isJavaProxy()) {
            otherServer = RavelServer.BE_PROXY;
        } else {
            otherServer = RavelServer.JE_PROXY;
        }
        if (server != otherServer) {
            return;
        }

        RavelInstance.getLogger().info("Querying connected server for player information");

        String[] connected = this.messager.sendCommandWithResponse(otherServer, MessagingCommand.PROXY_QUERY_CONNECTED, this.generateConnectedPlayerList());
        if (connected == null) {
            RavelInstance.getLogger().error("Unable to query connected server for player information!");
            return;
        }
        this.registerConnected(connected);
    }

    private String[] proxyTransferPlayer(RavelServer source, String[] args) {
        if (args.length != 2) {
            RavelInstance.getLogger().error("Invalid number of arguments for proxyTransferPlayer command!");
            return new String[] {MessagingConstants.COMMAND_FAILURE};
        }

        UUID uuid = UUID.fromString(args[0]);
        RavelPlayer player = RavelInstance.getPlayerManager().getPlayer(uuid);
        if (player == null) {
            RavelInstance.getLogger().error("Unable to transfer unknown player! (Requested by " + source + ")");
            return new String[] {MessagingConstants.COMMAND_FAILURE};
        }

        RavelServer target;
        try {
            target = RavelServer.valueOf(args[1]);
        } catch (IllegalArgumentException e) {
            RavelInstance.getLogger().error("Unable to transfer player to unknown server! (Requested by " + source + ")");
            return new String[] {MessagingConstants.COMMAND_FAILURE};
        }

        boolean success = this.transferPlayerToServer(player, target);
        if (!success) {
            RavelInstance.getLogger().error("Unable to transfer player to server! (Requested by " + source + ")");
            return new String[] {MessagingConstants.COMMAND_FAILURE};
        }

        player.setServer(target);

        return new String[] {MessagingConstants.COMMAND_SUCCESS};
    }

    private String[] playerJoinedProxyCommand(RavelServer source, String[] args) {
        if (args.length != 2) {
            RavelInstance.getLogger().error("Invalid number of arguments for playerJoinedProxy command!");
            return null;
        }

        UUID uuid = UUID.fromString(args[0]);
        this.playerJoinedProxyCommand(uuid, args[1]);
        return null;
    }

    protected abstract void playerJoinedProxyCommand(UUID uuid, String name);

    private String[] playerLeftProxyCommand(RavelServer source, String[] args) {
        if (args.length != 1) {
            RavelInstance.getLogger().error("Invalid number of arguments for playerLeftProxy command!");
            return null;
        }

        UUID uuid = UUID.fromString(args[0]);
        this.playerLeftProxyCommand(uuid);
        return null;
    }

    protected abstract void playerLeftProxyCommand(UUID uuid);

    private String[] generateConnectedPlayerList() {
        List<String> connected = new ArrayList<>();
        for (RavelPlayer player : RavelInstance.getPlayerManager().getConnectedPlayers()) {
            if (player.getOwnerProxy() != RavelInstance.getServer()) {
                //We're going to add all these players back again, who knows what happened to them since the servers last talked anyway
                this.playerLeftProxyCommand(null, new String[]{player.getUniqueID().toString()});

                continue;
            }

            connected.add(player.getUniqueID().toString() + "\n" + player.getName());
        }

        return connected.toArray(new String[0]);
    }

    private void registerConnected(String[] playerData) {
        for (String player : playerData) {
            this.playerJoinedProxyCommand(null, player.split("\n"));
        }
    }

    private String[] proxyQueryConnected(RavelServer source, String[] args) {
        String[] connected = this.generateConnectedPlayerList();
        this.registerConnected(args);

        return connected;
    }

    private String[] proxySendMessage(RavelServer source, String[] args) {
        if (args.length < 2) {
            RavelInstance.getLogger().error("Invalid number of arguments for proxySendMessage command!");
            return null;
        }

        UUID uuid = UUID.fromString(args[0]);
        RavelPlayer player = RavelInstance.getPlayerManager().getPlayer(uuid);
        if (player == null) {
            RavelInstance.getLogger().error("Unable to send message to unknown player! (Requested by " + source + ")");
            return null;
        }
        if (player.getOwnerProxy() != RavelInstance.getServer()) {
            RavelInstance.getLogger().error("Unable to send message to player on other proxy! (Requested by " + source + ")");
            return null;
        }

        Text message;
        try {
            message = Text.valueOf(args[1]);
        } catch (Exception e) {
            RavelInstance.getLogger().error("Unable to send unknown message to player! (Requested by " + source + ")");
            return null;
        }

        String[] formatArgs = new String[args.length - 2];
        System.arraycopy(args, 2, formatArgs, 0, formatArgs.length);

        player.sendMessage(message, formatArgs);
        return null;
    }
}
