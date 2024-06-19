package com.connexal.ravelcraft.shared.server.util.uuid;

import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.players.RavelPlayer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

class UUIDApi {
    private static final String API_KEY = "4857adc8-e4c8-4332-b719-ad2c33f328da"; //Maybe not terribly intelligent

    private static final String JAVA_UUID_URL = "https://mcprofile.io/api/v1/java/username/%s";
    private static final String JAVA_NAME_URL = "https://mcprofile.io/api/v1/java/uuid/%s";
    private static final String BEDROCK_UUID_URL = "https://mcprofile.io/api/v1/bedrock/gamertag/%s";
    private static final String BEDROCK_NAME_URL = "https://mcprofile.io/api/v1/bedrock/fuid/%s";

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();

    public static UUID getUUID(String name) {
        try {
            boolean isBedrock = name.startsWith(RavelPlayer.BEDROCK_PREFIX);
            URL url;

            if (isBedrock) { //Bedrock name
                url = new URL(String.format(BEDROCK_UUID_URL, name.substring(1)));
            } else { //Java name
                url = new URL(String.format(JAVA_UUID_URL, name));
            }

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "ImDaBigBoss/1.0 (RavelCraft)");
            connection.setRequestProperty("x-api-key", API_KEY);
            connection.setReadTimeout(5000);
            if (connection.getResponseCode() != 200) {
                //Get the raw response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                throw new RuntimeException("Unable to get UUID for " + name + ": " + builder);
            }

            if (isBedrock) {
                BedrockResponse response = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), BedrockResponse.class);
                return UUID.fromString(response.floodgateuid);
            } else {
                JavaResponse response = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), JavaResponse.class);
                return UUID.fromString(response.uuid);
            }
        } catch (Exception e) {
            RavelInstance.getLogger().error("Unable to get UUID for " + name, e);
            return null;
        }
    }

    public static String getName(UUID uuid) {
        try {
            boolean isBedrock = uuid.getMostSignificantBits() == 0;
            URL url;

            if (isBedrock) { //Bedrock UUID
                url = new URL(String.format(BEDROCK_NAME_URL, uuid));
            } else { //Java UUID
                url = new URL(String.format(JAVA_NAME_URL, uuid));
            }

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "ImDaBigBoss/1.0 (RavelCraft)");
            connection.setRequestProperty("x-api-key", API_KEY);
            connection.setReadTimeout(5000);
            if (connection.getResponseCode() != 200) {
                //Get the raw response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                throw new RuntimeException("Unable to get name for " + uuid + ": " + builder);
            }

            if (isBedrock) {
                BedrockResponse response = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), BedrockResponse.class);
                return RavelPlayer.BEDROCK_PREFIX + response.gamertag;
            } else {
                JavaResponse response = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), JavaResponse.class);
                return response.username;
            }
        } catch (Exception e) {
            RavelInstance.getLogger().error("Unable to get name for " + uuid.toString(), e);
            return null;
        }
    }

    public static class BedrockResponse {
        public String gamertag;
        public String xuid;
        public String floodgateuid;
        public String icon;
        public String gamescore;
        public String accounttier;
        public String textureid;
        public String skin;
        public boolean linked;
        public String java_uuid;
        public String java_name;
    }

    public static class JavaResponse {
        public String username;
        public String uuid;
        public String skin;
        public String cape;
        public boolean linked;
        public String bedrock_gamertag;
        public String bedrock_xuid;
        public String bedrock_fuid;
    }
}
