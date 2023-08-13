package com.connexal.ravelcraft.proxy.java;

import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.proxy.java.commands.VelocityCommandRegistrar;
import com.connexal.ravelcraft.proxy.java.players.VelocityPlayerManager;
import com.connexal.ravelcraft.proxy.java.servers.ServerManagerImpl;
import com.connexal.ravelcraft.proxy.java.util.VelocityRavelLogger;
import com.connexal.ravelcraft.shared.BuildConstants;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.RavelMain;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Plugin(id = BuildConstants.ID, name = BuildConstants.NAME, version = BuildConstants.VERSION,
        url = BuildConstants.SERVER_IP, description = BuildConstants.DESCRIPTION, authors = { BuildConstants.NAME })
public class JeProxy implements RavelMain {
    private static ProxyServer server = null;
    private static Logger logger = null;

    @Inject
    public JeProxy(ProxyServer server, Logger logger) {
        JeProxy.server = server;
        JeProxy.logger = logger;
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        RavelInstance.setup(this, Paths.get("plugins/" + BuildConstants.ID), new VelocityRavelLogger(logger));
        RavelProxyInstance.setup();

        RavelInstance.init(new VelocityCommandRegistrar(), new VelocityPlayerManager());
        RavelProxyInstance.init(new ServerManagerImpl());

        server.getEventManager().register(this, new JeEventListener());
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        RavelInstance.shutdown();
    }

    @Override
    public InputStream getResource(String name) {
        return JeProxy.class.getClassLoader().getResourceAsStream(name);
    }

    @Override
    public void scheduleTask(Runnable runnable) {
        server.getScheduler().buildTask(this, runnable).schedule();
    }

    @Override
    public void scheduleTask(Runnable runnable, int secondsDelay) {
        server.getScheduler().buildTask(this, runnable).delay(secondsDelay, TimeUnit.SECONDS).schedule();
    }

    @Override
    public void scheduleRepeatingTask(Runnable runnable, int secondsInterval) {
        server.getScheduler().buildTask(this, runnable).repeat(secondsInterval, TimeUnit.SECONDS).schedule();
    }

    public static ProxyServer getServer() {
        return server;
    }
}
