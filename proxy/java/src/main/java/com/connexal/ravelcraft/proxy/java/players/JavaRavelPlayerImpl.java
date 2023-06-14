package com.connexal.ravelcraft.proxy.java.players;

import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class JavaRavelPlayerImpl implements RavelPlayer {
    private final Player player;
    private String displayName;

    public JavaRavelPlayerImpl(Player player) {
        this.player = player;

        this.updateDisplayName();
    }

    @Override
    public void sendMessage(Text message, String... values) {
        String messageString = message.getMessage(this.getLanguage(), values);
        this.player.sendMessage(Component.text(messageString));
    }

    @Override
    public String getName() {
        return this.player.getUsername();
    }

    @Override
    public String displayName() {
        return this.displayName;
    }

    @Override
    public void updateDisplayName() {
        this.displayName = this.buildDisplayName();
    }

    @Override
    public UUID getUniqueID() {
        return this.player.getUniqueId();
    }

    @Override
    public RavelServer getOwnerProxy() {
        return RavelServer.JE_PROXY;
    }
}
