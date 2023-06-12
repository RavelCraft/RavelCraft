package com.connexal.ravelcraft.mod.server.players;

import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class RavelPlayerImpl implements RavelPlayer {
    private final ServerPlayerEntity player;

    public RavelPlayerImpl(ServerPlayerEntity player) {
        this.player = player;
        this.updateDisplayName();
    }

    @Override
    public void sendMessage(Text message, String... values) {
        String messageString = message.getMessage(this.getLanguage(), values);
        this.player.sendMessage(net.minecraft.text.Text.literal(messageString));
    }

    @Override
    public String getName() {
        return this.player.getName().getString();
    }

    @Override
    public String displayName() {
        return this.player.getCustomName().getString();
    }

    @Override
    public void updateDisplayName() {
        this.player.setCustomName(net.minecraft.text.Text.literal(this.buildDisplayName()));
        this.player.setCustomNameVisible(true);
    }

    @Override
    public UUID getUniqueID() {
        return this.player.getUuid();
    }
}
