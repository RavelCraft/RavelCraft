package com.connexal.ravelcraft.shared.players;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.util.RavelServer;
import com.connexal.ravelcraft.shared.util.text.Language;

import java.util.UUID;

public interface RavelPlayer extends RavelCommandSender {
    String BEDROCK_PREFIX = ".";
    String BEDROCK_SPACE_REPLACEMENT = ".";

    String getName();

    String displayName();

    void updateDisplayName();

    UUID getUniqueID();

    default String buildDisplayName() {
        RavelRank rank = this.getRank();

        if (rank.getName() == null) {
            return this.getName();
        } else {
            return "[" + this.getRank().getName() + "] " + this.getName();
        }
    }

    @Override
    default boolean isOp() {
        return RavelInstance.getPlayerManager().getRank(this).isOperator();
    }

    @Override
    default boolean isPlayer() {
        return true;
    }

    @Override
    default RavelPlayer asPlayer() {
        return this;
    }

    default RavelServer getServer() {
        return RavelInstance.getPlayerManager().getServer(this);
    }

    default void sendToServer(RavelServer server) {
        RavelInstance.getPlayerManager().setServer(this, server);
    }

    default Language getLanguage() {
        return RavelInstance.getPlayerManager().getLanguage(this);
    }

    default void setLanguage(Language language) {
        RavelInstance.getPlayerManager().setLanguage(this, language);
    }

    default RavelRank getRank() {
        return RavelInstance.getPlayerManager().getRank(this);
    }

    default void setRank(RavelRank rank) {
        RavelInstance.getPlayerManager().setRank(this, rank);
    }

    default void kick(String reason, boolean network) {
        RavelInstance.getPlayerManager().kick(this, reason, network);
    }
}
