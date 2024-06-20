package com.connexal.ravelcraft.mod.cross.types.items.polymer;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class SimpleRavelPolymerItem extends Item implements RavelPolymerItem {
    private final Item displayItem;

    public SimpleRavelPolymerItem(Item displayItem, Item.Settings settings) {
        super(settings);

        this.displayItem = displayItem;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.displayItem;
    }
}
