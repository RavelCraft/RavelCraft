package com.connexal.ravelcraft.mod.cross.types.items.polymer;

import net.minecraft.item.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class PolymerArmorItem extends ArmorItem implements RavelPolymerItem {
    private final Item displayItem;
    private final int color;

    public PolymerArmorItem(RegistryEntry<ArmorMaterial> material, Type type, int color, Settings settings) {
        super(material, type, settings.maxCount(1));

        this.displayItem = switch (type) {
            case HELMET -> Items.LEATHER_HELMET;
            case CHESTPLATE -> Items.LEATHER_CHESTPLATE;
            case LEGGINGS -> Items.LEATHER_LEGGINGS;
            case BOOTS -> Items.LEATHER_BOOTS;
            default -> throw new IllegalStateException("Invalid armor type: " + type);
        };
        this.color = color;
    }

    @Override
    public int getPolymerArmorColor(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.color;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.displayItem;
    }
}
