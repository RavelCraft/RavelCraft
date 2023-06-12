package com.connexal.ravelcraft.proxy.java.players;

import com.connexal.ravelcraft.proxy.cross.players.ProxyRavelPlayer;
import com.connexal.ravelcraft.proxy.cross.servers.ProxyType;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.util.RavelServer;
import com.connexal.ravelcraft.shared.util.text.Text;

import java.util.UUID;

public class BedrockRavelPlayerImpl implements ProxyRavelPlayer {
    private final UUID uuid;
    private final String name;
    private String displayName;

    public BedrockRavelPlayerImpl(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;

        this.updateDisplayName();
    }

    @Override
    public void sendMessage(Text message, String... values) {
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
    public ProxyType getOwner() {
        return ProxyType.BEDROCK;
    }
}
