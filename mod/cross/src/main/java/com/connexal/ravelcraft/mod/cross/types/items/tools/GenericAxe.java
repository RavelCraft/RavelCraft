package com.connexal.ravelcraft.mod.cross.types.items.tools;

import com.connexal.ravelcraft.mod.cross.types.items.utils.GenericTyped;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ToolMaterial;

class GenericAxe extends AxeItem implements GenericTyped {
    public GenericAxe(ToolMaterial material, float attackDamage, float attackSpeed) {
        super(material, attackDamage, attackSpeed, new FabricItemSettings());
    }

    @Override
    public String getType() {
        return "axe";
    }
}
