package com.connexal.ravelcraft.mod.server.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Util;

import java.util.Base64;
import java.util.UUID;

public class SkullUtils {
    public static ItemStack getSkullFromBase64(String base64, String name, UUID uuid) {
        ItemStack item = Items.PLAYER_HEAD.getDefaultStack();

        GameProfile profile = new GameProfile(uuid == null ? Util.NIL_UUID : uuid, name);
        profile.getProperties().put("textures", new Property("textures", base64));

        item.set(DataComponentTypes.PROFILE, new ProfileComponent(profile));

        return item;
    }

    public static ItemStack getSkull(String url, String name) {
        if (url.isEmpty()) {
            return null;
        }

        String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}";
        byte[] encodedData = Base64.getEncoder().encode(json.getBytes());

        return getSkullFromBase64(new String(encodedData), name, null);
    }

    public static ItemStack getSkull(UUID owner, boolean isJava) {
        //TODO: Implement skull from UUID
        return null;
    }
}
