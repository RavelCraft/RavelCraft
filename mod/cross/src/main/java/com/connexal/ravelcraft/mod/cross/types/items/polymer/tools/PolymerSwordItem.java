package com.connexal.ravelcraft.mod.cross.types.items.polymer.tools;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class PolymerSwordItem extends SwordItem implements RavelPolymerToolItem {
    private final Item displayItem;

    public PolymerSwordItem(ToolMaterial toolMaterial, Item displayItem, Settings settings) {
        super(toolMaterial, settings);

        this.displayItem = displayItem;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.displayItem;
    }
}
