package com.connexal.ravelcraft.proxy.cross.servers.whitelist;

import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.Messager;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.messaging.MessagingConstants;
import com.connexal.ravelcraft.shared.util.server.RavelServer;

import java.util.*;

public abstract class WhitelistManager {
    protected List<UUID> whitelist = null;
    protected List<RavelServer> enabledServers = null;
    protected Map<RavelServer, List<UUID>> backendWhitelist = null;

    protected final Messager messager;

    public WhitelistManager() {
        this.messager = RavelInstance.getMessager();
        this.messager.registerCommandHandler(MessagingCommand.PROXY_WHITELIST_ENABLED_SET, this::setEnabledCommand);
        this.messager.registerCommandHandler(MessagingCommand.PROXY_WHITELIST_SET, this::setWhitelistedCommand);

        this.messager.registerDisconnectHandler(server -> {
            this.whitelist = null;
            this.enabledServers = null;
            this.backendWhitelist = null;
        });
    }

    public static WhitelistManager create() {
        if (MessagingConstants.isServer()) {
            return new MasterWhitelistManager();
        } else {
            return new SlaveWhitelistManager();
        }
    }

    protected abstract void ensureVariables();

    //--- Enable ---

    public boolean isEnabled(RavelServer server) {
        if (server == null || server.isProxy()) {
            throw new IllegalArgumentException("Invalid server!");
        } else {
            this.ensureVariables();

            return this.enabledServers.contains(server);
        }
    }

    public void setEnabled(RavelServer server, boolean enabled) {
        if (server == null || server.isProxy()) {
            throw new IllegalArgumentException("Invalid server!");
        } else {
            this.setEnabledInternal(server, enabled);

            this.messager.sendCommand(RavelProxyInstance.getOtherProxy(), MessagingCommand.PROXY_WHITELIST_ENABLED_SET, server.name(), Boolean.toString(enabled));
        }
    }

    protected abstract void setEnabledChildInternal(RavelServer server, boolean enabled);

    private void setEnabledInternal(RavelServer server, boolean enabled) {
        this.ensureVariables();

        if (enabled) {
            if (!this.enabledServers.contains(server)) {
                this.enabledServers.add(server);
            }
        } else {
            this.enabledServers.remove(server);
        }

        this.setEnabledChildInternal(server, enabled);
    }

    private String[] setEnabledCommand(RavelServer source, String[] args) {
        if (args.length != 2) {
            RavelInstance.getLogger().error("Invalid server enable command!");
            return null;
        }

        RavelServer server;
        try {
            server = RavelServer.valueOf(args[0].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            RavelInstance.getLogger().error("Invalid server!", e);
            return null;
        }

        boolean enabled;
        try {
            enabled = Boolean.parseBoolean(args[1]);
        } catch (IllegalArgumentException e) {
            RavelInstance.getLogger().error("Invalid boolean value!", e);
            return null;
        }

        this.setEnabledInternal(server, enabled);
        return null;
    }

    //--- Whitelist get ---

    public boolean isWhitelisted(UUID uuid, RavelServer server) {
        this.ensureVariables();

        if (server == null) {
            return this.whitelist.contains(uuid);
        } else {
            if (server.isProxy()) {
                throw new IllegalArgumentException("Invalid server!");
            }

            List<UUID> whitelist = this.backendWhitelist.get(server);
            if (whitelist == null) {
                return false;
            } else {
                return whitelist.contains(uuid);
            }
        }
    }

    public boolean isWhitelisted(UUID uuid) {
        return this.isWhitelisted(uuid, null);
    }

    //--- Whitelist set ---

    public void setWhitelisted(UUID uuid, boolean whitelisted, RavelServer server) {
        if (server != null && server.isProxy()) {
            throw new IllegalArgumentException("Invalid server!");
        }

        this.setWhitelistedInternal(uuid, whitelisted, server);

        if (server == null) {
            this.messager.sendCommand(RavelProxyInstance.getOtherProxy(), MessagingCommand.PROXY_WHITELIST_SET, uuid.toString(), Boolean.toString(whitelisted));
        } else {
            this.messager.sendCommand(RavelProxyInstance.getOtherProxy(), MessagingCommand.PROXY_WHITELIST_SET, uuid.toString(), Boolean.toString(whitelisted), server.name());
        }
    }

    protected abstract void setWhitelistedChildInternal(UUID uuid, boolean whitelisted, RavelServer server);

    private void setWhitelistedInternal(UUID uuid, boolean whitelisted, RavelServer server) {
        this.ensureVariables();

        List<UUID> whitelist;
        if (server == null) {
            whitelist = this.whitelist;
        } else {
            whitelist = this.backendWhitelist.computeIfAbsent(server, k -> new ArrayList<>());
        }

        if (whitelisted) {
            if (!whitelist.contains(uuid)) {
                whitelist.add(uuid);
            }
        } else {
            whitelist.remove(uuid);
        }

        this.setWhitelistedChildInternal(uuid, whitelisted, server);
    }

    private String[] setWhitelistedCommand(RavelServer source, String[] args) {
        if (args.length != 2 && args.length != 3) {
            RavelInstance.getLogger().error("Invalid whitelist set command!");
            return null;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(args[0]);
        } catch (IllegalArgumentException e) {
            RavelInstance.getLogger().error("Invalid UUID!", e);
            return null;
        }

        boolean whitelisted;
        try {
            whitelisted = Boolean.parseBoolean(args[1]);
        } catch (IllegalArgumentException e) {
            RavelInstance.getLogger().error("Invalid boolean value!", e);
            return null;
        }

        RavelServer server = null;
        if (args.length == 3) {
            try {
                server = RavelServer.valueOf(args[2].toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                RavelInstance.getLogger().error("Invalid server!", e);
                return null;
            }
        }

        this.setWhitelistedInternal(uuid, whitelisted, server);
        return null;
    }
}
