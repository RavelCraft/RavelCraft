package com.connexal.ravelcraft.mod.server.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Util;

import java.util.Base64;
import java.util.UUID;

public class SkullUtils {
    public static ItemStack getSkullFromBase64(String base64, String name) {
        ItemStack item = Items.PLAYER_HEAD.getDefaultStack();

        NbtCompound nbtCompound = new NbtCompound();
        NbtCompound display = new NbtCompound();
        NbtCompound skullOwner = new NbtCompound();
        NbtCompound properties = new NbtCompound();
        NbtList textures = new NbtList();
        NbtCompound valueData = new NbtCompound();

        display.putString("Name", name);
        nbtCompound.put("display", display);

        valueData.putString("Value", base64);
        textures.add(valueData);
        properties.put("textures", textures);
        skullOwner.put("Properties", properties);
        nbtCompound.put("SkullOwner", skullOwner);

        item.setNbt(nbtCompound);
        return item;
    }

    public static ItemStack getSkull(String url, String name) {
        if (url.isEmpty()) {
            return null;
        }

        String json = "{\n  \"textures\" : {    \"SKIN\" : {      \"url\" : " + url + "    }\n  }\n}";
        byte[] encodedData = Base64.getEncoder().encode(json.getBytes());

        return getSkullFromBase64(new String(encodedData), name);
    }

    public static ItemStack getSkull(UUID owner, boolean isJava) {
        //TODO: Implement skull from UUID
        return null;
    }
}
