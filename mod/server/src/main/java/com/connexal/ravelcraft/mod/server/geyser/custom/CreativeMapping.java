package com.connexal.ravelcraft.mod.server.geyser.custom;

import org.geysermc.geyser.api.util.CreativeCategory;

public record CreativeMapping(String creativeGroup, CreativeCategory creativeCategory) {
    public CreativeMapping(String creativeGroup) {
        this(creativeGroup, CreativeCategory.ITEMS);
    }

    public CreativeMapping(CreativeCategory creativeCategory) {
        this(switch (creativeCategory) {
            case COMMANDS -> "itemGroup.name.commands";
            case CONSTRUCTION -> "itemGroup.name.construction";
            case EQUIPMENT -> "itemGroup.name.equipment";
            case NATURE -> "itemGroup.name.nature";
            case ITEMS, NONE -> "itemGroup.name.items";
        }, creativeCategory);
    }
}
