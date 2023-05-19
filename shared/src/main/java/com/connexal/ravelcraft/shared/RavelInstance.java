package com.connexal.ravelcraft.shared;

import com.connexal.ravelcraft.shared.util.RavelLogger;

public class RavelInstance {
    private static RavelLogger logger;

    public static void init(RavelLogger logger) {
        RavelInstance.logger = logger;

        logger.info("RavelCraft is initializing...");
    }

    public static RavelLogger getLogger() {
        return logger;
    }
}
