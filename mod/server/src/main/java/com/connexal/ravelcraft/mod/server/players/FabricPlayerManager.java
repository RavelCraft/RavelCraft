package com.connexal.ravelcraft.mod.server.players;

import com.connexal.ravelcraft.mod.server.RavelModServer;
import com.connexal.ravelcraft.mod.server.util.SkinApplier;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.messaging.MessagingConstants;
import com.connexal.ravelcraft.shared.players.PlayerManager;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.players.RavelRank;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

public class FabricPlayerManager extends PlayerManager {
    @Override
    public void init() {
        super.init();

        this.messager.registerCommandHandler(MessagingCommand.PLAYER_SKIN_UPDATE, this::playerSkinUpdated);
    }

    @Override
    public void messagingConnected(RavelServer server) {
    }

    @Override
    protected boolean transferPlayerInternal(RavelPlayer player, RavelServer server) {
        //Will never be run
        throw new IllegalStateException("This method should never be run on the server");
    }

    @Override
    public void applyPlayerRank(RavelPlayer player, RavelRank rank) {
        ServerPlayerEntity serverPlayer = ((FabricRavelPlayer) player).getPlayer();

        if (rank.isOperator()) {
            RavelModServer.getServer().getPlayerManager().addToOperators(serverPlayer.getGameProfile());
        } else {
            RavelModServer.getServer().getPlayerManager().removeFromOperators(serverPlayer.getGameProfile());
        }
    }

    @Override
    protected void playerRankChanged(RavelPlayer player, RavelRank rank) {
        player.updateDisplayName();
        this.applyPlayerRank(player, rank);
    }

    @Override
    public boolean kick(RavelPlayer player, String reason, boolean network) {
        if (network) {
            String[] response = this.messager.sendCommandWithResponse(player.getOwnerProxy(), MessagingCommand.PLAYER_KICK, player.getUniqueID().toString(), reason);
            if (response == null || response.length != 1 || !response[0].equals(MessagingConstants.COMMAND_SUCCESS)) {
                RavelInstance.getLogger().error("Unable to kick " + player.getName() + " from proxy " + player.getOwnerProxy());
                return false;
            }

            return true;
        }

        ServerPlayerEntity fabricPlayer = ((FabricRavelPlayer) player).getPlayer();
        fabricPlayer.networkHandler.disconnect(Text.of(reason));
        RavelInstance.getLogger().info("Kicked " + player.getName() + " from the server");

        return true;
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
