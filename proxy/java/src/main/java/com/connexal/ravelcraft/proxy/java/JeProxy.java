package com.connexal.ravelcraft.proxy.java;

import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.proxy.java.commands.VelocityCommandRegistrar;
import com.connexal.ravelcraft.proxy.java.players.VelocityPlayerManager;
import com.connexal.ravelcraft.proxy.java.servers.ServerManagerImpl;
import com.connexal.ravelcraft.proxy.java.util.VelocityRavelLogger;
import com.connexal.ravelcraft.proxy.java.website.WebServer;
import com.connexal.ravelcraft.shared.all.Ravel;
import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.all.RavelMain;
import com.connexal.ravelcraft.shared.all.util.RavelLogger;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Plugin(id = Ravel.ID, name = Ravel.NAME, version = Ravel.VERSION,
        url = Ravel.SERVER_IP, description = Ravel.DESCRIPTION, authors = { Ravel.NAME })
public class JeProxy implements RavelMain {
    private static ProxyServer server = null;
    private static RavelLogger logger = null;
    private static Path dataPath = null;

    private static WebServer webServer;

    @Inject
    public JeProxy(ProxyServer server, Logger logger) {
        JeProxy.server = server;
        JeProxy.logger = new VelocityRavelLogger(logger);
        JeProxy.dataPath = Paths.get("plugins", Ravel.ID);
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        RavelMain.set(this);
        RavelInstance.setup();

        RavelInstance.init(new VelocityCommandRegistrar(), new VelocityPlayerManager());
        RavelProxyInstance.init(new ServerManagerImpl());

        server.getEventManager().register(this, new JeEventListener());

        webServer = WebServer.create();
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

    @Override
    public RavelLogger getRavelLogger() {
        return logger;
    }

    @Override
    public Path getDataPath() {
        return dataPath;
    }

    public static ProxyServer getServer() {
        return server;
    }

    public static WebServer getWebServer() {
        return webServer;
    }
}
