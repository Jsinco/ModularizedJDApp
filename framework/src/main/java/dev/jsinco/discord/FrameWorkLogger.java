package dev.jsinco.discord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FrameWorkLogger {

    private static final Logger logger = LoggerFactory.getLogger(FrameWorkLogger.class);

    public static void info(String message) {
        logger.info(message);
    }

    public static void warn(String message) {
        logger.warn(message);
    }

    public static void error(String message) {
        logger.error(message);
    }

    public static void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }
}
