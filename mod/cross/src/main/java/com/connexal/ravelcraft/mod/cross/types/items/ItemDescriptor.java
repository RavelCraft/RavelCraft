package com.connexal.ravelcraft.mod.cross.types.items;

import com.connexal.ravelcraft.mod.cross.types.Descriptor;
import com.connexal.ravelcraft.shared.all.Ravel;
import com.connexal.ravelcraft.shared.all.RavelMain;
import com.connexal.ravelcraft.shared.all.text.Language;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.util.Identifier;

import java.util.Optional;

public record ItemDescriptor(Identifier identifier, Item item, Model displayModel, String displayName) implements Descriptor {
    public static Builder builder(Identifier identifier) {
        return new Builder(identifier);
    }

    public static Builder builder(String identifier) {
        return new Builder(Identifier.of(Ravel.ID, identifier));
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

        public ItemDescriptor build() {
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

            String displayName = RavelText.getFormatString(Language.DEFAULT,  Ravel.ID + ".item." + this.identifier.getPath());
            if (displayName == null) {
                displayName = this.identifier.getPath();
            }

            return new ItemDescriptor(this.identifier, this.item, this.displayModel, displayName);
        }
    }
}
