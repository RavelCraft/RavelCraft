package com.connexal.ravelcraft.mod.cross.types.blocks;

import com.connexal.ravelcraft.mod.cross.registry.RavelBlockRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;

public class GenericBlock extends Block {
    private final String identifier;

    public GenericBlock(String identifier, AbstractBlock.Settings settings) {
        super(settings);
        this.identifier = identifier;
    }

    public Block register() {
        return RavelBlockRegistry.register(this, this.identifier);
    }
}
