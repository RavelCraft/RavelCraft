package com.connexal.ravelcraft.mod.server.players;

import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import com.connexal.ravelcraft.shared.util.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import org.geysermc.geyser.api.GeyserApi;

import java.util.UUID;

public class RavelPlayerImpl implements RavelPlayer {
    private final ServerPlayerEntity player;
    private final RavelServer ownerProxy;

    public RavelPlayerImpl(ServerPlayerEntity player) {
        this.player = player;
        this.updateDisplayName();

        if (GeyserApi.api().isBedrockPlayer(player.getUuid())) {
            this.ownerProxy = RavelServer.BE_PROXY;
        } else {
            this.ownerProxy = RavelServer.JE_PROXY;
        }
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

    @Override
    public RavelServer getOwnerProxy() {
        return this.ownerProxy;
    }
}
