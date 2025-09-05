package com.electroblob.wizardry.platform;

import com.electroblob.wizardry.core.platform.services.IPlatformHelper;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean intHotBiomes(Holder<Biome> biome) {
        return biome.is(ConventionalBiomeTags.CLIMATE_DRY) || biome.is(ConventionalBiomeTags.CLIMATE_HOT);
    }

    @Override
    public boolean inEarthBiomes(Holder<Biome> biome) {
        return biome.is(ConventionalBiomeTags.FOREST) || biome.is(ConventionalBiomeTags.TREE_CONIFEROUS) || biome.is(ConventionalBiomeTags.JUNGLE);
    }

    @Override
    public boolean inIceBiomes(Holder<Biome> biome) {
        return biome.is(ConventionalBiomeTags.SNOWY);
    }


}
