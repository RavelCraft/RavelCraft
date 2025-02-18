package com.connexal.ravelcraft.shared.server.messaging;

import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.util.server.RavelServer;

public class MessagingConstants {
    public static final int PORT = 17157;
    public static final String MAGIC = "RavelCraft Messaging System";
    public static final RavelServer MESSAGING_SERVER = RavelServer.JE_PROXY; //Changing this will probably break everything. Code designed so that a proxy is the sever

    public static final int HEARTBEAT_INTERVAL = 10;

    public static final String COMMAND_FAILURE = "FAILURE";
    public static final String COMMAND_SUCCESS = "SUCCESS";

    public static boolean isServer() {
        return RavelInstance.getServer() == MESSAGING_SERVER;
    }
}
