package com.binaris.wizardry.core;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * EBLogger is a simple wrapper around the SLF4J Logger, providing convenience methods for logging messages at different
 * levels. It is designed to be used throughout the EBWizardry mod for consistent logging and to be easy to change and
 * maintain.
 */
public final class EBLogger {
    private static final Logger LOGGER = LogUtils.getLogger();

    private EBLogger() {
    } // Use your own logger, not mine!! >:C

    public static void info(String info, Object... args) {
        LOGGER.info(info, args);
    }

    public static void error(String message, Object... args) {
        LOGGER.error("======================================"); // I love this format
        LOGGER.error("EBWizardry Error:");
        LOGGER.error(message, args);
        LOGGER.error("======================================");
    }

    public static void debug(String message, Object... args) {
        LOGGER.debug(message, args);
    }

    public static void warn(String message, Object... args) {
        LOGGER.warn(message, args);
    }
}
