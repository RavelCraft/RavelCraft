package com.connexal.ravelcraft.proxy.cross.servers.whitelist;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.util.RavelConfig;
import com.connexal.ravelcraft.shared.util.server.RavelServer;

import java.util.*;

class MasterWhitelistManager extends WhitelistManager {
    private final RavelConfig config;

    MasterWhitelistManager() {
        super();

        this.messager.registerCommandHandler(MessagingCommand.PROXY_WHITELIST_ENABLED_GET, this::getEnabledCommand);
        this.messager.registerCommandHandler(MessagingCommand.PROXY_WHITELIST_GET, this::getWhitelistedCommand);

        this.config = RavelInstance.getConfig("whitelist");
        this.ensureVariables();

        if (this.config.contains("whitelist") && this.config.isList("whitelist")) {
            this.whitelist.addAll(this.config.getUUIDList("whitelist"));
        }

        if (this.config.contains("enabled") && this.config.isList("enabled")) {
            List<String> enabled = this.config.getStringList("enabled");
            for (String server : enabled) {
                try {
                    this.enabledServers.add(RavelServer.valueOf(server.toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException e) {
                    RavelInstance.getLogger().error("Invalid server in whitelist enabled list: " + server);
                }
            }
        }

        for (RavelServer server : RavelServer.values()) {
            String serverName = server.name().toLowerCase();
            if (server.isProxy() || !this.config.contains(serverName)) {
                continue;
            }

            String path = "server." + serverName;
            if (this.config.contains(path) && this.config.isList(path)) {
                this.backendWhitelist.put(server, this.config.getUUIDList(path));
            }
        }
    }

    @Override
    protected void ensureVariables() {
        if (this.whitelist != null) {
            return;
        }

        this.whitelist = new ArrayList<>();
        this.enabledServers = new ArrayList<>();
        this.backendWhitelist = new HashMap<>();
    }

    @Override
    protected void setEnabledChildInternal(RavelServer server, boolean enabled) {
        this.config.set("enabled", this.enabledServers.stream().map(RavelServer::name).toList());
        this.config.save();
    }

    @Override
    protected void setWhitelistedChildInternal(UUID uuid, boolean whitelisted, RavelServer server) {
        List<String> whitelistString;
        String path;

        if (server == null) {
            whitelistString = this.whitelist.stream().map(UUID::toString).toList();
            path = "whitelist";
        } else {
            whitelistString = this.backendWhitelist.get(server).stream().map(UUID::toString).toList();
            path = "server." + server.name().toLowerCase();
        }

        this.config.set(path, whitelistString);
        this.config.save();
    }

    private String[] getEnabledCommand(RavelServer source, String[] args) {
        if (args.length != 0) {
            RavelInstance.getLogger().error("Invalid whitelist servers get command!");
            return new String[0];
        }

        String enabledList = this.enabledServers.stream().map(RavelServer::name).reduce((a, b) -> a + "," + b).orElse("");
        return new String[] { enabledList };
    }

    private String[] getWhitelistedCommand(RavelServer source, String[] args) {
        if (args.length != 0) {
            RavelInstance.getLogger().error("Invalid whitelisted get command!");
            return new String[0];
        }

        String[] output = new String[this.backendWhitelist.size() + 1]; // +1 for the master whitelist
        output[0] = this.whitelist.stream().map(UUID::toString).reduce((a, b) -> a + "," + b).orElse("");

        int i = 0;
        for (Map.Entry<RavelServer, List<UUID>> entry : this.backendWhitelist.entrySet()) {
            i++;

            output[i] = entry.getKey().name() + ";" + entry.getValue().stream().map(UUID::toString).reduce((a, b) -> a + "," + b).orElse("");
        }

        return output;
    }
}
