package com.binaris.wizardry.api;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public final class EBLogger {
    private static final Logger LOGGER = LogUtils.getLogger();

    private EBLogger() {
    } // Use your own logger, not mine!! >:C

    public static void info(String info, Object... args) {
        LOGGER.info(info, args);
    }

    /**
     * Sends an error in the format "EBWizardry Error: message"
     */
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
