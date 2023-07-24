package com.connexal.ravelcraft.mod.server.players;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.PlayerManager;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.players.RavelRank;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import com.connexal.ravelcraft.shared.util.text.Language;
import com.connexal.ravelcraft.shared.util.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import org.geysermc.geyser.api.GeyserApi;

import java.util.UUID;

public class RavelPlayerImpl implements RavelPlayer {
    private final ServerPlayerEntity player;
    private final RavelServer ownerProxy;
    private RavelRank rank;
    private Language language;
    private RavelServer server;

    public RavelPlayerImpl(ServerPlayerEntity player) {
        this.player = player;

        if (GeyserApi.api().isBedrockPlayer(player.getUuid())) {
            this.ownerProxy = RavelServer.BE_PROXY;
        } else {
            this.ownerProxy = RavelServer.JE_PROXY;
        }

        this.server = RavelInstance.getServer();
        PlayerManager.PlayerSettings settings = RavelInstance.getPlayerManager().getPlayerSettings(player.getUuid(), true);
        this.rank = settings.rank();
        this.language = settings.language();

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

    @Override
    public RavelRank getRank() {
        return this.rank;
    }

    @Override
    public void setRank(RavelRank rank) {
        this.rank = rank;
    }

    @Override
    public RavelServer getServer() {
        return this.server;
    }

    @Override
    public void setServer(RavelServer server) {
        this.server = server;
    }

    @Override
    public RavelServer getOwnerProxy() {
        return this.ownerProxy;
    }

    @Override
    public Language getLanguage() {
        return this.language;
    }

    @Override
    public void setLanguage(Language language) {
        this.language = language;
    }
}
