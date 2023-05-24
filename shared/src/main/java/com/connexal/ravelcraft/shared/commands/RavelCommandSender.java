package com.connexal.ravelcraft.shared.commands;

import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.text.Text;

public interface RavelCommandSender {
    void sendMessage(Text message, String... values);

    default void sendMessage(Text message) {
        sendMessage(message, new String[0]);
    }

    boolean isOp();

    boolean isPlayer();

    default RavelPlayer asPlayer() {
        return (RavelPlayer) this;
    }
}
