package com.connexal.ravelcraft.mod.cross.registry;

import com.connexal.ravelcraft.mod.cross.types.Descriptor;
import com.connexal.ravelcraft.mod.cross.types.blocks.BlockDescriptor;
import com.connexal.ravelcraft.mod.cross.types.items.ItemDescriptor;
import com.connexal.ravelcraft.mod.cross.types.items.MiningLevel;
import com.connexal.ravelcraft.mod.cross.types.items.sets.ArmorSetDescriptor;
import com.connexal.ravelcraft.mod.cross.types.items.sets.ItemSetDescriptor;
import com.connexal.ravelcraft.mod.cross.types.items.sets.ToolSetDescriptor;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public enum RavelItemRegistry {
    MAGIC_INGOT(ItemDescriptor.builder("magic_ingot")
            .displayItem(Items.NETHERITE_INGOT)
            .build()),

    MAGIC_TOOLS(ToolSetDescriptor.builder("magic")
            .durability(2000)
            .miningSpeedMultiplier(15f)
            .attackDamage(7f)
            .miningLevel(MiningLevel.NETHERITE)
            .enchantability(10)
            .repairIngredient(RavelItemRegistry.MAGIC_INGOT.itemDescriptor().item())
            .build()),

    MAGIC_ARMOR(ArmorSetDescriptor.builder("magic")
            .durability(13, 15, 16, 11)
            .durabilityMultiplier(44)
            .protection(4, 7, 9, 3)
            .enchantability(10)
            .addLayer("", false)
            .equipSound(SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE)
            .repairIngredient(RavelItemRegistry.MAGIC_INGOT.itemDescriptor().item())
            .toughness(3.0f)
            .knockbackResistance(0.0f)
            .color(206, 0, 255)
            .build());

    private final Descriptor descriptor;

    RavelItemRegistry(Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    public Descriptor descriptor() {
        return this.descriptor;
    }

    public ItemDescriptor itemDescriptor() {
        if (this.descriptor instanceof ItemDescriptor itemDescriptor) {
            return itemDescriptor;
        } else {
            throw new IllegalStateException("This does not refer to an item");
        }
    }

    public ItemSetDescriptor<?> setDescriptor() {
        if (this.descriptor instanceof ItemSetDescriptor<?> setDescriptor) {
            return setDescriptor;
        } else {
            throw new IllegalStateException("This does not refer to a set");
        }
    }


    public static final ImmutableList<ItemDescriptor> ITEM_LIST = build();

    public static ImmutableList<ItemDescriptor> build() {
        List<ItemDescriptor> list = new ArrayList<>();

        //Items
        for (RavelItemRegistry registry : values()) {
            if (registry.descriptor() instanceof ItemDescriptor descriptor) {
                Registry.register(Registries.ITEM, descriptor.identifier(), descriptor.item());
                list.add(descriptor);
            } else if (registry.descriptor() instanceof ItemSetDescriptor<?> setDescriptor) {
                for (ItemDescriptor item : setDescriptor.items().values()) {
                    Registry.register(Registries.ITEM, item.identifier(), item.item());
                    list.add(item);
                }
            } else {
                throw new IllegalStateException("This does not refer to a block");
            }
        }

        //Block items
        for (BlockDescriptor blockDescriptor : RavelBlockRegistry.BLOCK_LIST) {
            Registry.register(Registries.ITEM, blockDescriptor.identifier(), blockDescriptor.blockItem());
        }

        return ImmutableList.copyOf(list);
    }

    public static void initialize() {}
}
