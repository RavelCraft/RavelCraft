package com.connexal.ravelcraft.mod.cross.types.items.polymer.tools;

import com.connexal.ravelcraft.mod.cross.types.items.polymer.RavelPolymerItem;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public interface RavelPolymerToolItem extends RavelPolymerItem {
    @Override
    default boolean handleMiningOnServer(ItemStack tool, BlockState targetBlock, BlockPos pos, ServerPlayerEntity player) {
        return true;
    }
}
