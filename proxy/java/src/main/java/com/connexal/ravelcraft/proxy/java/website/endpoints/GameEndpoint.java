package com.connexal.ravelcraft.proxy.java.website.endpoints;

import com.connexal.ravelcraft.proxy.java.website.*;
import com.connexal.ravelcraft.shared.RavelInstance;

public class GameEndpoint extends AbstractEndpoint {
    public GameEndpoint() {
        super(EndpointType.SIMPLE, "/game");
    }

    @Override
    protected PageReturn getPageContents(WebServer server, PageRequest request) {
        if (!request.getQueries().containsKey("username") || !request.getQueries().containsKey("password") || !request.getQueries().containsKey("name")) {
            return new PageReturn("Invalid request", 200, true);
        }

        String username = request.getQueries().get("username");
        String password = request.getQueries().get("password");
        String name = request.getQueries().get("name");

        boolean out;
        try {
            //out = RavelInstance.getCrackedPlayerManager().crackedPlayerLogin(name, username, password);
            out = true;
        } catch (IllegalArgumentException e) {
            return new PageReturn("Invalid UUID", 200, true);
        }

        if (out) {
            RavelInstance.getLogger().info("Player " + name + " logged in with username " + username);
            return new PageReturn("OK", 200, true);
        } else {
            return new PageReturn("Invalid username or password", 200, true);
        }
    }
}

