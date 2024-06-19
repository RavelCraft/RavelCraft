package com.connexal.ravelcraft.mod.cross.util;

import com.connexal.ravelcraft.shared.all.Ravel;
import com.connexal.ravelcraft.shared.all.util.RavelLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabricRavelLogger implements RavelLogger {
    private final Logger logger;

    public FabricRavelLogger() {
        this.logger = LoggerFactory.getLogger(Ravel.ID);
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
