package com.connexal.ravelcraft.proxy.cross.servers.maintenance;

import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.server.messaging.MessagingConstants;
import com.connexal.ravelcraft.shared.server.util.server.RavelServer;

import java.util.ArrayList;
import java.util.Locale;

class SlaveMaintenanceManager extends MaintenanceManager {
    SlaveMaintenanceManager() {
        super();
    }

    @Override
    protected void ensureVariables() {
        if (this.globalEnabled != null) {
            return;
        }

        //Enabled servers
        String[] maintenanceResponse = this.messager.sendCommandWithResponse(MessagingConstants.MESSAGING_SERVER, MessagingCommand.PROXY_MAINTENANCE_GET);
        if (maintenanceResponse == null || maintenanceResponse.length != 2) {
            throw new IllegalStateException("Failed to get maintenance servers from master!");
        }

        try {
            this.globalEnabled = Boolean.parseBoolean(maintenanceResponse[0]);
        } catch (IllegalArgumentException e) {
            this.globalEnabled = false;
            RavelInstance.getLogger().error("Invalid global maintenance enabled value: " + maintenanceResponse[0]);
        }

        this.serversEnabled = new ArrayList<>();
        for (String serverName : maintenanceResponse[1].split(",")) {
            if (serverName.isEmpty()) {
                continue;
            }

            try {
                this.serversEnabled.add(RavelServer.valueOf(serverName.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException e) {
                RavelInstance.getLogger().error("Invalid server in maintenance enabled list: " + serverName);
            }
        }
    }

    @Override
    protected void setEnabledChildInternal(RavelServer server, boolean enabled) {
        //Nothing
    }
}
