package com.connexal.ravelcraft.proxy.bedrock.xbox;

import com.rtm516.mcxboxbroadcast.core.Logger;
import dev.waterdog.waterdogpe.logger.MainLogger;

public class XboxLogger implements Logger {
    private final MainLogger logger;
    private final String prefixString;

    public XboxLogger(MainLogger logger) {
        this(logger, "");
    }

    public XboxLogger(MainLogger logger, String prefixString) {
        this.logger = logger;
        this.prefixString = prefixString;
    }

    @Override
    public void info(String message) {
        logger.info(prefix(message));
    }

    @Override
    public void warning(String message) {
        logger.warning(prefix(message));
    }

    @Override
    public void error(String message) {
        logger.error(prefix(message));
    }

    @Override
    public void error(String message, Throwable ex) {
        logger.error(prefix(message), ex);
    }

    @Override
    public void debug(String message) {
        logger.debug(prefix(message));
    }

    @Override
    public Logger prefixed(String prefixString) {
        return new XboxLogger(logger, prefixString);
    }

    private String prefix(String message) {
        if (prefixString.isEmpty()) {
            return message;
        } else {
            return "[" + prefixString + "] " + message;
        }
    }
}
