package com.connexal.ravelcraft.shared.server.commands;

import com.connexal.ravelcraft.shared.server.players.RavelPlayer;
import com.connexal.ravelcraft.shared.all.text.RavelText;

public interface RavelCommandSender {
    void sendMessage(RavelText message, String... values);

    default void sendMessage(RavelText message) {
        sendMessage(message, new String[0]);
    }

    boolean isOp();

    boolean isPlayer();

    default RavelPlayer asPlayer() {
        return (RavelPlayer) this;
    }
}
