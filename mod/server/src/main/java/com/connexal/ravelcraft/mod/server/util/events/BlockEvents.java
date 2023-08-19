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
        boolean onBlockPlace(FabricRavelPlayer player, BlockState futureState, BlockPos position);
    }
    public static final Event<BlockPlaceEvent> PLACE = EventFactory.createArrayBacked(BlockPlaceEvent.class, listeners -> (player, futureState, position) -> {
        for (BlockPlaceEvent listener : listeners) {
            boolean out = listener.onBlockPlace(player, futureState, position);
            if (!out) {
                return false;
            }
        }

        return true;
    });

    @FunctionalInterface
    public interface BlockBreakEvent {
        boolean onBlockBreak(FabricRavelPlayer player, Block block, BlockPos position);
    }
    public static final Event<BlockBreakEvent> BREAK = EventFactory.createArrayBacked(BlockBreakEvent.class, listeners -> (player, block, position) -> {
        for (BlockBreakEvent listener : listeners) {
            boolean out = listener.onBlockBreak(player, block, position);
            if (!out) {
                return false;
            }
        }

        return true;
    });
}
