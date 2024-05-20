package com.connexal.ravelcraft.mod.cross;

import net.fabricmc.api.ModInitializer;

public class RavelModDummyInit implements ModInitializer {
    @Override
    public void onInitialize() {
        RavelModInstance.setup();
        RavelModInstance.init();
    }
}
