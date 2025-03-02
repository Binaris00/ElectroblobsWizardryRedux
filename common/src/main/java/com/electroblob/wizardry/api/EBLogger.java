package com.electroblob.wizardry.api;

import com.electroblob.wizardry.WizardryMainMod;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public final class EBLogger {


    public static void info(Component component) {
        if(!load().disabled) load().logger.info(component.getString());
    }
    /**
     * Sends an error in the format "EBWizardry Error: "
     */
    public static void error(Component component) {
        if(!load().disabled)
            load().logger.error("{}: {}", Component.translatable("logger.error.ebwizardry").getString(), component.getString());
    }

    public static void debug(Component component) {
        if(!load().disabled) load().logger.debug(component.getString());
    }

    public static void useIfEnabled(Consumer<Logger> loggerAction){
        if(!load().disabled) loggerAction.accept(load().logger);
    }

    public static void disable() {
        info(Component.translatable("logger.info.ebwizardry.disabled"));
        load().disabled = true;
    }

    public static EBLogger load() {
        return Instance.INSTANCE;
    }

    private static class Instance {
        private static final EBLogger INSTANCE = new EBLogger();
    }

    private final Logger logger = LoggerFactory.getLogger(WizardryMainMod.MOD_ID);
    private boolean disabled;

    private EBLogger(){}
}
