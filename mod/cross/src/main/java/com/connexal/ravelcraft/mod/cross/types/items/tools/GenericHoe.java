package com.connexal.ravelcraft.mod.cross.types.items.tools;

import com.connexal.ravelcraft.mod.cross.types.items.utils.GenericTyped;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ToolMaterial;

class GenericHoe extends HoeItem implements GenericTyped {
    public GenericHoe(ToolMaterial material, int attackDamage, float attackSpeed) {
        super(material, attackDamage, attackSpeed, new FabricItemSettings());
    }

    @Override
    public String getType() {
        return "hoe";
    }
}
