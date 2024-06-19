package com.connexal.ravelcraft.mod.cross;

import com.connexal.ravelcraft.mod.cross.registry.RavelBlockRegistry;
import com.connexal.ravelcraft.mod.cross.registry.RavelItemRegistry;
import com.connexal.ravelcraft.mod.cross.registry.RavelTabRegistry;
import com.connexal.ravelcraft.mod.cross.util.FabricRavelLogger;
import com.connexal.ravelcraft.shared.all.Ravel;
import com.connexal.ravelcraft.shared.all.RavelMain;
import com.connexal.ravelcraft.shared.all.util.RavelLogger;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RavelModInstance implements RavelMain {
    private static ModContainer mod;
    private static ScheduledExecutorService scheduler;
    private static RavelLogger logger;
    private static Path dataPath;

    public static void setup() {
        scheduler = Executors.newScheduledThreadPool(3);
        mod = FabricLoader.getInstance().getModContainer(Ravel.ID).orElseThrow();

        logger = new FabricRavelLogger();
        dataPath = FabricLoader.getInstance().getConfigDir().resolve(Ravel.ID);

        RavelMain.set(new RavelModInstance());
    }

    public static void init() {
        RavelTabRegistry.initialize();

        RavelItemRegistry.initialize();
        RavelBlockRegistry.initialize();

        logger.info("Modded components loaded!");
    }

    @Override
    public InputStream getResource(String name) {
        Path path = mod.findPath(name).orElse(null);
        if (path == null) {
            return null;
        }

        try {
            return path.getFileSystem()
                    .provider()
                    .newInputStream(path);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void scheduleTask(Runnable runnable) {
        scheduler.execute(runnable);
    }

    @Override
    public void scheduleTask(Runnable runnable, int secondsDelay) {
        scheduler.schedule(runnable, secondsDelay, TimeUnit.SECONDS);
    }

    @Override
    public void scheduleRepeatingTask(Runnable runnable, int secondsInterval) {
        scheduler.scheduleAtFixedRate(runnable, 0, secondsInterval, TimeUnit.SECONDS);
    }

    @Override
    public RavelLogger getRavelLogger() {
        return logger;
    }

    @Override
    public Path getDataPath() {
        return dataPath;
    }
}
