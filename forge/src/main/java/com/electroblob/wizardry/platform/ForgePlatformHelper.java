package com.electroblob.wizardry.platform;

import com.electroblob.wizardry.registry.EBArgumentTypesForge;
import com.electroblob.wizardry.core.platform.services.IPlatformHelper;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

public class ForgePlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public boolean isDedicatedServer() {
        return FMLLoader.getDist().isDedicatedServer();
    }

    @Override
    public boolean intHotBiomes(Holder<Biome> biome) {
        return biome.is(Tags.Biomes.IS_HOT) || biome.is(Tags.Biomes.IS_DRY);
    }

    @Override
    public boolean inEarthBiomes(Holder<Biome> biome) {
        return biome.is(Biomes.JUNGLE) || biome.is(Biomes.FOREST) || biome.is(Tags.Biomes.IS_CONIFEROUS);
    }

    @Override
    public boolean inIceBiomes(Holder<Biome> biome) {
        return biome.is(Tags.Biomes.IS_SNOWY);
    }

    @Override
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void registerArgumentType(ResourceLocation id, Class<? extends A> clazz, ArgumentTypeInfo<A, T> serializer) {
        EBArgumentTypesForge.registerArgumentType(id.getPath(), clazz, serializer);
    }
}