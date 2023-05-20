package com.connexal.ravelcraft.beproxy;

import com.connexal.ravelcraft.beproxy.util.RavelLoggerImpl;
import com.connexal.ravelcraft.shared.RavelInstance;
import dev.waterdog.waterdogpe.plugin.Plugin;

public class BeProxy extends Plugin {
    @Override
    public void onEnable() {
        RavelInstance.init(new RavelLoggerImpl(this.getLogger()), this.getDataFolder().toPath());
    }

    @Override
    public void onDisable() {
        RavelInstance.shutdown();
    }
}
