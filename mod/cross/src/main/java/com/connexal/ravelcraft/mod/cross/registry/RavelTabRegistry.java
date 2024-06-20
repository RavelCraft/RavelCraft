package com.connexal.ravelcraft.mod.cross.registry;

import com.connexal.ravelcraft.mod.cross.types.tabs.TabDescriptor;
import com.google.common.collect.ImmutableList;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public enum RavelTabRegistry {
    RAVEL_TAB(TabDescriptor.builder(RavelItemRegistry.MAGIC_INGOT.itemDescriptor().item(), "ravel_tab")
            .entry(RavelItemRegistry.MAGIC_INGOT.itemDescriptor())
            .entry(RavelItemRegistry.MAGIC_ARMOR.setDescriptor())
            .entry(RavelItemRegistry.MAGIC_TOOLS.setDescriptor())
            .entry(RavelBlockRegistry.MAGIC_BLOCK.blockDescriptor().blockItem())
            .build());

    private final TabDescriptor tabDescriptor;

    RavelTabRegistry(TabDescriptor descriptor) {
        this.tabDescriptor = descriptor;
    }

    public TabDescriptor descriptor() {
        return this.tabDescriptor;
    }

    public static final ImmutableList<TabDescriptor> TAB_LIST = build();

    public static ImmutableList<TabDescriptor> build() {
        List<TabDescriptor> list = new ArrayList<>();

        for (RavelTabRegistry registry : values()) {
            TabDescriptor descriptor = registry.descriptor();

            list.add(descriptor);
            Registry.register(Registries.ITEM_GROUP, descriptor.getRegistryKey(), descriptor.getItemGroup());
            PolymerItemGroupUtils.registerPolymerItemGroup(descriptor.getIdentifier(), descriptor.getItemGroup());
        }

        return ImmutableList.copyOf(list);
    }

    public static void initialize() {}
}
