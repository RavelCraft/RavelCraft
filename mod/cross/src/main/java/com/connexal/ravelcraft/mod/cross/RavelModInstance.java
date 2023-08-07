package com.connexal.ravelcraft.mod.cross;

import com.connexal.ravelcraft.mod.cross.registry.RavelBlockRegistry;
import com.connexal.ravelcraft.mod.cross.registry.RavelItemRegistry;
import com.connexal.ravelcraft.mod.cross.registry.RavelTabRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RavelModInstance {
    private static final Logger logger = LoggerFactory.getLogger(BuildConstants.ID);

    public static void setup() {
        //No-op
    }

    public static void init() {
        RavelTabRegistry.initialize();

        RavelItemRegistry.initialize();
        RavelBlockRegistry.initialize();

        logger.info("Modded components loaded!");
    }

    public Logger getLogger() {
        return logger;
    }
}
