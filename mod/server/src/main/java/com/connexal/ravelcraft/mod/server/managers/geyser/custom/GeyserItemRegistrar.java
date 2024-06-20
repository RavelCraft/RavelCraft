package com.connexal.ravelcraft.mod.server.managers.geyser.custom;

import com.connexal.ravelcraft.mod.cross.types.Descriptor;
import com.connexal.ravelcraft.mod.cross.types.blocks.BlockDescriptor;
import com.connexal.ravelcraft.mod.cross.types.items.ItemDescriptor;
import com.connexal.ravelcraft.mod.cross.types.items.sets.ArmorSetDescriptor;
import com.connexal.ravelcraft.mod.cross.types.items.sets.ItemSetDescriptor;
import com.connexal.ravelcraft.mod.cross.types.items.sets.ToolSetDescriptor;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomItemsEvent;
import org.geysermc.geyser.api.item.custom.NonVanillaCustomItemData;
import org.geysermc.geyser.api.util.CreativeCategory;

import java.util.Locale;
import java.util.Map;

public class GeyserItemRegistrar {
    public static void register(GeyserDefineCustomItemsEvent event, Descriptor descriptor) {
        switch (descriptor) {
            case ItemDescriptor itemDescriptor -> event.register(itemBuilder(itemDescriptor).build());
            case ItemSetDescriptor<?> setDescriptor -> registerSet(event, setDescriptor);
            case BlockDescriptor ignored -> throw new IllegalStateException("Bedrock has no concept of a block item, simply register the block itself");
            case null, default -> throw new IllegalStateException("This does not refer to a block or item");
        }
    }

    private static NonVanillaCustomItemData.Builder itemBuilder(ItemDescriptor descriptor) {
        Item item = descriptor.item();
        ItemStack defaultStack = item.getDefaultStack();

        //TODO: Set translationString, repairMaterials, edible, canAlwaysEat, isChargable

        return NonVanillaCustomItemData.builder()
                .icon(descriptor.displayItem().toString().substring(descriptor.displayItem().toString().indexOf(":") + 1))
                .name(descriptor.identifier().getPath())
                .displayName(descriptor.displayName())
                .javaId(Registries.ITEM.getRawId(item))
                .identifier(descriptor.identifier().toString())
                .stackSize(item.getMaxCount())
                .maxDamage(defaultStack.getMaxDamage())
                .allowOffhand(true)
                .foil(item.hasGlint(defaultStack))
                .creativeCategory(CreativeCategory.ITEMS.id())
                .creativeGroup("itemGroup.name.item");
    }

    private static void registerSet(GeyserDefineCustomItemsEvent event, ItemSetDescriptor<?> descriptor) {
        for (Map.Entry<?, ItemDescriptor> entry : descriptor.items().entrySet()) {
            NonVanillaCustomItemData.Builder builder = itemBuilder(entry.getValue());

            if (descriptor instanceof ArmorSetDescriptor armorDescriptor) {
                ArmorItem.Type type = (ArmorItem.Type) entry.getKey();

                builder.armorType(type.toString().toLowerCase(Locale.ROOT));
                builder.protectionValue(((ArmorItem) entry.getValue().item()).getProtection());

                if (type == ArmorItem.Type.HELMET) {
                    builder.hat(true);
                }
            } else if (descriptor instanceof ToolSetDescriptor toolDescriptor) {
                ToolSetDescriptor.Type type = (ToolSetDescriptor.Type) entry.getKey();

                builder.toolType(type.toString().toLowerCase(Locale.ROOT));
                //builder.toolTier("TODO");
                builder.attackDamage((int) toolDescriptor.getAttackDamage());
            }

            event.register(builder.build());
        }
    }
}
