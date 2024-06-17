package com.connexal.ravelcraft.mod.cross.types.items;

import com.connexal.ravelcraft.mod.cross.BuildConstants;
import com.connexal.ravelcraft.mod.cross.registry.RavelItemRegistry;
import com.connexal.ravelcraft.mod.cross.types.Identifiable;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.util.Identifier;

import java.util.Optional;

public record ItemDescriptor(Identifier identifier, Item item, Model displayModel) implements Identifiable {
    public static Builder builder(Identifier identifier) {
        return new Builder(identifier);
    }

    public static Builder builder(String identifier) {
        return new Builder(Identifier.of(BuildConstants.ID, identifier));
    }

    public static class Builder {
        private final Identifier identifier;
        private Item item = null;
        private Model displayModel = null;

        private Builder(Identifier identifier) {
            this.identifier = identifier;
        }

        public Builder item(Item item) {
            this.item = item;
            return this;
        }

        public Builder displayModel(Model displayModel) {
            this.displayModel = displayModel;
            return this;
        }

        public ItemDescriptor register() {
            if (this.item == null) {
                this.item = new Item(new Item.Settings());
            }
            if (this.displayModel == null) {
                if (this.item instanceof ToolItem) {
                    this.displayModel = Models.HANDHELD;
                } else if (this.item instanceof BlockItem) {
                    this.displayModel = new Model(Optional.of(this.identifier), Optional.empty(), TextureKey.LAYER0);
                } else {
                    this.displayModel = Models.GENERATED;
                }
            }

            ItemDescriptor descriptor = new ItemDescriptor(this.identifier, this.item, this.displayModel);
            RavelItemRegistry.register(descriptor);
            return descriptor;
        }
    }
}
