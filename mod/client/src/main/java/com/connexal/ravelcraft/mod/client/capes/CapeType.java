package com.connexal.ravelcraft.mod.client.capes;

import com.connexal.ravelcraft.shared.all.Ravel;
import com.mojang.authlib.GameProfile;

public enum CapeType {
    RAVELCRAFT,
    OPTIFINE;

    public String getURL(GameProfile profile) {
        return switch (this) {
            case OPTIFINE -> "http://s.optifine.net/capes/" + profile.getName() + ".png";
            case RAVELCRAFT -> "https://db." + Ravel.SERVER_IP + "/capes/" + profile.getId().toString() + ".png";
        };
    }
}
