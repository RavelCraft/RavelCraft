package com.connexal.ravelcraft.mod.server.players;

import com.connexal.ravelcraft.shared.players.PlayerManager;
import com.connexal.ravelcraft.shared.util.RavelServer;

public class PlayerManagerImpl extends PlayerManager {
    @Override
    protected String[] playerJoinedProxyCommand(RavelServer source, String[] args) {
        throw new UnsupportedOperationException("The Fabric server shouldn't ever receive the player joined proxy command");
    }

    @Override
    protected String[] playerLeftProxyCommand(RavelServer source, String[] args) {
        throw new UnsupportedOperationException("The Fabric server shouldn't ever receive the player left proxy command");
    }
}
