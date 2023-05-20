package com.connexal.ravelcraft.jeproxy;

import com.connexal.ravelcraft.jeproxy.util.RavelLoggerImpl;
import com.connexal.ravelcraft.shared.BuildConstants;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.nio.file.Paths;

@Plugin(id = BuildConstants.ID, name = BuildConstants.NAME, version = BuildConstants.VERSION,
        url = BuildConstants.SERVER_IP, description = BuildConstants.DESCRIPTION, authors = { BuildConstants.NAME })
public class JeProxy {
    private static ProxyServer server = null;
    private static Logger logger = null;

    @Inject
    public JeProxy(ProxyServer server, Logger logger) {
        JeProxy.server = server;
        JeProxy.logger = logger;
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        RavelInstance.init(new RavelLoggerImpl(logger), Paths.get("plugins/" + BuildConstants.ID));
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        RavelInstance.shutdown();
    }
}
