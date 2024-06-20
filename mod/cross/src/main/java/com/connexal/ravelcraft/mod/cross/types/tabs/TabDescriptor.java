package com.connexal.ravelcraft.mod.cross.types.tabs;

import com.connexal.ravelcraft.mod.cross.types.Descriptor;
import com.connexal.ravelcraft.mod.cross.types.items.ItemDescriptor;
import com.connexal.ravelcraft.mod.cross.types.items.sets.ItemSetDescriptor;
import com.connexal.ravelcraft.shared.all.Ravel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class TabDescriptor implements Descriptor {
    private final ItemGroup itemGroup;
    private final Identifier identifier;
    private final RegistryKey<ItemGroup> registryKey;

    public TabDescriptor(ItemGroup itemGroup, Identifier identifier) {
        this.itemGroup = itemGroup;
        this.identifier = identifier;
        this.registryKey = RegistryKey.of(Registries.ITEM_GROUP.getKey(), this.identifier);
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    public ItemGroup getItemGroup() {
        return this.itemGroup;
    }

    public RegistryKey<ItemGroup> getRegistryKey() {
        return this.registryKey;
    }

    public static Builder builder(Item icon, String id) {
        return new Builder(icon, Identifier.of(Ravel.ID, id));
    }

    public static class Builder {
        private final ItemStack icon;
        private final Identifier identifier;
        private final List<ItemStack> entries = new ArrayList<>();

        private Builder(Item icon, Identifier identifier) {
            this.icon = new ItemStack(icon);
            this.identifier = identifier;
        }

        public Builder entry(ItemStack entry) {
            this.entries.add(entry);
            return this;
        }

        public Builder entry(Item entry) {
            return this.entry(new ItemStack(entry));
        }

        public Builder entry(ItemDescriptor itemDescriptor) {
            return this.entry(itemDescriptor.item());
        }

        public Builder entry(ItemSetDescriptor<?> setDescriptor) {
            for (ItemDescriptor itemDescriptor : setDescriptor.items().values()) {
                this.entry(itemDescriptor);
            }
            return this;
        }

        public TabDescriptor build() {
            return new TabDescriptor(ItemGroup.create(ItemGroup.Row.BOTTOM, -1)
                    .icon(() -> this.icon)
                    .displayName(Text.translatable("itemGroup." + Ravel.ID + "." + this.identifier.getPath()))
                    .entries((group, entries) -> {
                        for (ItemStack entry : this.entries) {
                            entries.add(entry);
                        }
                    })
                    .build(), this.identifier);
        }
    }
}
