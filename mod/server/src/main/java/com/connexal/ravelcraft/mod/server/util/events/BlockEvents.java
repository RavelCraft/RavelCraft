package com.connexal.ravelcraft.mod.server.util.events;

import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BlockEvents {
    @FunctionalInterface
    public interface BlockPlaceEvent {
        void onBlockPlace(FabricRavelPlayer player, BlockState futureState, BlockPos position);
    }
    public static final Event<BlockPlaceEvent> PLACE = EventFactory.createArrayBacked(BlockPlaceEvent.class, listeners -> (player, futureState, position) -> {
        for (BlockPlaceEvent listener : listeners) {
            listener.onBlockPlace(player, futureState, position);
        }
    });

    @FunctionalInterface
    public interface BlockBreakEvent {
        void onBlockBreak(FabricRavelPlayer player, Block block, BlockPos position);
    }
    public static final Event<BlockBreakEvent> BREAK = EventFactory.createArrayBacked(BlockBreakEvent.class, listeners -> (player, block, position) -> {
        for (BlockBreakEvent listener : listeners) {
            listener.onBlockBreak(player, block, position);
        }
    });
}
