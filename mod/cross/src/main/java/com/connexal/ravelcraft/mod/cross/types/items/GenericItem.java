package com.connexal.ravelcraft.mod.cross.types.items;

import com.connexal.ravelcraft.mod.cross.registry.RavelItemRegistry;
import net.minecraft.item.Item;

public class GenericItem extends Item {
    private final String identifier;

    public GenericItem(String identifier, Item.Settings settings) {
        super(settings);
        this.identifier = identifier;
    }

    public GenericItem(String identifier) {
        this(identifier, new Item.Settings());
    }

    public Item register() {
        return RavelItemRegistry.register(this, this.identifier);
    }
}
