package com.connexal.ravelcraft.shared;

import java.io.InputStream;

public interface RavelMain {
    InputStream getResource(String name);

    void scheduleTask(Runnable runnable);

    void scheduleTask(Runnable runnable, int secondsDelay);

    void scheduleRepeatingTask(Runnable runnable, int secondsInterval);
}
