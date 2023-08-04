package com.connexal.ravelcraft.mod.cross.custom.blocks;

import com.connexal.ravelcraft.mod.cross.custom.tabs.ModTabs;
import com.connexal.ravelcraft.mod.cross.BuildConstants;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {
    public static final List<Block> BLOCK_LIST = new ArrayList<>();

    public static final Block CONDENSED_DIRT = register(new Block(AbstractBlock.Settings.create().sounds(BlockSoundGroup.GRASS)), "condensed_dirt");
    public static final Block MAGIC_BLOCK = register(new Block(AbstractBlock.Settings.create().sounds(BlockSoundGroup.AMETHYST_BLOCK)), "magic_block");

    public static <T extends Block> T register(T block, String name) {
        Identifier id = new Identifier(BuildConstants.ID, name);

        BlockItem blockItem = new BlockItem(block, new Item.Settings());
        Registry.register(Registries.ITEM, id, blockItem);

        T registeredBlock = Registry.register(Registries.BLOCK, id, block);
        BLOCK_LIST.add(registeredBlock);

        return registeredBlock;
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ModTabs.RAVEL_TAB.getRegistryKey()).register(itemGroup -> {
            for (Block block : BLOCK_LIST) {
                itemGroup.add(block.asItem());
            }
        });
    }
}
