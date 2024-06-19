package com.connexal.ravelcraft.proxy.java.website.endpoints.api;

import com.connexal.ravelcraft.proxy.java.website.*;
import com.connexal.ravelcraft.proxy.java.website.endpoints.AbstractEndpoint;
import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.all.util.RavelConfig;
import com.velocitypowered.api.util.GameProfile;

import java.util.*;

public class AuthEndpoint extends AbstractEndpoint {
    private static final List<CrackedPlayerSession> crackedAuth = new ArrayList<>();
    private static RavelConfig config;

    public AuthEndpoint() {
        super(EndpointType.SIMPLE, "/api/auth");

        config = RavelInstance.getConfig("cracked");
    }

    @Override
    protected PageReturn getPageContents(WebServer server, PageRequest request) {
        String ouptut = null;
        WebSession session = request.getSession();

        if (!session.getData().containsKey("loggedIn")) {
            String name = request.getQueries().get("name");
            String username = request.getQueries().get("username");
            String password = request.getQueries().get("password");

            if (name != null && username != null && password != null) {
                if (crackedPlayerLogin(name, username, password)) {
                    session.getData().put("loggedIn", true);
                } else {
                    ouptut = "Invalid username or password";
                }
            } else {
                ouptut = "Username and password must be set";
            }
        }

        return new PageReturn(Objects.requireNonNullElse(ouptut, "OK"), 200, true);
    }

    public static void reloadConfig() {
        config.reload();
    }

    public static GameProfile getCrackedProfile(String name) {
        if (!name.startsWith("*")) {
            return null;
        }
        String configName = name.substring(1);

        int index = -1;
        for (int i = 0; i < crackedAuth.size(); i++) {
            CrackedPlayerSession entry = crackedAuth.get(i);
            if (entry.getName().equals(name)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return null;
        }

        CrackedPlayerSession entry = crackedAuth.get(index);
        crackedAuth.remove(index);

        if (System.currentTimeMillis() > entry.getTime() + (20 * 1000)) {
            return null;
        }

        if (!config.contains(configName)) {
            return null;
        }

        UUID profileUUID = UUID.fromString(config.getString(configName + ".uuid"));
        RavelInstance.getUUIDTools().registerPlayerData(profileUUID, name);

        return new GameProfile(profileUUID, name, Collections.singletonList(new GameProfile.Property("textures", "", "")));
    }

    public static boolean crackedPlayerLogin(String name, String username, String password) {
        if (!name.startsWith("*")) {
            return false;
        }
        String configName = name.substring(1);

        if (!config.contains(configName)) {
            return false;
        }

        if (!config.getString(configName + ".username").equals(username) || !config.getString(configName + ".password").equals(password)) {
            return false;
        }

        crackedAuth.add(new CrackedPlayerSession(name, System.currentTimeMillis()));
        return true;
    }

    public static class CrackedPlayerSession {
        private final String name;
        private final long time;

        public CrackedPlayerSession(String name, long time) {
            this.name = name;
            this.time = time;
        }

        public String getName() {
            return this.name;
        }

        public long getTime() {
            return this.time;
        }
    }

}
