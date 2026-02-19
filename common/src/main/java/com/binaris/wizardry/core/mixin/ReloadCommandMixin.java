package com.binaris.wizardry.core.mixin;

import com.binaris.wizardry.WizardryMainMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.ReloadCommand;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(ReloadCommand.class)
public abstract class ReloadCommandMixin {

    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(method = "reloadPacks", at = @At("HEAD"))
    private static void EBWRIZARDRY$reloadPacks(Collection<String> selectedIds, CommandSourceStack source, CallbackInfo ci) {
        WizardryMainMod.reloadConfigs();
        LOGGER.info("Reloading Electroblob's Wizardry configs");
    }
}
