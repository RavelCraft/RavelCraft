package com.connexal.ravelcraft.mod.cross.types.items.tools;

import com.connexal.ravelcraft.mod.cross.types.items.utils.GenericTyped;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;

class GenericPickaxe extends PickaxeItem implements GenericTyped {
    public GenericPickaxe(ToolMaterial material, int attackDamage, float attackSpeed) {
        super(material, attackDamage, attackSpeed, new FabricItemSettings());
    }

    @Override
    public String getType() {
        return "pickaxe";
    }
}
