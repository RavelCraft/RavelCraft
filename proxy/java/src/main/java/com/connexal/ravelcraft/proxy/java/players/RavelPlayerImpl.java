package com.connexal.ravelcraft.proxy.java.players;

import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class RavelPlayerImpl implements RavelPlayer {
    private final Player player;

    public RavelPlayerImpl(Player player) {
        this.player = player;
    }

    @Override
    public void sendMessage(Text message, String... values) {
        String messageString = message.getMessage(this.getLanguage(), values);
        this.player.sendMessage(Component.text(messageString));
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public String getName() {
        return this.player.getUsername();
    }

    @Override
    public UUID getUniqueID() {
        return this.player.getUniqueId();
    }
}
