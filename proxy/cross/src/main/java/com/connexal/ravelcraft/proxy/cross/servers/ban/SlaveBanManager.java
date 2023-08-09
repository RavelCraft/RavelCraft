package com.connexal.ravelcraft.proxy.cross.servers.ban;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.messaging.MessagingConstants;

import java.util.HashMap;
import java.util.UUID;

class SlaveBanManager extends BanManager {
    SlaveBanManager() {
        super();
    }

    @Override
    protected void ensureVariables() {
        if (this.banList != null) {
            return;
        }

        String[] banResponse = this.messager.sendCommandWithResponse(MessagingConstants.MESSAGING_SERVER, MessagingCommand.PROXY_BAN_GET);

        //Modulo 2 check is to ensure that the response contains 2 entries per banned player
        if (banResponse == null) {
            throw new IllegalStateException("Failed to get banned players from master!");
        }
        if (banResponse.length == 0) { //There aren't any banned players
            this.banList = new HashMap<>();
            return;
        }
        if (banResponse.length % 2 != 0) {
            throw new IllegalStateException("Invalid response from master! There arent 2 entries per banned player!");
        }

        this.banList = new HashMap<>();
        for (int i = 0; i < banResponse.length; i += 2) {
            String uuidEnd = banResponse[i];
            String reason = banResponse[i + 1];

            UUID uuid;
            long end;
            try {
                uuid = UUID.fromString(uuidEnd.substring(0, uuidEnd.indexOf(';')));
                end = Long.parseLong(uuidEnd.substring(uuidEnd.indexOf(';') + 1));
            } catch (Exception e) {
                RavelInstance.getLogger().error("Failed to parse banned player from master: " + uuidEnd, e);
                continue;
            }

            this.banList.put(uuid, new BanData(end, reason));
        }
    }

    @Override
    protected void banRemoveChildInternal(UUID uuid) {
        //Nothing
    }

    @Override
    protected void banAddChildInternal(UUID uuid, long end, String reason) {
        //Nothing
    }
}
