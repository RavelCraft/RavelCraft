package com.connexal.ravelcraft.jeproxy;

import com.connexal.ravelcraft.shared.util.RavelLogger;
import org.slf4j.Logger;

public class RavelLoggerImpl implements RavelLogger {
    private final Logger logger;

    public RavelLoggerImpl(Logger logger) {
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
