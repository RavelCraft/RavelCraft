package com.connexal.ravelcraft.mod.cross;

import com.connexal.ravelcraft.mod.cross.custom.tabs.ModTabs;
import com.connexal.ravelcraft.mod.cross.custom.blocks.ModBlocks;
import com.connexal.ravelcraft.mod.cross.custom.items.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RavelModInstance {
    private static final Logger logger = LoggerFactory.getLogger(BuildConstants.ID);

    public static void setup() {
        //No-op
    }

    public static void init() {
        ModTabs.initialize();

        ModItems.initialize();
        ModBlocks.initialize();

        logger.info("Modded components loaded!");
    }

    public Logger getLogger() {
        return logger;
    }
}
