package com.connexal.ravelcraft.mod.server.managers.geyser.custom;

import com.connexal.ravelcraft.mod.cross.types.Descriptor;
import com.connexal.ravelcraft.mod.cross.types.blocks.BlockDescriptor;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomBlocksEvent;

public class GeyserBlockRegistrar {
    public static void register(GeyserDefineCustomBlocksEvent event, Descriptor descriptor) {
        switch (descriptor) {
            case BlockDescriptor blockDescriptor -> registerBlock(event, blockDescriptor);
            case null, default -> throw new IllegalStateException("This does not refer to a block or item");
        }
    }

    private static void registerBlock(GeyserDefineCustomBlocksEvent event, BlockDescriptor descriptor) {

    }
}
