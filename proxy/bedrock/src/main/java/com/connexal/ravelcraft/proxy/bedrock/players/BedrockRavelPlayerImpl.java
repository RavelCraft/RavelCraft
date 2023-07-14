package com.connexal.ravelcraft.proxy.bedrock.players;

import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.uuid.UUIDTools;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import com.connexal.ravelcraft.shared.util.text.Text;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

import java.util.UUID;

public class BedrockRavelPlayerImpl implements RavelPlayer {
    private final ProxiedPlayer player;
    private final UUID uuid;
    private String displayName;

    public BedrockRavelPlayerImpl(ProxiedPlayer player) {
        this.player = player;
        this.uuid = UUIDTools.getJavaUUIDFromXUID(player.getXuid());

        this.updateDisplayName();
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
    public String displayName() {
        return this.displayName;
    }

    @Override
    public void updateDisplayName() {
        this.displayName = this.buildDisplayName();
    }

    @Override
    public UUID getUniqueID() {
        return this.uuid;
    }

    @Override
    public RavelServer getOwnerProxy() {
        return RavelServer.BE_PROXY;
    }
}
