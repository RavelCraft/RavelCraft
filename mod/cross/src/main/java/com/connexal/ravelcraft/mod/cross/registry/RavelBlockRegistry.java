package com.connexal.ravelcraft.mod.cross.registry;

import com.connexal.ravelcraft.mod.cross.types.Descriptor;
import com.connexal.ravelcraft.mod.cross.types.blocks.BlockDescriptor;
import com.connexal.ravelcraft.mod.cross.types.blocks.polymer.SimpleRavelPolymerBlock;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

import java.util.ArrayList;
import java.util.List;

public enum RavelBlockRegistry {
    MAGIC_BLOCK(BlockDescriptor.builder("magic_block")
            .block(new SimpleRavelPolymerBlock(Blocks.STONE, AbstractBlock.Settings.create()
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK)))
            .build());

    private final Descriptor descriptor;

    RavelBlockRegistry(Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    public Descriptor descriptor() {
        return this.descriptor;
    }

    public BlockDescriptor blockDescriptor() {
        if (this.descriptor instanceof BlockDescriptor blockDescriptor) {
            return blockDescriptor;
        } else {
            throw new IllegalStateException("This does not refer to a block");
        }
    }


    public static final ImmutableList<BlockDescriptor> BLOCK_LIST = build();

    public static ImmutableList<BlockDescriptor> build() {
        List<BlockDescriptor> list = new ArrayList<>();

        for (RavelBlockRegistry registry : values()) {
            if (registry.descriptor() instanceof BlockDescriptor descriptor) {
                Registry.register(Registries.BLOCK, descriptor.identifier(), descriptor.block());
                list.add(descriptor);
            } else {
                throw new IllegalStateException("This does not refer to a block");
            }
        }

        return ImmutableList.copyOf(list);
    }

    public static void initialize() {}
}
