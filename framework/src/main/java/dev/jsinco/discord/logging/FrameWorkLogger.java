package dev.jsinco.discord.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logger for the framework
 * @since 1.0
 * @author Jonah
 */
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

    public static void configureLogging() {
        // Create a PatternLayout with the desired format
        PatternLayout layout = PatternLayout.newBuilder()
                .withPattern("[%d{HH:mm:ss} %c{1}/%thread]: %msg%n")
                .build();

        // Create a ConsoleAppender and associate it with the PatternLayout
        ConsoleAppender consoleAppender = ConsoleAppender.createDefaultAppenderForLayout(layout);

        // Get the current Log4j2 configuration
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();

        // Add the appender to the configuration
        config.addAppender(consoleAppender);
        consoleAppender.start();
        // Remove all existing appenders from the root logger
        config.getRootLogger().getAppenders().values().forEach(appender -> config.getRootLogger().removeAppender(appender.getName()));
        // Add the appender to the root logger with null filter and level
        config.getRootLogger().setAdditive(false);
        config.getRootLogger().setLevel(Level.INFO);
        config.getRootLogger().addAppender(consoleAppender, Level.INFO, null);



        // Update the existing configuration with the modifications
        context.updateLoggers();
    }
}
