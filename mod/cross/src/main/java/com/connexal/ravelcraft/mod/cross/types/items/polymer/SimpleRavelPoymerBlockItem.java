package com.connexal.ravelcraft.mod.cross.types.items.polymer;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class SimpleRavelPoymerBlockItem extends BlockItem implements RavelPolymerItem {
    private final Item displayItem;

    public SimpleRavelPoymerBlockItem(Block block, Item displayItem, Settings settings) {
        super(block, settings);

        this.displayItem = displayItem;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.displayItem;
    }
}
