package com.connexal.ravelcraft.proxy.bedrock.players;

import com.connexal.ravelcraft.proxy.cross.players.ProxyRavelPlayer;
import com.connexal.ravelcraft.proxy.cross.servers.ProxyType;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.util.RavelServer;
import com.connexal.ravelcraft.shared.util.text.Text;

import java.util.UUID;

public class JavaRavelPlayerImpl implements ProxyRavelPlayer {
    private final UUID uuid;
    private final String name;

    public JavaRavelPlayerImpl(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public void sendMessage(Text message, String... values) {
        RavelInstance.getMessager().sendCommand(RavelServer.JE_PROXY, MessagingCommand.PROXY_SEND_MESSAGE, this.uuid.toString(), message.name());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public UUID getUniqueID() {
        return this.uuid;
    }

    @Override
    public ProxyType getOwner() {
        return ProxyType.JAVA;
    }
}
