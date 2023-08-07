package com.connexal.ravelcraft.mod.cross.registry;

import com.connexal.ravelcraft.mod.cross.BuildConstants;
import com.connexal.ravelcraft.mod.cross.blocks.MagicBlock;
import com.connexal.ravelcraft.mod.cross.types.blocks.GenericBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class RavelBlockRegistry {
    public static final List<Block> BLOCK_LIST = new ArrayList<>();

    public static final Block MAGIC_BLOCK = new MagicBlock().register();

    public static <T extends GenericBlock> T register(T block, String id) {
        Identifier identifier = new Identifier(BuildConstants.ID, id);

        BlockItem blockItem = new BlockItem(block, new Item.Settings());
        Registry.register(Registries.ITEM, identifier, blockItem);

        T registeredBlock = Registry.register(Registries.BLOCK, identifier, block);
        BLOCK_LIST.add(registeredBlock);

        return registeredBlock;
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(RavelTabRegistry.RAVEL_TAB.getRegistryKey()).register(itemGroup -> {
            for (Block block : BLOCK_LIST) {
                itemGroup.add(block.asItem());
            }
        });
    }
}
