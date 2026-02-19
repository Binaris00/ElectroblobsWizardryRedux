package com.binaris.wizardry.platform;

import com.binaris.wizardry.core.platform.services.IPlatformHelper;
import com.binaris.wizardry.registry.EBArgumentTypesForge;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ForgePlatformHelper implements IPlatformHelper {
    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

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

    @Override
    public boolean firePlayerBlockBreakEvent(Level level, BlockPos pos, Player player) {
        BlockState state = level.getBlockState(pos);
        BlockEvent.BreakEvent testEvent = new BlockEvent.BreakEvent(level, pos, state, player);
        MinecraftForge.EVENT_BUS.post(testEvent);

        return testEvent.isCanceled();
    }

    @Override
    public boolean fireMobBlockBreakEvent(Level level, BlockPos pos, Mob mob) {
        return !ForgeEventFactory.getMobGriefingEvent(level, mob);
    }
}