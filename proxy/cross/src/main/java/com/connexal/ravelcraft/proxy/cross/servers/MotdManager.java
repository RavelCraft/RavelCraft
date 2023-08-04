package com.connexal.ravelcraft.proxy.cross.servers;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.Messager;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.messaging.MessagingConstants;
import com.connexal.ravelcraft.shared.util.RavelConfig;
import com.connexal.ravelcraft.shared.util.server.RavelServer;

public class MotdManager {
    private String motd;

    public MotdManager() {
        Messager messager = RavelInstance.getMessager();

        if (MessagingConstants.isServer()) {
            RavelConfig config = RavelInstance.getConfig();
            if (!config.contains("motd")) {
                config.set("motd", "A Minecraft Server");
                config.save();
            }
            this.motd = config.getString("motd");

            messager.registerCommandHandler(MessagingCommand.PROXY_MOTD_GET, this::getMotdCommand);
        } else {
            String[] response = messager.sendCommandWithResponse(MessagingConstants.MESSAGING_SERVER, MessagingCommand.PROXY_MOTD_GET);
            if (response == null || response.length != 1) {
                this.motd = "A Minecraft Server";
                RavelInstance.getLogger().error("Failed to get MOTD from server!");
            }
        }

        messager.registerCommandHandler(MessagingCommand.PROXY_MOTD_SET, this::setMotdCommand);
    }

    public String getMotd() {
        return this.motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;

        if (MessagingConstants.isServer()) {
            RavelConfig config = RavelInstance.getConfig();
            config.set("motd", motd);
            config.save();

            RavelServer otherProxy = RavelInstance.getServer().isJavaProxy() ? RavelServer.BE_PROXY : RavelServer.JE_PROXY;
            RavelInstance.getMessager().sendCommand(otherProxy, MessagingCommand.PROXY_MOTD_SET, motd);
        } else {
            RavelInstance.getMessager().sendCommand(MessagingConstants.MESSAGING_SERVER, MessagingCommand.PROXY_MOTD_SET, motd);
        }
    }

    private String[] getMotdCommand(RavelServer source, String[] args) {
        return new String[]{this.getMotd()};
    }

    private String[] setMotdCommand(RavelServer source, String[] args) {
        if (args.length != 1) {
            RavelInstance.getLogger().error("Invalid number of arguments for motd set command!");
            return null;
        }

        this.motd = args[0];
        return null;
    }
}
