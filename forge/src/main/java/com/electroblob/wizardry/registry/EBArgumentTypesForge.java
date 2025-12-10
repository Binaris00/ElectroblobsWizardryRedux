package com.electroblob.wizardry.registry;

import com.electroblob.wizardry.WizardryMainMod;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

// I don't like forge
public final class EBArgumentTypesForge {
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES =
            DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, WizardryMainMod.MOD_ID);

    private EBArgumentTypesForge() {}

    public static void register(IEventBus modBus) {
        ARGUMENT_TYPES.register(modBus);
    }

    @SuppressWarnings("unchecked")
    public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void registerArgumentType(
            String name,
            Class<? extends A> clazz,
            ArgumentTypeInfo<A, T> serializer) {

        ARGUMENT_TYPES.register(name, () -> {
            return ArgumentTypeInfos.registerByClass((Class<A>) clazz, serializer);
        });
    }
}
