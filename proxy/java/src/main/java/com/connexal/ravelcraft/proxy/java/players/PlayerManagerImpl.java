package com.connexal.ravelcraft.proxy.java.players;

import com.connexal.ravelcraft.proxy.cross.players.ProxyPlayerManagerImpl;

import java.util.UUID;

public class PlayerManagerImpl extends ProxyPlayerManagerImpl {
    @Override
    protected void playerJoinedProxyCommand(UUID uuid, String name) {
        this.playerJoinedInternal(new BedrockRavelPlayerImpl(uuid, name));
    }

    @Override
    protected void playerLeftProxyCommand(UUID uuid) {
        this.playerLeftInternal(uuid);
    }
}
