package com.connexal.ravelcraft.mod.cross.types.blocks;

import com.connexal.ravelcraft.mod.cross.types.Descriptor;
import com.connexal.ravelcraft.mod.cross.types.blocks.polymer.SimpleRavelPolymerBlock;
import com.connexal.ravelcraft.mod.cross.types.items.polymer.SimpleRavelPoymerBlockItem;
import com.connexal.ravelcraft.shared.all.Ravel;
import com.connexal.ravelcraft.shared.all.RavelMain;
import com.connexal.ravelcraft.shared.all.text.Language;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public record BlockDescriptor(Identifier identifier, Block block, BlockItem blockItem, String displayName, BlockModel model, boolean collisions) implements Descriptor {
    public static Builder builder(Identifier identifier) {
        return new Builder(identifier);
    }

    public static Builder builder(String identifier) {
        return new Builder(Identifier.of(Ravel.ID, identifier));
    }

    public static class Builder {
        private final Identifier identifier;
        private Block block = null;
        private Block displayBlock = null;
        private BlockItem blockItem = null;
        private BlockModel model = null;
        private boolean collisions = true;

        private Builder(Identifier identifier) {
            this.identifier = identifier;
        }

        public Builder block(Block block) {
            this.block = block;
            return this;
        }

        public Builder displayBlock(Block displayBlock) {
            this.displayBlock = displayBlock;
            return this;
        }

        public Builder blockItem(BlockItem blockItem) {
            this.blockItem = blockItem;
            return this;
        }

        public Builder model(BlockModel model) {
            this.model = model;
            return this;
        }

        public Builder collisions(boolean collisions) {
            this.collisions = collisions;
            return this;
        }

        public BlockDescriptor build() {
            if (this.block == null) {
                if (this.displayBlock == null) {
                    this.displayBlock = Blocks.STONE;
                }

                this.block = new SimpleRavelPolymerBlock(this.displayBlock, AbstractBlock.Settings.create());
            } else {
                if (this.displayBlock == null) {
                    if (this.block instanceof SimpleRavelPolymerBlock polymerBlock) {
                        this.displayBlock = polymerBlock.getPolymerBlockState(null).getBlock();
                    } else {
                        RavelMain.get().getRavelLogger().error("Block is set for " + this.identifier + ", but a display block is not set. The block will be used as the display block.");
                        this.displayBlock = this.block;
                    }
                } else {
                    RavelMain.get().getRavelLogger().error("Display block is set for " + this.identifier + ", but a block is also set. The display block will be ignored.");
                }
            }

            if (this.blockItem == null) {
                this.blockItem = new SimpleRavelPoymerBlockItem(this.block, Items.STONE, new Item.Settings());
            }

            if (this.model == null) {
                this.model = BlockModel.CUBE;
            }

            String displayName = RavelText.getFormatString(Language.DEFAULT,  Ravel.ID + ".block." + this.identifier.getPath());
            if (displayName == null) {
                displayName = this.identifier.getPath();
            }

            return new BlockDescriptor(this.identifier, this.block, this.blockItem, displayName, this.model, this.collisions);
        }
    }
}
