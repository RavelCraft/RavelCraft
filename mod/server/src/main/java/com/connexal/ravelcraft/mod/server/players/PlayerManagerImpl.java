package com.connexal.ravelcraft.mod.server.players;

import com.connexal.ravelcraft.mod.server.RavelModServer;
import com.connexal.ravelcraft.mod.server.util.SkinApplier;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.messaging.MessagingConstants;
import com.connexal.ravelcraft.shared.players.PlayerManager;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerManagerImpl extends PlayerManager {
    @Override
    public void init() {
        super.init();

        this.messager.registerCommandHandler(MessagingCommand.PLAYER_SKIN_UPDATE, this::playerSkinUpdated);
    }

    @Override
    public void messagingConnected(RavelServer server) {
    }

    @Override
    protected boolean setServerInternal(RavelPlayer player, RavelServer server) {
        CompletableFuture<String[]> future = this.messager.sendCommandWithResponse(player.getOwnerProxy(), MessagingCommand.PROXY_TRANSFER_PLAYER, player.getUniqueID().toString(), server.name());
        if (future == null) {
            return false;
        }

        String[] response = future.join();
        if (response == null || response.length != 1) {
            return false;
        }

        return response[0].equals(MessagingConstants.COMMAND_SUCCESS);
    }

    private String[] playerSkinUpdated(RavelServer source, String[] args) {
        if (args.length != 3) {
            return null;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(args[0]);
        } catch (IllegalArgumentException e) {
            return null;
        }

        ServerPlayerEntity player = RavelModServer.getServer().getPlayerManager().getPlayer(uuid);
        if (player == null) {
            RavelInstance.getLogger().warning("Received skin update for player " + uuid + " but they are not online");
            return null;
        }

        SkinApplier.applySkin(player, args[1], args[2]);
        RavelInstance.getLogger().info("Applied new skin for player " + uuid);

        return null;
    }
}
