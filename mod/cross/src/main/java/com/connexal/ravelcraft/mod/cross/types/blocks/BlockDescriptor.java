package com.connexal.ravelcraft.mod.cross.types.blocks;

import com.connexal.ravelcraft.mod.cross.types.Descriptor;
import com.connexal.ravelcraft.shared.all.Ravel;
import com.connexal.ravelcraft.shared.all.text.Language;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public record BlockDescriptor(Identifier identifier, Block block, BlockItem blockItem, String displayName) implements Descriptor {
    public static Builder builder(Identifier identifier) {
        return new Builder(identifier);
    }

    public static Builder builder(String identifier) {
        return new Builder(Identifier.of(Ravel.ID, identifier));
    }

    public static class Builder {
        private final Identifier identifier;
        private Block block = null;
        private BlockItem blockItem = null;

        private Builder(Identifier identifier) {
            this.identifier = identifier;
        }

        public Builder block(Block block) {
            this.block = block;
            return this;
        }

        public Builder blockItem(BlockItem blockItem) {
            this.blockItem = blockItem;
            return this;
        }

        public BlockDescriptor build() {
            if (this.block == null) {
                this.block = new Block(AbstractBlock.Settings.create());
            }
            if (this.blockItem == null) {
                this.blockItem = new BlockItem(this.block, new Item.Settings());
            }

            String displayName = RavelText.getFormatString(Language.DEFAULT,  Ravel.ID + ".block." + this.identifier.getPath());
            if (displayName == null) {
                displayName = this.identifier.getPath();
            }

            return new BlockDescriptor(this.identifier, this.block, this.blockItem, displayName);
        }
    }
}
