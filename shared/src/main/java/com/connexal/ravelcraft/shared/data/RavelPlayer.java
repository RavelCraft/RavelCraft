package com.connexal.ravelcraft.shared.data;

import java.util.UUID;

public class RavelPlayer {
    private final String name;
    private final UUID uuid;
    private final RavelRank rank;

    public RavelPlayer(String name, UUID uuid, RavelRank rank) {
        this.name = name;
        this.uuid = uuid;
        this.rank = rank;
    }
}
