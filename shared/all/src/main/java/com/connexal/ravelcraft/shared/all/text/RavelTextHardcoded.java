package com.connexal.ravelcraft.shared.all.text;

import com.connexal.ravelcraft.shared.all.Ravel;
import com.connexal.ravelcraft.shared.all.util.ChatColor;

public class RavelTextHardcoded {
    public static final String SERVER_FULL = "The server is full!\nLe serveur est plein !";

    public static final String NOT_WHITELISTED = ChatColor.RED + "You are not whitelisted on this server! Please register with us.\n\n" +
            ChatColor.RED + "Vous n'êtes pas sur la liste blanche de ce serveur ! Veuillez vous inscrire auprès de nous.";

    public static final String BANNED = ChatColor.RED + "You are banned from this server! See end date and reason below. If you do not agree with this decision, please contact us at " + Ravel.EMAIL + "\n\n" +
            ChatColor.RED + "Vous êtes banni de ce serveur ! Voir date de fin et raison ci-dessous. Si vous n'êtes pas d'accord avec cette décision, veuillez nous contacter à l'adresse suivante : " + Ravel.EMAIL + ChatColor.RESET + "\n\n\n";

    public static final String MAINTENANCE = ChatColor.RED + "The server is currently in maintenance mode. Please try again later.\n\n" +
            ChatColor.RED + "Le serveur est actuellement en mode maintenance. Veuillez réessayer plus tard.";
}
