package com.connexal.ravelcraft.mod.cross.types.blocks.polymer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class SimpleRavelPolymerBlock extends Block implements RavelPolymerBlock {
    private final Block displayBlock;

    public SimpleRavelPolymerBlock(Block displayBlock, Settings settings) {
        super(settings);

        this.displayBlock = displayBlock;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return this.displayBlock.getDefaultState();
    }
}
