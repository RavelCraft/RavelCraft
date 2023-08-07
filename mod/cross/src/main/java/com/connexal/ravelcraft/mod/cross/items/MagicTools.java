package com.connexal.ravelcraft.mod.cross.items;

import com.connexal.ravelcraft.mod.cross.registry.RavelItemRegistry;
import com.connexal.ravelcraft.mod.cross.types.items.MiningLevel;
import com.connexal.ravelcraft.mod.cross.types.items.tools.GenericToolSet;

public class MagicTools extends GenericToolSet {
    public MagicTools() {
        super("magic", GenericToolSet.builder()
                .durability(2000)
                .miningSpeedMultiplier(15f)
                .attackDamage(7f)
                .miningLevel(MiningLevel.NETHERITE)
                .enchantability(10)
                .repairIngredient(RavelItemRegistry.MAGIC_INGOT)
        );
    }
}
