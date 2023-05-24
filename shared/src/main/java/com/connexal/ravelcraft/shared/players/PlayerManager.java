package com.connexal.ravelcraft.shared.players;

import com.connexal.ravelcraft.shared.util.RavelServer;
import com.connexal.ravelcraft.shared.util.text.Language;

public class PlayerManager {
    public static boolean getIsOp(RavelPlayer player) {
        //TODO: Implement this
        return false;
    }

    public static void setIsOp(RavelPlayer player, boolean isOp) {
        //TODO: Implement this
    }

    public static RavelServer getServer(RavelPlayer player) {
        //TODO: Implement this
        return null;
    }

    public static void setServer(RavelPlayer player, RavelServer server) {

    }

    public static Language getLanguage(RavelPlayer player) {
        //TODO: Implement this
        return null;
    }

    public static void setLanguage(RavelPlayer player, Language language) {

    }

    public static RavelRank getRank(RavelPlayer player) {
        //TODO: Implement this
        return null;
    }

    public static void setRank(RavelPlayer player, RavelRank rank) {

    }

    public static void kick(RavelPlayer player, String reason, boolean network) {

    }
}
