package com.connexal.ravelcraft.shared;

import java.io.InputStream;

public interface RavelMain {
    InputStream getResource(String name);

    void runTask(Runnable runnable);

    void runTask(Runnable runnable, int secondsDelay);
}
