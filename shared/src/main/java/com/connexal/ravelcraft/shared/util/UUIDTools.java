package com.connexal.ravelcraft.shared.util;

import java.util.UUID;

public class UUIDTools {
    public static UUID getJavaUUIDFromXUID(String xuid) {
        try {
            long xuidLong = Long.parseLong(xuid);
            return new UUID(0, xuidLong);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public UUID getUUID(String name) {
        //TODO: Get UUID from name
        return null;
    }

    public String getName(UUID uuid) {
        //TODO: Get name from UUID
        return null;
    }
}
