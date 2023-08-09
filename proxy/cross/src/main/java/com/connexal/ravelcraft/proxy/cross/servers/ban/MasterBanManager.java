package com.connexal.ravelcraft.proxy.cross.servers.ban;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.util.RavelConfig;
import com.connexal.ravelcraft.shared.util.server.RavelServer;

import java.util.*;

class MasterBanManager extends BanManager {
    private final RavelConfig config;

    MasterBanManager() {
        super();

        this.messager.registerCommandHandler(MessagingCommand.PROXY_BAN_GET, this::getBanCommand);

        this.config = RavelInstance.getConfig("bans");
        this.ensureVariables();

        List<UUID> uuids;
        if (this.config.contains("ban-list") && this.config.isList("ban-list")) {
            uuids = this.config.getUUIDList("ban-list");
        } else {
            uuids = new ArrayList<>();
            this.config.set("ban-list", uuids);
            this.config.save();
        }

        for (UUID uuid : uuids) {
            long end = this.config.getLong("bans." + uuid + ".end");
            String reason = this.config.getString("bans." + uuid + ".reason");
            this.banList.put(uuid, new BanData(end, reason));
        }
    }

    @Override
    protected void ensureVariables() {
        if (this.banList != null) {
            return;
        }

        this.banList = new HashMap<>();
    }

    @Override
    protected void banRemoveChildInternal(UUID uuid) {
        this.config.set("bans." + uuid, null);
        this.config.set("ban-list", this.banList.keySet().stream().toList());
        this.config.save();
    }

    @Override
    protected void banAddChildInternal(UUID uuid, long end, String reason) {
        this.config.set("bans." + uuid + ".end", end);
        this.config.set("bans." + uuid + ".reason", reason);
        this.config.set("ban-list", this.banList.keySet().stream().map(UUID::toString).toList());
        this.config.save();
    }

    private String[] getBanCommand(RavelServer source, String[] args) {
        if (args.length != 0) {
            RavelInstance.getLogger().error("Invalid server ban get command!");
            return new String[0];
        }

        String[] data = new String[this.banList.size() * 2];
        int i = 0;
        for (Map.Entry<UUID, BanData> entry : this.banList.entrySet()) {
            data[i] = entry.getKey().toString() + ";" + entry.getValue().end();
            data[i + 1] = entry.getValue().reason();
            i += 2;
        }

        return data;
    }
}
