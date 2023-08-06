package com.connexal.ravelcraft.shared.util.text;

import com.connexal.ravelcraft.shared.util.ChatColor;

public class InitText {
    public static final String SERVER_FULL = "The server is full!\nLe serveur est plein !";

    public static final String NOT_WHITELISTED = ChatColor.RED + "You are not whitelisted on this server!\n" +
            ChatColor.RED + "Vous n'êtes pas sur la liste blanche de ce serveur !";

    public static final String BANNED = ChatColor.RED + "You are banned from this server! See end date and reason below.\n" +
            ChatColor.RESET + "Vous êtes banni de ce serveur ! Voir date de fin et raison ci-dessous.\n\n" +
            "{0} UTC\n{1}";
}
