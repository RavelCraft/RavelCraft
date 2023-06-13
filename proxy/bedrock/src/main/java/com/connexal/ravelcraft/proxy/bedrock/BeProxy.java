package com.connexal.ravelcraft.proxy.bedrock;

import com.connexal.ravelcraft.proxy.bedrock.commands.CommandRegistrarImpl;
import com.connexal.ravelcraft.proxy.bedrock.players.PlayerManagerImpl;
import com.connexal.ravelcraft.proxy.bedrock.servers.ServerManagerImpl;
import com.connexal.ravelcraft.proxy.bedrock.skin.SkinUploader;
import com.connexal.ravelcraft.proxy.bedrock.util.RavelLoggerImpl;
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

        RavelInstance.setup(this, this.getDataFolder().toPath(), new RavelLoggerImpl(this.getLogger()));
        RavelProxyInstance.setup();

        RavelInstance.init(new CommandRegistrarImpl(), new PlayerManagerImpl());
        RavelProxyInstance.init(new ServerManagerImpl());

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

    public static ProxyServer getServer() {
        return server;
    }

    public static SkinUploader getSkinUploadManager() {
        return skinUploader;
    }
}
