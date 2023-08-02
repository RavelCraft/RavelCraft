package com.connexal.ravelcraft.mod.server.geyser.custom;

import com.connexal.ravelcraft.mod.cross.custom.blocks.ModBlocks;
import com.connexal.ravelcraft.mod.cross.custom.items.ModItems;
import com.connexal.ravelcraft.mod.server.RavelModServer;
import com.connexal.ravelcraft.mod.server.geyser.GeyserEventRegistration;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.block.custom.CustomBlockData;
import org.geysermc.geyser.api.block.custom.CustomBlockPermutation;
import org.geysermc.geyser.api.block.custom.CustomBlockState;
import org.geysermc.geyser.api.block.custom.NonVanillaCustomBlockData;
import org.geysermc.geyser.api.block.custom.component.BoxComponent;
import org.geysermc.geyser.api.block.custom.component.CustomBlockComponents;
import org.geysermc.geyser.api.block.custom.nonvanilla.JavaBlockState;
import org.geysermc.geyser.api.block.custom.nonvanilla.JavaBoundingBox;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomBlocksEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomItemsEvent;
import org.geysermc.geyser.api.item.custom.NonVanillaCustomItemData;
import org.geysermc.geyser.api.util.CreativeCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class GeyserCustomRegistration {
    public static void setup() {
        GeyserEventRegistration eventRegistrar = RavelModServer.getGeyserEvents();
        GeyserCustomRegistration customRegistrar = new GeyserCustomRegistration();

        eventRegistrar.register(GeyserDefineCustomItemsEvent.class, customRegistrar::onDefineCustomItems);
        eventRegistrar.register(GeyserDefineCustomBlocksEvent.class, customRegistrar::onDefineCustomBlocks);
    }

    @Subscribe
    public void onDefineCustomItems(GeyserDefineCustomItemsEvent event) {
        for (Item item : ModItems.ITEM_LIST) {
            Optional<RegistryKey<Item>> optionalLocation = Registries.ITEM.getKey(item);
            if (optionalLocation.isEmpty()) {
                continue;
            }
            Identifier location = optionalLocation.get().getValue();

            NonVanillaCustomItemData.Builder dataBuilder = NonVanillaCustomItemData.builder()
                    .name(location.getPath())
                    .displayName(Text.translatable(item.getTranslationKey()).getString())
                    .identifier(location.toString())
                    .icon(location.toString())
                    .javaId(Registries.ITEM.getRawId(item))
                    .stackSize(item.getMaxCount())
                    .maxDamage(item.getMaxDamage())
                    .allowOffhand(true)
                    .creativeCategory(CreativeCategory.ITEMS.id())
                    .creativeGroup("itemGroup.name.items");

            //Can add foil/glint if needed

            if (item.isFood()) {
                dataBuilder
                        .creativeCategory(CreativeCategory.EQUIPMENT.id())
                        .creativeGroup("itemGroup.name.miscFood")
                        .edible(true);
            }

            CreativeMappings.setup(item, dataBuilder);

            if (item instanceof ArmorItem armorItem) {
                dataBuilder.protectionValue(armorItem.getProtection());
                switch (armorItem.getSlotType()) {
                    case HEAD -> dataBuilder.armorType("helmet").creativeGroup("itemGroup.name.helmet");
                    case CHEST -> dataBuilder.armorType("chestplate").creativeGroup("itemGroup.name.chestplate");
                    case LEGS -> dataBuilder.armorType("leggings").creativeGroup("itemGroup.name.leggings");
                    case FEET -> dataBuilder.armorType("boots").creativeGroup("itemGroup.name.boots");
                }
            } else if (item instanceof ToolItem tieredItem) {
                dataBuilder.displayHandheld(true); // So we hold the tool right

                switch (tieredItem.getMaterial().getMiningLevel()) {
                    case 0 -> dataBuilder.toolTier("WOOD");
                    case 1 -> dataBuilder.toolTier("STONE");
                    case 2 -> dataBuilder.toolTier("IRON");
                    case 3 -> dataBuilder.toolTier("DIAMOND");
                    case 4 -> dataBuilder.toolTier("NETHERITE");
                }

                if (item instanceof PickaxeItem) {
                    dataBuilder.toolType("pickaxe");
                } else if (item instanceof HoeItem) {
                    dataBuilder.toolType("hoe");
                } else if (item instanceof AxeItem) {
                    dataBuilder.toolType("axe");
                } else if (item instanceof ShovelItem) {
                    dataBuilder.toolType("shovel");
                } else if (item instanceof SwordItem) {
                    dataBuilder.toolType("sword");
                }
            } else if (item instanceof ShearsItem) {
                dataBuilder.toolType("shears");
            } else if (item instanceof BowItem) {
                dataBuilder.chargeable(true);
            }

            event.register(dataBuilder.build());
        }
    }

    @Subscribe
    public void onDefineCustomBlocks(GeyserDefineCustomBlocksEvent event) {
        for (Block block : ModBlocks.BLOCK_LIST) {
            Identifier location;
            try {
                location = Registries.BLOCK.getKey(block).get().getValue();
            } catch (NoSuchElementException | NullPointerException e) {
                continue;
            }

            CustomBlockComponents components = CustomBlockComponents.builder()
                    .collisionBox(BoxComponent.fullBox())
                    .selectionBox(BoxComponent.fullBox())
                    .build();

            CustomBlockData.Builder dataBuilder = NonVanillaCustomBlockData.builder()
                    .identifier(location.toString())
                    .name(location.getPath())
                    .includedInCreativeInventory(true)
                    .creativeGroup("itemGroup.name.items")
                    .creativeCategory(CreativeCategory.ITEMS)
                    .components(components);

            event.register(dataBuilder.build());
        }
    }
}
