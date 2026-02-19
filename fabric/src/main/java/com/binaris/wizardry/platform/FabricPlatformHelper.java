package com.binaris.wizardry.platform;

import com.binaris.wizardry.core.platform.services.IPlatformHelper;
import com.mojang.brigadier.arguments.ArgumentType;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

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

    @Override
    public boolean firePlayerBlockBreakEvent(Level level, BlockPos pos, Player player) {
        BlockState state = level.getBlockState(pos);
        BlockEntity entity = level.getBlockEntity(pos);
        return !PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(level, player, pos, state, entity);
    }

    @Override
    public boolean fireMobBlockBreakEvent(Level level, BlockPos pos, Mob mob) {
        return !level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
    }
}
