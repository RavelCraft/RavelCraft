package com.connexal.ravelcraft.shared.all;

import com.connexal.ravelcraft.shared.all.util.RavelLogger;

import java.io.InputStream;
import java.nio.file.Path;

public interface RavelMain {
    InputStream getResource(String name);

    void scheduleTask(Runnable runnable);

    void scheduleTask(Runnable runnable, int secondsDelay);

    void scheduleRepeatingTask(Runnable runnable, int secondsInterval);

    RavelLogger getRavelLogger();

    Path getDataPath();

    static RavelMain set(RavelMain main) {
        if (Ravel.MAIN != null) {
            throw new IllegalStateException("RavelMain already set!");
        }

        main.getRavelLogger().info("RavelMain registered!");
        return Ravel.MAIN = main;
    }

    static RavelMain get() {
        return Ravel.MAIN;
    }
}
