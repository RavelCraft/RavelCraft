package com.connexal.ravelcraft.proxy.bedrock.players;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.PlayerManager;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.players.RavelRank;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import com.connexal.ravelcraft.shared.util.text.Language;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.connexal.ravelcraft.shared.util.uuid.UUIDTools;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

import java.util.UUID;

public class WaterdogBedrockRavelPlayer implements RavelPlayer {
    private final ProxiedPlayer player;
    private final UUID uuid;
    private String displayName;
    private RavelRank rank;
    private Language language;
    private RavelServer server;

    public WaterdogBedrockRavelPlayer(ProxiedPlayer player) {
        this.player = player;
        this.uuid = UUIDTools.getJavaUUIDFromXUID(player.getXuid());

        this.server = RavelServer.DEFAULT_SERVER;
        PlayerManager.PlayerSettings settings = RavelInstance.getPlayerManager().getPlayerSettings(this.uuid, true);
        this.rank = settings.rank();
        this.language = settings.language();

        this.updateDisplayName();
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
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
        return RavelServer.BE_PROXY;
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
