package com.connexal.ravelcraft.proxy.java.players;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.PlayerManager;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.players.RavelRank;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import com.connexal.ravelcraft.shared.util.text.Language;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class JavaRavelPlayerImpl implements RavelPlayer {
    private final Player player;
    private String displayName;
    private RavelRank rank;
    private Language language;
    private RavelServer server;

    public JavaRavelPlayerImpl(Player player) {
        this.player = player;

        this.server = RavelServer.DEFAULT_SERVER;
        PlayerManager.PlayerSettings settings = RavelInstance.getPlayerManager().getPlayerSettings(this.player.getUniqueId(), true);
        this.rank = settings.rank();
        this.language = settings.language();

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
        return RavelServer.JE_PROXY;
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
