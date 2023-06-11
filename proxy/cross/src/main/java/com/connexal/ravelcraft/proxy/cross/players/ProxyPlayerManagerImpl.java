package com.connexal.ravelcraft.proxy.cross.players;

import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.players.PlayerManager;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.RavelServer;
import com.connexal.ravelcraft.shared.util.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class ProxyPlayerManagerImpl extends PlayerManager {
    @Override
    public void init() {
        super.init();

        this.messager.registerCommandHandler(MessagingCommand.PLAYER_JOINED_PROXY, this::playerJoinedProxyCommand);
        this.messager.registerCommandHandler(MessagingCommand.PLAYER_LEFT_PROXY, this::playerLeftProxyCommand);
        this.messager.registerCommandHandler(MessagingCommand.PROXY_QUERY_CONNECTED, this::proxyQueryConnected);
        this.messager.registerCommandHandler(MessagingCommand.PROXY_SEND_MESSAGE, this::proxySendMessage);
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
        CompletableFuture<String[]> connectedFuture = this.messager.sendCommandWithResponse(otherServer, MessagingCommand.PROXY_QUERY_CONNECTED, this.generateConnectedPlayerList());
        this.registerConnected(connectedFuture.join());
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
        for (RavelPlayer genericPlayer : RavelInstance.getPlayerManager().getConnectedPlayers()) {
            if (!(genericPlayer instanceof ProxyRavelPlayer player)) {
                continue;
            }
            if (player.getOwner() != RavelProxyInstance.getProxyType()) {
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
        if (!(player instanceof ProxyRavelPlayer proxyPlayer)) {
            RavelInstance.getLogger().error("Unable to send message to non-proxy player! (Requested by " + source + ")");
            return null;
        }
        if (proxyPlayer.getOwner() != RavelProxyInstance.getProxyType()) {
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
