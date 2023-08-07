package com.connexal.ravelcraft.mod.cross.types.items;

public enum MiningLevel {
    WOOD(0),
    STONE(1),
    IRON(2),
    DIAMOND(3),
    NETHERITE(4);

    private final int level;

    MiningLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
