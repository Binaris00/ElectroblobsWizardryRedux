package com.electroblob.wizardry.core.mixin;

import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.EBCommands;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Check {@link EBCommands}
 * */
@Mixin(Commands.class)
public abstract class CommandsMixin {

    @Shadow
    @Final
    private CommandDispatcher<CommandSourceStack> dispatcher;

    @Inject(method =
            "<init>(Lnet/minecraft/commands/Commands$CommandSelection;Lnet/minecraft/commands/CommandBuildContext;)V",
            at = @At(value = "RETURN"))
    public void EBWIZARDRY$commands(Commands.CommandSelection commandSelection, CommandBuildContext commandBuildContext, CallbackInfo ci) {
        EBCommands.COMMANDS_TO_REGISTER.forEach(c -> c.accept(dispatcher));
        if(Services.PLATFORM.isDevelopmentEnvironment()){
            EBCommands.DEBUG_COMMANDS.forEach(c -> c.accept(dispatcher));
        }
    }

}
