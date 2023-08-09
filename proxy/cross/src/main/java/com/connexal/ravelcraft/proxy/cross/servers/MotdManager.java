package com.connexal.ravelcraft.proxy.cross.servers;

import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.Messager;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.messaging.MessagingConstants;
import com.connexal.ravelcraft.shared.util.RavelConfig;
import com.connexal.ravelcraft.shared.util.server.RavelServer;

public class MotdManager {
    private static final String MOTD_DEFAULT = "A Minecraft Server";
    private final Messager messager;

    private String motd = null;

    public MotdManager() {
        this.messager = RavelInstance.getMessager();

        if (MessagingConstants.isServer()) {
            this.messager.registerCommandHandler(MessagingCommand.PROXY_MOTD_GET, this::getMotdCommand);
        }

        this.messager.registerCommandHandler(MessagingCommand.PROXY_MOTD_SET, this::setMotdCommand);

        this.messager.registerDisconnectHandler(server -> {
            this.motd = null;
        });
    }

    public String getMotd() {
        if (this.motd == null) {
            if (MessagingConstants.isServer()) {
                RavelConfig config = RavelInstance.getConfig();
                if (!config.contains("motd")) {
                    config.set("motd", MOTD_DEFAULT);
                    config.save();
                }
                this.motd = config.getString("motd");
            } else {
                String[] response = this.messager.sendCommandWithResponse(MessagingConstants.MESSAGING_SERVER, MessagingCommand.PROXY_MOTD_GET);
                if (response == null || response.length != 1) {
                    this.motd = MOTD_DEFAULT;
                    RavelInstance.getLogger().error("Failed to get MOTD from server!");
                } else {
                    this.motd = response[0];
                }
            }
        }

        return this.motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;

        if (MessagingConstants.isServer()) {
            RavelConfig config = RavelInstance.getConfig();
            config.set("motd", motd);
            config.save();

            this.messager.sendCommand(RavelProxyInstance.getOtherProxy(), MessagingCommand.PROXY_MOTD_SET, motd);
        } else {
            this.messager.sendCommand(MessagingConstants.MESSAGING_SERVER, MessagingCommand.PROXY_MOTD_SET, motd);
        }
    }

    private String[] getMotdCommand(RavelServer source, String[] args) {
        return new String[]{ this.getMotd() };
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
