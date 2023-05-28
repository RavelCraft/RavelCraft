package com.connexal.ravelcraft.proxy.cross.players;

import com.connexal.ravelcraft.proxy.cross.servers.ProxyType;
import com.connexal.ravelcraft.shared.players.RavelPlayer;

public interface ProxyRavelPlayer extends RavelPlayer {
    ProxyType getOwner();
}
