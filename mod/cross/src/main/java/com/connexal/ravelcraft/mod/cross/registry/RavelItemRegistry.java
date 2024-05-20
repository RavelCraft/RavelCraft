package com.connexal.ravelcraft.mod.cross.registry;

import com.connexal.ravelcraft.mod.cross.types.items.ItemDescriptor;
import com.connexal.ravelcraft.mod.cross.types.items.MiningLevel;
import com.connexal.ravelcraft.mod.cross.types.items.sets.ArmorSet;
import com.connexal.ravelcraft.mod.cross.types.items.sets.ToolSet;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public class RavelItemRegistry {
    public static final List<ItemDescriptor> ITEM_LIST = new ArrayList<>();


    public static final ItemDescriptor MAGIC_INGOT = ItemDescriptor.builder("magic_ingot").register();

    public static final ToolSet MAGIC_TOOLS = ToolSet.builder("magic")
            .durability(2000)
            .miningSpeedMultiplier(15f)
            .attackDamage(7f)
            .miningLevel(MiningLevel.NETHERITE)
            .enchantability(10)
            .repairIngredient(RavelItemRegistry.MAGIC_INGOT.item())
            .register();

    public static final ArmorSet MAGIC_ARMOR = ArmorSet.builder("magic")
            .durability(13, 15, 16, 11)
            .durabilityMultiplier(44)
            .protection(4, 7, 9, 3)
            .enchantability(10)
            .addLayer("", false)
            .equipSound(SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE)
            .repairIngredient(RavelItemRegistry.MAGIC_INGOT.item())
            .toughness(3.0f)
            .knockbackResistance(0.0f)
            .register();


    public static ItemDescriptor register(ItemDescriptor descriptor) {
        Registry.register(Registries.ITEM, descriptor.identifier(), descriptor.item());
        ITEM_LIST.add(descriptor);

        return descriptor;
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(RavelTabRegistry.RAVEL_TAB.getRegistryKey()).register(itemGroup -> {
            for (ItemDescriptor item : ITEM_LIST) {
                itemGroup.add(item.item());
            }
        });
    }
}
