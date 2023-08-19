package com.connexal.ravelcraft.mod.server.listeners;

import com.connexal.ravelcraft.mod.server.RavelModServer;
import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.Location;
import com.connexal.ravelcraft.mod.server.util.events.BlockEvents;
import com.connexal.ravelcraft.mod.server.util.events.EntityEvents;
import com.connexal.ravelcraft.mod.server.util.events.ItemEvents;
import com.connexal.ravelcraft.mod.server.util.events.PlayerEvents;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.RavelRank;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LobbyListener {
    public static void register() {
        PlayerEvents.JOINED.register(LobbyListener::spawnRedirect);
        PlayerEvents.JOINED.register(LobbyListener::builderPermissions);
        EntityEvents.DAMAGE.register(LobbyListener::damageSuppress);
        BlockEvents.PLACE.register(LobbyListener::allowBlockPlace);
        BlockEvents.BREAK.register(LobbyListener::allowBlockBreak);
        ItemEvents.BUCKET_PRE_FILL.register(LobbyListener::stopBucketFill);
        ItemEvents.BUCKET_PRE_EMPTY.register(LobbyListener::stopBucketEmpty);
        UseBlockCallback.EVENT.register(LobbyListener::disableBlcokInteractions);
        UseItemCallback.EVENT.register(LobbyListener::disableItemInteractions);
        UseEntityCallback.EVENT.register(LobbyListener::disableEntityInteractions);
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
    private static boolean damageSuppress(Entity entity, DamageSource source, float amount) {
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

    //Only allow creative players to fill buckets
    private static boolean stopBucketFill(FabricRavelPlayer player, BlockPos blockPos, ItemStack itemStack) {
        return player.isCreative();
    }

    //Only allow creative players to empty buckets
    private static boolean stopBucketEmpty(FabricRavelPlayer player, BlockPos blockPos, ItemStack itemStack) {
        return player.isCreative();
    }

    //Only allow creative players to interact with blocks
    private static ActionResult disableBlcokInteractions(PlayerEntity player, World world, Hand hand, BlockHitResult blockHitResult) {
        BlockState state = world.getBlockState(blockHitResult.getBlockPos());
        Identifier id = Registries.BLOCK.getId(state.getBlock());

        if (state.isAir() || id.getPath().endsWith("door")) {
            return ActionResult.PASS;
        }

        return player.isCreative() ? ActionResult.PASS : ActionResult.FAIL;
    }

    //Only allow creative players to interact with items
    private static TypedActionResult<ItemStack> disableItemInteractions(PlayerEntity player, World world, Hand hand) {
        return player.isCreative() ? TypedActionResult.pass(player.getStackInHand(hand)) : TypedActionResult.fail(player.getStackInHand(hand));
    }

    //Only allow creative players to interact with entities
    private static ActionResult disableEntityInteractions(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult entityHitResult) {
        return player.isCreative() ? ActionResult.PASS : ActionResult.FAIL;
    }
}
