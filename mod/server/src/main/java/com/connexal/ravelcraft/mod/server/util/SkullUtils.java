package com.connexal.ravelcraft.mod.server.util;

import com.connexal.ravelcraft.shared.RavelInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.Base64;
import java.util.UUID;

public class SkullUtils {
    public static ItemStack getSkullFromBase64(String base64, String name, UUID uuid) {
        ItemStack item = Items.PLAYER_HEAD.getDefaultStack();

        NbtCompound nbtCompound = new NbtCompound();
        NbtCompound skullOwner = new NbtCompound();
        NbtCompound properties = new NbtCompound();
        NbtCompound valueData = new NbtCompound();
        NbtList textures = new NbtList();

        valueData.putString("Value", base64);

        textures.add(valueData);
        properties.put("textures", textures);

        skullOwner.put("Id", NbtHelper.fromUuid(uuid == null ? Util.NIL_UUID : uuid));
        skullOwner.put("Properties", properties);
        nbtCompound.put("SkullOwner", skullOwner);

        item.getOrCreateNbt().copyFrom(nbtCompound);

        item.setCustomName(Text.literal(name));

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
