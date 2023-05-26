package com.connexal.ravelcraft.proxy.bedrock;

import com.connexal.ravelcraft.proxy.bedrock.commands.CommandRegistrarImpl;
import com.connexal.ravelcraft.proxy.bedrock.servers.ServerManagerImpl;
import com.connexal.ravelcraft.proxy.bedrock.util.RavelLoggerImpl;
import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.RavelMain;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.plugin.Plugin;

import java.io.InputStream;

public class BeProxy extends Plugin implements RavelMain {
    private static ProxyServer server = null;

    @Override
    public void onEnable() {
        server = this.getProxy();

        RavelInstance.init(this, new RavelLoggerImpl(this.getLogger()), new CommandRegistrarImpl(), this.getDataFolder().toPath());
        RavelProxyInstance.init(new ServerManagerImpl());
    }

    @Override
    public void onDisable() {
        RavelInstance.shutdown();
    }

    @Override
    public InputStream getResource(String name) {
        return this.getResourceFile(name);
    }

    public static ProxyServer getServer() {
        return server;
    }
}
