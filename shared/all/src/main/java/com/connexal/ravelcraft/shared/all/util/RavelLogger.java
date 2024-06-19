package com.connexal.ravelcraft.shared.all.util;

public interface RavelLogger {
    void info(String message);

    void warning(String message);

    void error(String message);

    void error(String message, Throwable throwable);
}
