package com.connexal.ravelcraft.mod.cross.registry;

import com.connexal.ravelcraft.mod.cross.types.blocks.BlockDescriptor;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

import java.util.ArrayList;
import java.util.List;

public class RavelBlockRegistry {
    public static final List<BlockDescriptor> BLOCK_LIST = new ArrayList<>();


    public static final BlockDescriptor MAGIC_BLOCK = BlockDescriptor.builder("magic_block")
            .block(new Block(AbstractBlock.Settings.create()
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK)))
            .register();


    public static BlockDescriptor register(BlockDescriptor descriptor) {
        Registry.register(Registries.BLOCK, descriptor.identifier(), descriptor.block());
        BLOCK_LIST.add(descriptor);

        return descriptor;
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(RavelTabRegistry.RAVEL_TAB.getRegistryKey()).register(itemGroup -> {
            for (BlockDescriptor descriptor : BLOCK_LIST) {
                itemGroup.add(descriptor.blockItem());
            }
        });
    }
}
