package com.connexal.ravelcraft.mod.client.capes;

import com.connexal.ravelcraft.mod.cross.BuildConstants;
import com.mojang.authlib.GameProfile;

public enum CapeType {
    RAVELCRAFT,
    OPTIFINE;

    public String getURL(GameProfile profile) {
        return switch (this) {
            case OPTIFINE -> "http://s.optifine.net/capes/" + profile.getName() + ".png";
            case RAVELCRAFT -> "https://db." + BuildConstants.SERVER_IP + "/capes/" + profile.getId().toString() + ".png";
        };
    }
}
