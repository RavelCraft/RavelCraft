package com.connexal.ravelcraft.proxy.bedrock;

import com.connexal.ravelcraft.proxy.bedrock.commands.WaterdogCommandRegistrar;
import com.connexal.ravelcraft.proxy.bedrock.players.WaterdogPlayerManager;
import com.connexal.ravelcraft.proxy.bedrock.servers.WaterdogServerManager;
import com.connexal.ravelcraft.proxy.bedrock.skin.SkinUploader;
import com.connexal.ravelcraft.proxy.bedrock.util.WaterdogRavelLogger;
import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.RavelMain;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.plugin.Plugin;

import java.io.InputStream;

public class BeProxy extends Plugin implements RavelMain {
    private static ProxyServer server = null;

    private static SkinUploader skinUploader = null;

    @Override
    public void onEnable() {
        server = this.getProxy();

        RavelInstance.setup(this, this.getDataFolder().toPath(), new WaterdogRavelLogger(this.getLogger()));
        RavelProxyInstance.setup();

        RavelInstance.init(new WaterdogCommandRegistrar(), new WaterdogPlayerManager());
        RavelProxyInstance.init(new WaterdogServerManager());

        new BeEventListener();

        skinUploader = new SkinUploader();
        skinUploader.start();
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

    public static ProxyServer getServer() {
        return server;
    }

    public static SkinUploader getSkinUploader() {
        return skinUploader;
    }
}
