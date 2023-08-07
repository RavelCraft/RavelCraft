package com.connexal.ravelcraft.mod.cross.blocks;

import com.connexal.ravelcraft.mod.cross.types.blocks.GenericBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.sound.BlockSoundGroup;

public class MagicBlock extends GenericBlock {
    public MagicBlock() {
        super("magic_block", AbstractBlock.Settings.create()
                .sounds(BlockSoundGroup.AMETHYST_BLOCK)
        );
    }
}
