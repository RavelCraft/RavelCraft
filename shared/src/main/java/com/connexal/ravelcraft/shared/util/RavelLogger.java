package com.connexal.ravelcraft.shared.util;

public interface RavelLogger {
    void info(String message);

    void warning(String message);

    void error(String message);

    void error(String message, Throwable throwable);
}
