package com.connexal.ravelcraft.proxy.cross.servers.whitelist;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.messaging.MessagingConstants;
import com.connexal.ravelcraft.shared.util.server.RavelServer;

import java.util.*;

class SlaveWhitelistManager extends WhitelistManager {
    SlaveWhitelistManager() {
        super();
    }

    @Override
    protected void ensureVariables() {
        if (this.whitelist != null) {
            return;
        }

        //Enabled servers
        String[] enabledResponse = this.messager.sendCommandWithResponse(MessagingConstants.MESSAGING_SERVER, MessagingCommand.PROXY_WHITELIST_ENABLED_GET);
        if (enabledResponse == null || enabledResponse.length != 1) {
            throw new IllegalStateException("Failed to get enabled servers from master!");
        }
        this.enabledServers = new ArrayList<>();
        for (String serverName : enabledResponse[0].split(",")) {
            if (serverName.isEmpty()) {
                continue;
            }

            try {
                this.enabledServers.add(RavelServer.valueOf(serverName.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException e) {
                RavelInstance.getLogger().error("Invalid server in whitelist enabled list: " + serverName);
            }
        }

        //Whitelist
        String[] whitelistResponse = this.messager.sendCommandWithResponse(MessagingConstants.MESSAGING_SERVER, MessagingCommand.PROXY_WHITELIST_GET);
        if (whitelistResponse == null || whitelistResponse.length < 1) {
            throw new IllegalStateException("Failed to get whitelist from master!");
        }

        //Proxy whitelist
        this.whitelist = new ArrayList<>();
        for (String uuidString : whitelistResponse[0].split(",")) {
            try {
                this.whitelist.add(UUID.fromString(uuidString));
            } catch (IllegalArgumentException e) {
                RavelInstance.getLogger().error("Invalid UUID in whitelist: " + uuidString);
            }
        }

        //Backend whitelist
        this.backendWhitelist = new HashMap<>();
        for (int i = 1 ; i < whitelistResponse.length; i++) {
            String[] serverPart = whitelistResponse[i].split(";");
            if (serverPart.length != 2) {
                continue;
            }

            String serverName = serverPart[0];
            RavelServer server;
            try {
                server = RavelServer.valueOf(serverName.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                RavelInstance.getLogger().error("Invalid server in whitelist: " + serverName);
                continue;
            }

            List<UUID> serverWhitelist = new ArrayList<>();
            String[] uuidStrings = serverPart[1].split(",");
            for (String uuidString : uuidStrings) {
                if (uuidString.isEmpty()) {
                    continue;
                }

                try {
                    serverWhitelist.add(UUID.fromString(uuidString));
                } catch (IllegalArgumentException e) {
                    RavelInstance.getLogger().error("Invalid UUID in whitelist for server " + serverName + ": " + uuidString);
                }
            }

            this.backendWhitelist.put(server, serverWhitelist);
        }
    }

    @Override
    protected void setEnabledChildInternal(RavelServer server, boolean enabled) {
        //Nothing
    }

    @Override
    protected void setWhitelistedChildInternal(UUID uuid, boolean whitelisted, RavelServer server) {
        //Nothing
    }
}
