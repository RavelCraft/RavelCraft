package com.connexal.ravelcraft.mod.cross.types.blocks;

import com.connexal.ravelcraft.mod.cross.BuildConstants;
import com.connexal.ravelcraft.mod.cross.registry.RavelBlockRegistry;
import com.connexal.ravelcraft.mod.cross.registry.RavelItemRegistry;
import com.connexal.ravelcraft.mod.cross.types.Identifiable;
import com.connexal.ravelcraft.mod.cross.types.items.ItemDescriptor;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.util.Identifier;

import java.util.Optional;

public record BlockDescriptor(Identifier identifier, Block block, BlockItem blockItem) implements Identifiable {
    public static Builder builder(Identifier identifier) {
        return new Builder(identifier);
    }

    public static Builder builder(String identifier) {
        return new Builder(new Identifier(BuildConstants.ID, identifier));
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

        public BlockDescriptor register() {
            if (this.block == null) {
                this.block = new Block(AbstractBlock.Settings.create());
            }
            if (this.blockItem == null) {
                this.blockItem = new BlockItem(this.block, new Item.Settings());
            }

            BlockDescriptor descriptor = new BlockDescriptor(this.identifier, this.block, this.blockItem);
            RavelBlockRegistry.register(descriptor);
            ItemDescriptor.builder(this.identifier).item(this.blockItem).register();
            return descriptor;
        }
    }
}
