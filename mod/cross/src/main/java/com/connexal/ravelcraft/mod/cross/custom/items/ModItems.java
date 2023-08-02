package com.connexal.ravelcraft.mod.cross.custom.items;

import com.connexal.ravelcraft.mod.cross.custom.tabs.ModTabs;
import com.connexal.ravelcraft.mod.cross.BuildConstants;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static final List<Item> ITEM_LIST = new ArrayList<>();

    public static final Item WAND = register(new Item(new FabricItemSettings()), "wand");

    public static <T extends Item> T register(T item, String ID) {
        Identifier itemID = new Identifier(BuildConstants.ID, ID);

        T registeredItem = Registry.register(Registries.ITEM, itemID, item);
        ITEM_LIST.add(registeredItem);

        return registeredItem;
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ModTabs.RAVEL_TAB.getRegistryKey()).register(itemGroup -> {
            for (Item item : ITEM_LIST) {
                itemGroup.add(item);
            }
        });
    }
}
