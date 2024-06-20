package com.connexal.ravelcraft.mod.server.managers.geyser.custom;

import com.connexal.ravelcraft.mod.cross.types.Descriptor;
import com.connexal.ravelcraft.mod.cross.types.blocks.BlockDescriptor;
import org.geysermc.geyser.api.block.custom.CustomBlockData;
import org.geysermc.geyser.api.block.custom.component.BoxComponent;
import org.geysermc.geyser.api.block.custom.component.CustomBlockComponents;
import org.geysermc.geyser.api.block.custom.component.GeometryComponent;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomBlocksEvent;
import org.geysermc.geyser.api.util.CreativeCategory;

public class GeyserBlockRegistrar {
    public static void register(GeyserDefineCustomBlocksEvent event, Descriptor descriptor) {
        switch (descriptor) {
            case BlockDescriptor blockDescriptor -> registerBlock(event, blockDescriptor);
            case null, default -> throw new IllegalStateException("This does not refer to a block or item");
        }
    }

    private static void registerBlock(GeyserDefineCustomBlocksEvent event, BlockDescriptor descriptor) {
        BoxComponent box;
        CustomBlockComponents.Builder components = CustomBlockComponents.builder();

        switch (descriptor.model()) {
            case CUBE:
                box = BoxComponent.fullBox();
                components.geometry(GeometryComponent.builder().identifier("minecraft:geometry.full_block").build());
                break;
            default:
                throw new IllegalStateException("This block model is not supported");
        }

        components.selectionBox(box)
                .displayName(descriptor.displayName());

        if (descriptor.collisions()) {
            components.collisionBox(box);
        } else {
            components.collisionBox(BoxComponent.emptyBox());
        }

        CustomBlockData data = CustomBlockData.builder()
                .name(descriptor.identifier().getPath())
                .includedInCreativeInventory(true)
                .components(components.build())
                .creativeCategory(CreativeCategory.ITEMS)
                .creativeGroup("itemGroup.name.item")
                .build();

        event.register(data);
    }
}
