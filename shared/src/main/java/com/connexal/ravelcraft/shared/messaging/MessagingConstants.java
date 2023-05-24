package com.connexal.ravelcraft.shared.messaging;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.util.RavelServer;

public class MessagingConstants {
    public static final int PORT = 17157;
    public static final String MAGIC = "RavelCraft Messaging System";
    public static final RavelServer MESSAGING_SERVER = RavelServer.JE_PROXY;

    public static boolean isServer() {
        return RavelInstance.getServer() == MESSAGING_SERVER;
    }
}
