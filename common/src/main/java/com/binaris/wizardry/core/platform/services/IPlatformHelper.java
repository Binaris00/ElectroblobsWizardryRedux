package com.binaris.wizardry.core.platform.services;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

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

    /**
     * Each loader has its own way of firing events so we're getting all platform-specific logic out of the main codebase.
     *
     * @return true if the event is canceled, false otherwise
     */
    boolean firePlayerBlockBreakEvent(Level level, BlockPos pos, Player player);

    /**
     * Each loader has its own way of firing events so we're getting all platform-specific logic out of the main codebase.
     *
     * @return true if the event is canceled, false otherwise
     */
    boolean fireMobBlockBreakEvent(Level level, @Nullable BlockPos pos, Mob mob);
}