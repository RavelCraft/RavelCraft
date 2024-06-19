package com.connexal.ravelcraft.shared.server.players;

import com.connexal.ravelcraft.shared.server.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.server.util.server.RavelServer;
import com.connexal.ravelcraft.shared.all.text.Language;

import java.util.UUID;

public interface RavelPlayer extends RavelCommandSender {
    String BEDROCK_PREFIX = ".";
    String BEDROCK_SPACE_REPLACEMENT = ".";

    String getName();

    String displayName();

    void updateDisplayName();

    UUID getUniqueID();

    @Override
    default boolean isPlayer() {
        return true;
    }

    @Override
    default RavelPlayer asPlayer() {
        return this;
    }

    RavelRank getRank();

    void setRank(RavelRank rank);

    default String buildDisplayName() {
        RavelRank rank = this.getRank();

        if (rank == RavelRank.NONE) {
            return this.getName();
        } else {
            return "[" + this.getRank().getName() + "] " + this.getName();
        }
    }

    @Override
    default boolean isOp() {
        return this.getRank().isOperator();
    }

    RavelServer getServer();

    void setServer(RavelServer server);

    RavelServer getOwnerProxy();

    Language getLanguage();

    void setLanguage(Language language);
}
