package com.connexal.ravelcraft.proxy.cross.servers.maintenance;

import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.Messager;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.messaging.MessagingConstants;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.players.RavelRank;
import com.connexal.ravelcraft.shared.util.server.RavelServer;

import java.util.List;
import java.util.Locale;

public abstract class MaintenanceManager {
    protected Boolean globalEnabled = null;
    protected List<RavelServer> serversEnabled = null;

    protected final Messager messager;

    public MaintenanceManager() {
        this.messager = RavelInstance.getMessager();
        this.messager.registerCommandHandler(MessagingCommand.PROXY_MAINTENANCE_SET, this::setEnabledCommand);

        this.messager.registerDisconnectHandler(server -> {
            this.globalEnabled = null;
            this.serversEnabled = null;
        });
    }

    public static MaintenanceManager create() {
        if (MessagingConstants.isServer()) {
            return new MasterMaintenanceManager();
        } else {
            return new SlaveMaintenanceManager();
        }
    }

    protected abstract void ensureVariables();

    public boolean canBypass(RavelPlayer player) {
        return player.getRank() == RavelRank.DEV || player.getRank() == RavelRank.OWNER;
    }

    public boolean isEnabled(RavelServer server) {
        this.ensureVariables();
        if (server != null && server.isProxy()) {
            throw new IllegalArgumentException("Invalid server!");
        }

        if (server == null) {
            return this.globalEnabled;
        } else {
            return this.serversEnabled.contains(server);
        }
    }

    public boolean isEnabled() {
        return this.isEnabled(null);
    }

    public void setEnabled(RavelServer server, boolean enabled) {
        if (server != null && server.isProxy()) {
            throw new IllegalArgumentException("Invalid server!");
        }

        this.setEnabledInternal(server, enabled);
        if (server == null) {
            this.messager.sendCommand(RavelProxyInstance.getOtherProxy(), MessagingCommand.PROXY_MAINTENANCE_SET, Boolean.toString(enabled));
        } else {
            this.messager.sendCommand(RavelProxyInstance.getOtherProxy(), MessagingCommand.PROXY_MAINTENANCE_SET, Boolean.toString(enabled), server.name());
        }
    }

    protected abstract void setEnabledChildInternal(RavelServer server, boolean enabled);

    private void setEnabledInternal(RavelServer server, boolean enabled) {
        this.ensureVariables();

        if (server == null) {
            this.globalEnabled = enabled;
        } else {
            if (enabled) {
                if (!this.serversEnabled.contains(server)) {
                    this.serversEnabled.add(server);
                }
            } else {
                this.serversEnabled.remove(server);
            }
        }

        this.setEnabledChildInternal(server, enabled);
    }

    private String[] setEnabledCommand(RavelServer source, String[] args) {
        if (args.length != 1 && args.length != 2) {
            RavelInstance.getLogger().error("Invalid server enable command!");
            return null;
        }

        boolean enabled;
        try {
            enabled = Boolean.parseBoolean(args[0]);
        } catch (IllegalArgumentException e) {
            RavelInstance.getLogger().error("Invalid boolean value!", e);
            return null;
        }

        RavelServer server;
        if (args.length == 2) {
            try {
                server = RavelServer.valueOf(args[1].toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                RavelInstance.getLogger().error("Invalid server!", e);
                return null;
            }
        } else {
            server = null;
        }

        this.setEnabledInternal(server, enabled);
        return null;
    }
}
