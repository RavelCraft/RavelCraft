package com.connexal.ravelcraft.proxy.bedrock.players;

import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.text.Text;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

import java.util.UUID;

public class RavelPlayerImpl implements RavelPlayer {
    private final ProxiedPlayer player;

    public RavelPlayerImpl(ProxiedPlayer player) {
        this.player = player;
    }

    @Override
    public void sendMessage(Text message, String... values) {
        String messageString = message.getMessage(this.getLanguage(), values);
        this.player.sendMessage(messageString);
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public String getName() {
        return this.player.getName();
    }

    @Override
    public UUID getUniqueID() {
        return this.player.getUniqueId();
    }
}
