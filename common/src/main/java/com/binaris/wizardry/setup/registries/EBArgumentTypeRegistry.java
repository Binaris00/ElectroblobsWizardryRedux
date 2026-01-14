package com.binaris.wizardry.setup.registries;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.content.command.argument.SpellArgument;
import com.binaris.wizardry.core.platform.Services;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.resources.ResourceLocation;

public final class EBArgumentTypeRegistry {
    private EBArgumentTypeRegistry() {
    }

    public static void init() {
        registerArgumentType(WizardryMainMod.location("spell"), SpellArgument.class, SingletonArgumentInfo.contextFree(SpellArgument::new));
    }

    private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void registerArgumentType(ResourceLocation id, Class<? extends A> clazz, ArgumentTypeInfo<A, T> serializer) {
        Services.PLATFORM.registerArgumentType(id, clazz, serializer);
    }
}
