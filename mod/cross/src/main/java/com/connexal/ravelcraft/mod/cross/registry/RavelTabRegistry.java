package com.connexal.ravelcraft.mod.cross.registry;

import com.connexal.ravelcraft.mod.cross.types.tabs.TabWrapper;
import net.minecraft.item.Item;

public enum RavelTabRegistry {
    RAVEL_TAB(RavelItemRegistry.MAGIC_INGOT.itemDescriptor().item(), "ravel_tab");

    private final TabWrapper tabWrapper;

    RavelTabRegistry(Item icon, String id) {
        this.tabWrapper = new TabWrapper(icon, id).register();
    }

    public TabWrapper wrapper() {
        return tabWrapper;
    }

    public static void initialize() {}
}
