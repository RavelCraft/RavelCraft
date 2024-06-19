package com.connexal.ravelcraft.proxy.java.util;

import com.connexal.ravelcraft.shared.all.util.RavelLogger;
import org.slf4j.Logger;

public class VelocityRavelLogger implements RavelLogger {
    private final Logger logger;

    public VelocityRavelLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        this.logger.info(message);
    }

    @Override
    public void warning(String message) {
        this.logger.warn(message);
    }

    @Override
    public void error(String message) {
        this.logger.error(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        this.logger.error(message, throwable);
    }
}
