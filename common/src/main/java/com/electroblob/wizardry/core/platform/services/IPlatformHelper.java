package com.electroblob.wizardry.core.platform.services;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public interface IPlatformHelper {

    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    boolean intHotBiomes(Holder<Biome> biome);

    boolean inEarthBiomes(Holder<Biome> biome);

    boolean inIceBiomes(Holder<Biome> biome);
}