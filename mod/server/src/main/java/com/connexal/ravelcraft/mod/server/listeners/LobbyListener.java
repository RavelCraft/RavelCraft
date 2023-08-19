package com.connexal.ravelcraft.mod.server.listeners;

import com.connexal.ravelcraft.mod.server.RavelModServer;
import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.Location;
import com.connexal.ravelcraft.mod.server.util.events.BlockEvents;
import com.connexal.ravelcraft.mod.server.util.events.EntityEvents;
import com.connexal.ravelcraft.mod.server.util.events.PlayerEvents;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.RavelRank;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

class LobbyListener {
    static void register() {
        PlayerEvents.JOINED.register(LobbyListener::spawnRedirect);
        PlayerEvents.JOINED.register(LobbyListener::builderPermissions);
        EntityEvents.DAMAGE.register(LobbyListener::damageSuppress);
        BlockEvents.PLACE.register(LobbyListener::allowBlockPlace);
        BlockEvents.BREAK.register(LobbyListener::allowBlockBreak);
        PlayerEvents.INTERACT_BLOCK.register(LobbyListener::disableBlcokInteractions);
        PlayerEvents.INTERACT_ITEM.register(LobbyListener::disableItemInteractions);
        PlayerEvents.INTERACT_ENTITY.register(LobbyListener::disableEntityInteractions);
        PlayerEvents.ATTACK_ENTITY.register(LobbyListener::disableEntityKills);
    }

    //Builders are allowed to use commands in the lobby
    private static void builderPermissions(FabricRavelPlayer player) {
        if (player.getRank() == RavelRank.BUILDER) {
            RavelModServer.getServer().getPlayerManager().addToOperators(player.getPlayer().getGameProfile());
        }
    }

    //Always spawn players at the spawn location
    private static void spawnRedirect(FabricRavelPlayer player) {
        Location location = RavelModServer.getSpawnManager().getSpawn();
        if (location == null) {
            RavelInstance.getLogger().error("Spawn location is null!");
            return;
        }

        player.teleport(location);
    }

    //Prevent entities from taking damage
    private static boolean damageSuppress(LivingEntity entity, DamageSource source, float amount) {
        //No player can take damage
        if (entity instanceof PlayerEntity) {
            return false;
        }

        //Creative mode players can attack
        if (source.getAttacker() != null && source.getAttacker() instanceof PlayerEntity player) {
            return player.isCreative();
        }

        return false;
    }

    //Only allow creative players to break blocks
    private static boolean allowBlockBreak(FabricRavelPlayer player, Block block, BlockPos blockPos) {
        return player.isCreative();
    }

    //Only allow creative players to place blocks
    private static boolean allowBlockPlace(FabricRavelPlayer player, BlockState blockState, BlockPos blockPos) {
        return player.isCreative();
    }

    //Only allow creative players to interact with blocks
    private static boolean disableBlcokInteractions(FabricRavelPlayer player, World world, Hand hand, BlockHitResult blockHitResult) {
        BlockState state = world.getBlockState(blockHitResult.getBlockPos());
        Identifier id = Registries.BLOCK.getId(state.getBlock());

        //We allow doors to be interacted with (and don't mess with air)
        if (state.isAir() || id.getPath().endsWith("door")) {
            return true;
        }

        return player.isCreative();
    }

    //Only allow creative players to interact with items
    private static boolean disableItemInteractions(FabricRavelPlayer player, World world, Hand hand) {
        return player.isCreative();
    }

    //Only allow creative players to interact with entities
    private static boolean disableEntityInteractions(FabricRavelPlayer player, Entity entity) {
        return player.isCreative();
    }

    //Only allow creative players to kill entities
    private static boolean disableEntityKills(FabricRavelPlayer player, Entity entity) {
        return player.isCreative();
    }
}
