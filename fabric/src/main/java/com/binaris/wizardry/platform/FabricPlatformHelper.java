package com.binaris.wizardry.platform;

import com.binaris.wizardry.core.platform.services.IPlatformHelper;
import com.mojang.brigadier.arguments.ArgumentType;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
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
    public boolean isDedicatedServer() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
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

    @Override
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void registerArgumentType(ResourceLocation id, Class<? extends A> clazz, ArgumentTypeInfo<A, T> serializer) {
        ArgumentTypeRegistry.registerArgumentType(id, clazz, serializer);
    }


}
