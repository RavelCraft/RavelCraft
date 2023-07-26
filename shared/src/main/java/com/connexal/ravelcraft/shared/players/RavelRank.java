package com.connexal.ravelcraft.shared.players;

import com.connexal.ravelcraft.shared.util.ChatColor;

public enum RavelRank {
    OWNER("Owner", ChatColor.RED, true),
    MOD("Mod", ChatColor.GOLD, true),

    DEV("Dev", ChatColor.AQUA, true),
    BUILDER("Builder", ChatColor.AQUA, false),

    PARTNER("Partner", ChatColor.GREEN, false),
    DONATOR_I("Donator I", ChatColor.DARK_PURPLE, false),
    DONATOR_II( "Donator II", ChatColor.DARK_BLUE, false),
    DONATOR_III("Donator III", ChatColor.BLUE, false),

    NONE("None", null, false);

    private final String name;
    private final String colour;
    private final boolean operator;

    RavelRank(String name, String colour, boolean operator) {
        this.name = name;
        this.colour = colour;
        this.operator = operator;
    }

    public String getName() {
        if (this.colour == null) {
            return this.name;
        }

        return this.colour + this.name + ChatColor.RESET;
    }

    public boolean isOperator() {
        return this.operator;
    }
}
