package com.connexal.ravelcraft.jeproxy;

import com.connexal.ravelcraft.jeproxy.util.RavelLoggerImpl;
import com.connexal.ravelcraft.shared.BuildConstants;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.google.inject.Inject;
import com.velocitypowered.api.event.lifecycle.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

import java.nio.file.Paths;

@Plugin(id = BuildConstants.ID, name = BuildConstants.NAME, version = BuildConstants.VERSION)
public class JeProxy {
    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        RavelInstance.init(new RavelLoggerImpl(this.logger), Paths.get("plugins/" + BuildConstants.ID));
    }
}
