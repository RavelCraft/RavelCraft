package com.connexal.ravelcraft.proxy.bedrock.players;

import com.connexal.ravelcraft.proxy.cross.players.ProxyRavelPlayer;
import com.connexal.ravelcraft.proxy.cross.servers.ProxyType;
import com.connexal.ravelcraft.shared.util.text.Text;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

import java.util.UUID;

public class BedrockRavelPlayerImpl implements ProxyRavelPlayer {
    private final ProxiedPlayer player;

    public BedrockRavelPlayerImpl(ProxiedPlayer player) {
        this.player = player;
    }

    @Override
    public void sendMessage(Text message, String... values) {
        String messageString = message.getMessage(this.getLanguage(), values);
        this.player.sendMessage(messageString);
    }

    @Override
    public String getName() {
        return this.player.getName();
    }

    @Override
    public UUID getUniqueID() {
        return this.player.getUniqueId();
    }

    @Override
    public ProxyType getOwner() {
        return ProxyType.BEDROCK;
    }
}
