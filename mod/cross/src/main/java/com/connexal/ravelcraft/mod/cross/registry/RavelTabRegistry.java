package com.connexal.ravelcraft.mod.cross.registry;

import com.connexal.ravelcraft.mod.cross.types.tabs.TabWrapper;

public class RavelTabRegistry {
    public static final TabWrapper RAVEL_TAB = new TabWrapper(RavelItemRegistry.MAGIC_INGOT, "ravel_tab").register();

    public static void initialize() {}
}
