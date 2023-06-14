package com.connexal.ravelcraft.shared.messaging;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.util.server.RavelServer;

public class MessagingConstants {
    public static final int PORT = 17157;
    public static final String MAGIC = "RavelCraft Messaging System";
    public static final RavelServer MESSAGING_SERVER = RavelServer.JE_PROXY; //Changing this will probably break everything

    public static final String COMMAND_FAILURE = "FAILURE";
    public static final String COMMAND_SUCCESS = "SUCCESS";

    public static boolean isServer() {
        return RavelInstance.getServer() == MESSAGING_SERVER;
    }
}
