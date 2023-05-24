package com.connexal.ravelcraft.shared.players;

import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.util.RavelServer;
import com.connexal.ravelcraft.shared.util.text.Language;

import java.util.UUID;

public interface RavelPlayer extends RavelCommandSender {
    String getName();

    UUID getUniqueID();

    @Override
    default boolean isOp() {
        return PlayerManager.getIsOp(this);
    }

    default void setOp(boolean isOp) {
        PlayerManager.setIsOp(this, isOp);
    }

    default RavelServer getServer() {
        return PlayerManager.getServer(this);
    }

    default void sendToServer(RavelServer server) {
        PlayerManager.setServer(this, server);
    }

    default Language getLanguage() {
        return PlayerManager.getLanguage(this);
    }

    default void setLanguage(Language language) {
        PlayerManager.setLanguage(this, language);
    }

    default RavelRank getRank() {
        return PlayerManager.getRank(this);
    }

    default void setRank(RavelRank rank) {
        PlayerManager.setRank(this, rank);
    }

    default void kick(String reason, boolean network) {
        PlayerManager.kick(this, reason, network);
    }
}
