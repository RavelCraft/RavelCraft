package com.connexal.ravelcraft.shared.players;

import com.connexal.ravelcraft.shared.util.ChatColor;

public enum RavelRank {
    OWNER(ChatColor.RED + "Owner", true),
    MODERATOR(ChatColor.GOLD + "Mod", true),

    DEVELOPER(ChatColor.AQUA + "Dev", true),
    BUILDER(ChatColor.AQUA + "Builder", false),

    PARTNER(ChatColor.GREEN + "Partner", false),
    DONATOR_I(ChatColor.DARK_PURPLE + "Donator I", false),
    DONATOR_II(ChatColor.DARK_BLUE + "Donator II", false),
    DONATOR_III(ChatColor.BLUE + "Donator III", false);

    private final String name;
    private final boolean operator;

    RavelRank(String name, boolean operator) {
        this.name = name;
        this.operator = operator;
    }

    public String getName() {
        return name;
    }

    public boolean isOperator() {
        return operator;
    }
}
