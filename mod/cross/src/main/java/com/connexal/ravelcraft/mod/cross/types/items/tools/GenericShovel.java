package com.connexal.ravelcraft.mod.cross.types.items.tools;

import com.connexal.ravelcraft.mod.cross.types.items.utils.GenericTyped;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolMaterial;

class GenericShovel extends ShovelItem implements GenericTyped {
    public GenericShovel(ToolMaterial material, float attackDamage, float attackSpeed) {
        super(material, attackDamage, attackSpeed, new FabricItemSettings());
    }

    @Override
    public String getType() {
        return "shovel";
    }
}
