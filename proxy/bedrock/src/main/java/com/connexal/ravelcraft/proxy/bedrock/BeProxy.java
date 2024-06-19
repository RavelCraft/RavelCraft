package com.connexal.ravelcraft.proxy.bedrock;

import com.connexal.ravelcraft.proxy.bedrock.commands.WaterdogCommandRegistrar;
import com.connexal.ravelcraft.proxy.bedrock.players.WaterdogPlayerManager;
import com.connexal.ravelcraft.proxy.bedrock.players.skin.SkinUploader;
import com.connexal.ravelcraft.proxy.bedrock.servers.WaterdogServerManager;
import com.connexal.ravelcraft.proxy.bedrock.util.WaterdogRavelLogger;
import com.connexal.ravelcraft.proxy.bedrock.xbox.XboxBroadcaster;
import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.all.RavelMain;
import com.connexal.ravelcraft.shared.all.util.RavelLogger;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.plugin.Plugin;

import java.io.InputStream;
import java.nio.file.Path;

public class BeProxy extends Plugin implements RavelMain {
    private static ProxyServer server = null;
    private static RavelLogger logger = null;
    private static Path dataPath = null;

    private static SkinUploader skinUploader = null;
    private static XboxBroadcaster xboxBroadcaster = null;

    @Override
    public void onEnable() {
        server = this.getProxy();
        logger = new WaterdogRavelLogger(this.getLogger());
        dataPath = this.getDataFolder().toPath();

        RavelMain.set(this);
        RavelInstance.setup();

        RavelInstance.init(new WaterdogCommandRegistrar(), new WaterdogPlayerManager());
        RavelProxyInstance.init(new WaterdogServerManager());

        new BeEventListener();

        skinUploader = new SkinUploader();
        skinUploader.start();

        xboxBroadcaster = new XboxBroadcaster();
    }

    @Override
    public void onDisable() {
        skinUploader.close();

        RavelInstance.shutdown();
    }

    @Override
    public InputStream getResource(String name) {
        return this.getResourceFile(name);
    }

    @Override
    public void scheduleTask(Runnable runnable) {
        server.getScheduler().scheduleAsync(runnable);
    }

    @Override
    public void scheduleTask(Runnable runnable, int secondsDelay) {
        server.getScheduler().scheduleDelayed(runnable, secondsDelay * 20, true);
    }

    @Override
    public void scheduleRepeatingTask(Runnable runnable, int secondsInterval) {
        server.getScheduler().scheduleRepeating(runnable, secondsInterval * 20, true);
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

    public static SkinUploader getSkinUploader() {
        return skinUploader;
    }

    public static XboxBroadcaster getXboxBroadcaster() {
        return xboxBroadcaster;
    }
}
