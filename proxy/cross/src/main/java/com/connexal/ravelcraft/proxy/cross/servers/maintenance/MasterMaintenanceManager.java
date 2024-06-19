package com.connexal.ravelcraft.proxy.cross.servers.maintenance;

import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.all.util.RavelConfig;
import com.connexal.ravelcraft.shared.server.util.server.RavelServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class MasterMaintenanceManager extends MaintenanceManager {
    private final RavelConfig config;

    MasterMaintenanceManager() {
        super();

        this.messager.registerCommandHandler(MessagingCommand.PROXY_MAINTENANCE_GET, this::getEnabledCommand);
        this.config = RavelInstance.getConfig();

        this.ensureVariables();
    }

    @Override
    protected void ensureVariables() {
        if (this.globalEnabled != null) {
            return;
        }

        this.globalEnabled = false;
        this.serversEnabled = new ArrayList<>();

        if (this.config.contains("maintenance-mode") && this.config.isBoolean("maintenance-mode")) {
            this.globalEnabled = this.config.getBoolean("maintenance-mode");
        }

        if (this.config.contains("maintenance-servers") && this.config.isList("maintenance-servers")) {
            List<String> enabled = this.config.getStringList("maintenance-servers");
            for (String server : enabled) {
                try {
                    this.serversEnabled.add(RavelServer.valueOf(server.toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException e) {
                    RavelInstance.getLogger().error("Invalid server in maintenance enabled list: " + server);
                }
            }
        }
    }

    @Override
    protected void setEnabledChildInternal(RavelServer server, boolean enabled) {
        if (server == null) {
            this.config.set("maintenance-mode", enabled);
        } else {
            this.config.set("maintenance-servers", this.serversEnabled.stream().map(RavelServer::name).toList());
        }
        this.config.save();
    }

    private String[] getEnabledCommand(RavelServer source, String[] args) {
        if (args.length != 0) {
            RavelInstance.getLogger().error("Invalid maintenance get command!");
            return new String[0];
        }

        return new String[] { Boolean.toString(this.globalEnabled), this.serversEnabled.stream().map(RavelServer::name).reduce((a, b) -> a + "," + b).orElse("") };
    }
}
