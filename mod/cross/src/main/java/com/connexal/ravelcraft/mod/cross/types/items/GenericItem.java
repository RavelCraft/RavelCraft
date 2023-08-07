package com.connexal.ravelcraft.mod.cross.types.items;

import com.connexal.ravelcraft.mod.cross.registry.RavelItemRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;

public class GenericItem extends Item {
    private final String identifier;

    public GenericItem(String identifier, FabricItemSettings settings) {
        super(settings);
        this.identifier = identifier;
    }

    public Item register() {
        return RavelItemRegistry.register(this, this.identifier);
    }
}
