package com.connexal.ravelcraft.mod.server.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeyserSkinGetter {
    private static final Gson gson = new GsonBuilder().create();

    public static SkinData getSkin(String xuid) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://api.geysermc.org/v2/skin/" + xuid).openConnection();
            connection.setReadTimeout(5000);

            GeyserSkinAPIData data = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), GeyserSkinAPIData.class);

            SkinData skinData = new SkinData();
            skinData.value = data.value;
            skinData.signature = data.signature;

            return skinData;
        } catch (Exception e) {
            return null;
        }
    }

    public static class SkinData {
        public String signature;
        public String value;
    }

    private static class GeyserSkinAPIData {
        public boolean isSteve;
        public long lastUpdate;
        public String signature;
        public String textureId;
        public String value;
    }
}
