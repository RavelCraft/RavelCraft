package com.connexal.ravelcraft.mod.cross.custom.tabs;

import com.connexal.ravelcraft.mod.cross.BuildConstants;
import com.connexal.ravelcraft.mod.cross.custom.items.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TabWrapper {
    private final ItemGroup itemGroup;
    private final String id;

    public TabWrapper(Item icon, String id) {
        this.itemGroup = FabricItemGroup.builder()
                .icon(() -> new ItemStack(icon))
                .displayName(Text.translatable("itemGroup.ravelcraft." + id))
                .build();

        this.id = id;
    }

    public TabWrapper register() {
        Registry.register(Registries.ITEM_GROUP, new Identifier(BuildConstants.ID, this.id), this.itemGroup);
        return this;
    }

    public RegistryKey<ItemGroup> getRegistryKey() {
        return RegistryKey.of(Registries.ITEM_GROUP.getKey(), new Identifier(BuildConstants.ID, this.id));
    }
}
