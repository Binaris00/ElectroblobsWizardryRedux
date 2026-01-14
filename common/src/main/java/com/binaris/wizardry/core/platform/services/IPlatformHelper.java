package com.binaris.wizardry.core.platform.services;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public interface IPlatformHelper {

    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    boolean isDedicatedServer();

    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    boolean intHotBiomes(Holder<Biome> biome);

    boolean inEarthBiomes(Holder<Biome> biome);

    boolean inIceBiomes(Holder<Biome> biome);

    <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void registerArgumentType(ResourceLocation id,
                                                                                                  Class<? extends A> clazz,
                                                                                                  ArgumentTypeInfo<A, T> serializer);
}