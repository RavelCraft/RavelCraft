package com.connexal.ravelcraft.mod.cross.registry;

import com.connexal.ravelcraft.mod.cross.BuildConstants;
import com.connexal.ravelcraft.mod.cross.items.MagicIngot;
import com.connexal.ravelcraft.mod.cross.items.MagicTools;
import com.connexal.ravelcraft.mod.cross.types.items.GenericSet;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class RavelItemRegistry {
    public static final List<Item> ITEM_LIST = new ArrayList<>();

    public static final Item MAGIC_INGOT = new MagicIngot().register();
    public static final GenericSet MAGIC_TOOLS = new MagicTools().register();

    public static <T extends Item> T register(T item, String id) {
        Identifier itemID = new Identifier(BuildConstants.ID, id);

        T registeredItem = Registry.register(Registries.ITEM, itemID, item);
        ITEM_LIST.add(registeredItem);

        return registeredItem;
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(RavelTabRegistry.RAVEL_TAB.getRegistryKey()).register(itemGroup -> {
            for (Item item : ITEM_LIST) {
                itemGroup.add(item);
            }
        });
    }
}
