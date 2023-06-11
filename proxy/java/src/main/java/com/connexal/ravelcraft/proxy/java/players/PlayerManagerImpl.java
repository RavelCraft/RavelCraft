package com.connexal.ravelcraft.proxy.java.players;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.PlayerManager;
import com.connexal.ravelcraft.shared.util.RavelServer;

import java.util.UUID;

public class PlayerManagerImpl extends PlayerManager {
    @Override
    protected String[] playerJoinedProxyCommand(RavelServer source, String[] args) {
        if (args.length != 2) {
            RavelInstance.getLogger().error("Invalid number of arguments for playerJoinedProxy command!");
            return null;
        }

        UUID uuid = UUID.fromString(args[0]);
        this.playerJoinedInternal(new BedrockRavelPlayerImpl(uuid, args[1]));
        return null;
    }

    @Override
    protected String[] playerLeftProxyCommand(RavelServer source, String[] args) {
        if (args.length != 1) {
            RavelInstance.getLogger().error("Invalid number of arguments for playerLeftProxy command!");
            return null;
        }

        UUID uuid = UUID.fromString(args[0]);
        this.playerLeftInternal(uuid);
        return null;
    }
}
