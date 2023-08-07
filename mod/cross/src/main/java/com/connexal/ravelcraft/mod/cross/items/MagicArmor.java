package com.connexal.ravelcraft.mod.cross.items;

import com.connexal.ravelcraft.mod.cross.registry.RavelItemRegistry;
import com.connexal.ravelcraft.mod.cross.types.items.sets.GenericArmorSet;
import net.minecraft.sound.SoundEvents;

public class MagicArmor extends GenericArmorSet {
    public MagicArmor() {
        super("magic", GenericArmorSet.builder()
                .durability(13, 15, 16, 11)
                .durabilityMultiplier(44)
                .protection(4, 7, 9, 3)
                .enchantability(10)
                .equipSound(SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE)
                .repairIngredient(RavelItemRegistry.MAGIC_INGOT)
                .toughness(3.0f)
                .knockbackResistance(0.0f)
        );
    }
}
