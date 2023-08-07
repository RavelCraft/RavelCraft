package com.connexal.ravelcraft.mod.cross.types.items.sets;

import com.connexal.ravelcraft.mod.cross.registry.RavelItemRegistry;
import com.google.common.collect.ImmutableMap;
import net.minecraft.item.Item;

import java.util.Map;

public class GenericSet {
    private final String identifier;
    private ImmutableMap<String, Item> items = null;

    public GenericSet(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setItems(Map<String, Item> items) {
        this.items = ImmutableMap.copyOf(items);
    }

    public Item getItem(String type) {
        return this.items.get(type);
    }

    public GenericSet register() {
        if (this.items == null) {
            throw new IllegalStateException("Items not set before registering");
        }

        for (Map.Entry<String, Item> entry : this.items.entrySet()) {
            RavelItemRegistry.register(entry.getValue(), this.identifier + "_" + entry.getKey());
        }
        return this;
    }
}
