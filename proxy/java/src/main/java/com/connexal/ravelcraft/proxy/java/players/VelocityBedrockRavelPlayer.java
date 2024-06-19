package com.connexal.ravelcraft.proxy.java.players;

import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.server.players.PlayerManager;
import com.connexal.ravelcraft.shared.server.players.RavelPlayer;
import com.connexal.ravelcraft.shared.server.players.RavelRank;
import com.connexal.ravelcraft.shared.server.util.server.RavelServer;
import com.connexal.ravelcraft.shared.all.text.Language;
import com.connexal.ravelcraft.shared.all.text.RavelText;

import java.util.UUID;

public class VelocityBedrockRavelPlayer implements RavelPlayer {
    private final UUID uuid;
    private final String name;
    private String displayName;
    private RavelRank rank;
    private Language language;
    private RavelServer server;

    public VelocityBedrockRavelPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;

        this.server = RavelServer.DEFAULT_SERVER;
        PlayerManager.PlayerSettings settings = RavelInstance.getPlayerManager().getPlayerSettings(this.uuid, true);
        this.rank = settings.rank();
        this.language = settings.language();

        this.updateDisplayName();
    }

    @Override
    public void sendMessage(RavelText message, String... values) {
        String[] args = new String[values.length + 2];
        args[0] = this.uuid.toString();
        args[1] = message.name();
        System.arraycopy(values, 0, args, 2, values.length);

        RavelInstance.getMessager().sendCommand(RavelServer.BE_PROXY, MessagingCommand.PROXY_SEND_MESSAGE, args);
    }

    @Override
    public String getName() {
        return this.name;
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
