package com.connexal.ravelcraft.mod.client.capes;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CapeProvider {
    private static final Map<UUID, Identifier> capes = new HashMap<>();

    private static NativeImage uncropImage(NativeImage image) {
        int srcHeight = image.getHeight();
        int srcWidth = image.getWidth();
        int zoom = (int) Math.ceil(image.getHeight() / 32f);

        NativeImage out = new NativeImage(64 * zoom, 32 * zoom, true);
        for (int x = 0; x < srcWidth; x++) {
            for (int y = 0; y < srcHeight; y++) {
                out.setColor(x, y, image.getColor(x, y));
            }
        }

        return out;
    }

    public static void loadCape(GameProfile profile, CapeTextureAvailableCallback callback) {
        new Thread(() -> {
            if (capes.containsKey(profile.getId())) {
                callback.onTextureAvailable(capes.get(profile.getId()));
                return;
            }

            for (CapeType type : CapeType.values()) {
                try {
                    URL url = new URL(type.getURL(profile));
                    NativeImage texture = uncropImage(NativeImage.read(url.openStream()));
                    NativeImageBackedTexture textureWrapper = new NativeImageBackedTexture(texture);

                    Identifier identifier = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("ravelcape" + profile.getId().toString().replace("-", ""), textureWrapper);
                    capes.put(profile.getId(), identifier);
                    callback.onTextureAvailable(identifier);

                    return;
                } catch (FileNotFoundException e) {
                    // Ignore it, this means that the user has no cape for this type
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load cape of type " + type.name() + " for " + profile.getName(), e);
                }
            }
        }).start();
    }
}
