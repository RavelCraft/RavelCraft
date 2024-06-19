package com.connexal.ravelcraft.proxy.cross.players;

import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.server.messaging.MessagingConstants;
import com.connexal.ravelcraft.shared.server.players.PlayerManager;
import com.connexal.ravelcraft.shared.server.players.RavelPlayer;
import com.connexal.ravelcraft.shared.server.util.server.RavelServer;
import com.connexal.ravelcraft.shared.all.text.RavelText;

import java.util.UUID;

public abstract class ProxyPlayerManager extends PlayerManager {
    @Override
    public void init() {
        super.init();

        this.messager.registerCommandHandler(MessagingCommand.PROXY_PLAYER_JOINED, this::playerJoinedProxyCommand);
        this.messager.registerCommandHandler(MessagingCommand.PROXY_PLAYER_LEFT, this::playerLeftProxyCommand);
        this.messager.registerCommandHandler(MessagingCommand.PROXY_SEND_MESSAGE, this::proxySendMessage);
        this.messager.registerCommandHandler(MessagingCommand.PROXY_TRANSFER_PLAYER, this::proxyTransferPlayer);
        this.messager.registerCommandHandler(MessagingCommand.PROXY_TRANSFER_PLAYER_COMPLETE, this::playerTransferComplete);
    }

    @Override
    public boolean kick(RavelPlayer player, String reason, boolean network) {
        if (!network) {
            String[] response = this.messager.sendCommandWithResponse(player.getServer(), MessagingCommand.PLAYER_KICK, player.getUniqueID().toString(), reason);
            if (response == null || response.length != 1 || !response[0].equals(MessagingConstants.COMMAND_SUCCESS)) {
                RavelInstance.getLogger().error("Unable to kick " + player.getName() + " from backend server " + player.getServer());
                return false;
            }

            return true;
        }

        if (player.getOwnerProxy() != RavelInstance.getServer()) {
            String[] response = this.messager.sendCommandWithResponse(player.getOwnerProxy(), MessagingCommand.PLAYER_KICK, player.getUniqueID().toString(), reason);
            if (response == null || response.length != 1 || !response[0].equals(MessagingConstants.COMMAND_SUCCESS)) {
                RavelInstance.getLogger().error("Unable to kick " + player.getName() + " from proxy " + player.getOwnerProxy());
                return false;
            }

            return true;
        }

        return this.kickInternal(player, reason);
    }

    protected abstract boolean kickInternal(RavelPlayer player, String reason);

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

        return new String[] {MessagingConstants.COMMAND_SUCCESS};
    }

    private String[] playerTransferComplete(RavelServer source, String[] args) {
        if (args.length != 2) {
            RavelInstance.getLogger().error("Invalid number of arguments for playerTransferComplete command!");
            return null;
        }

        UUID uuid = UUID.fromString(args[0]);
        RavelPlayer player = RavelInstance.getPlayerManager().getPlayer(uuid);
        if (player == null) {
            RavelInstance.getLogger().error("Unable to locally complete transfer for unknown player!");
            return null;
        }

        RavelServer target;
        try {
            target = RavelServer.valueOf(args[1]);
        } catch (IllegalArgumentException e) {
            RavelInstance.getLogger().error("Unable to locally complete transfer to unknown server!");
            return null;
        }

        player.setServer(target);
        return null;
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

        RavelText message;
        try {
            message = RavelText.valueOf(args[1]);
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
