package com.connexal.ravelcraft.mod.cross.types.items;

import com.connexal.ravelcraft.mod.cross.registry.RavelItemRegistry;
import com.connexal.ravelcraft.mod.cross.types.items.utils.GenericTyped;
import net.minecraft.item.Item;

public class GenericSet {
    private final String identifier;
    private Item[] items = null;

    public GenericSet(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setItems(Item... items) {
        this.items = items;
    }

    public Item[] getItems() {
        return this.items;
    }

    public GenericSet register() {
        if (this.items == null) {
            throw new IllegalStateException("Items not set before registering");
        }

        for (Item item : this.items) {
            if (!(item instanceof GenericTyped typed)) {
                throw new IllegalStateException("Item " + item + " is not an instance of GenericTyped");
            }

            RavelItemRegistry.register(item, this.identifier + "_" + typed.getType());
        }
        return this;
    }
}
