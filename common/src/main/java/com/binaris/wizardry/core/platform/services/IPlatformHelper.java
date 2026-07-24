package com.binaris.wizardry.core.platform.services;

import com.binaris.wizardry.core.integrations.ArtifactIntegration;
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

import java.nio.file.Path;

/// Platform abstraction for loader/environment-specific operations — config paths, mod detection, biome
/// checks, argument type registration, block break events, and artifact integration.
///
/// Each loader (Fabric/Forge) has its own APIs for mod loading, environment detection, and event firing.
/// This interface centralizes all those platform-specific operations so the common codebase never directly
/// references Fabric or Forge internals. Implementations are loaded via Java {@code ServiceLoader} at class-load
/// time and accessed through the singleton {@code Services.PLATFORM}.
public interface IPlatformHelper {

    /// Returns the loader-specific configuration directory path.
    ///
    /// On Fabric this is {@code FabricLoader.getInstance().getConfigDir()}; on Forge it is {@code FMLPaths.CONFIGDIR.get()}.
    ///
    /// @return the platform's config directory as a {@code Path}
    Path getConfigDirectory();

    /// Returns the human-readable name of the current mod loader platform.
    ///
    /// @return {@code "Fabric"} on Fabric, {@code "Forge"} on Forge
    String getPlatformName();

    /// Checks whether a mod with the given ID is currently loaded.
    ///
    /// @param modId the mod ID to check (e.g. {@code "curios"}, {@code "trinkets"})
    /// @return {@code true} if the mod is present in the loaded mod list
    boolean isModLoaded(String modId);

    /// Returns whether the current runtime is a development environment (IDE / dev workspace).
    ///
    /// @return {@code true} if running in a development environment
    boolean isDevelopmentEnvironment();

    /// Returns whether the current runtime is a dedicated server (no integrated client).
    ///
    /// @return {@code true} if running on a dedicated server
    boolean isDedicatedServer();

    /// Returns a human-readable environment label derived from {@link #isDevelopmentEnvironment()}.
    ///
    /// @return {@code "development"} if in dev, {@code "production"} otherwise
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    /// Checks whether the given biome is classified as a hot biome. Used to conditionally apply biome-dependent
    /// item modifiers (e.g. Fire Ring potency in hot biomes).
    ///
    /// On Forge this checks {@code Tags.Biomes.IS_HOT} and {@code IS_DRY}; on Fabric it checks
    /// {@code ConventionalBiomeTags.CLIMATE_HOT} and {@code CLIMATE_DRY}.
    ///
    /// @param biome the biome holder to test
    /// @return {@code true} if the biome matches the hot/dry biome tag categories
    boolean intHotBiomes(Holder<Biome> biome);

    /// Checks whether the given biome is classified as an earth-type biome.
    ///
    /// Matches jungle, forest, and coniferous/tree-coniferous biome tags depending on the loader.
    ///
    /// @param biome the biome holder to test
    /// @return {@code true} if the biome is an earth-type biome
    boolean inEarthBiomes(Holder<Biome> biome);

    /// Checks whether the given biome is classified as an icy/snowy biome.
    ///
    /// On Forge this checks {@code Tags.Biomes.IS_SNOWY}; on Fabric it checks {@code ConventionalBiomeTags.SNOWY}.
    ///
    /// @param biome the biome holder to test
    /// @return {@code true} if the biome matches the snowy biome tag
    boolean inIceBiomes(Holder<Biome> biome);

    /// Registers a custom command argument type with the platform-specific argument registry.
    ///
    /// On Fabric this delegates directly to {@code ArgumentTypeRegistry.registerArgumentType}; on Forge
    /// it delegates to a {@code DeferredRegister} for argument serializer registration.
    ///
    /// @param id the {@code ResourceLocation} identifier for the argument type
    /// @param clazz the argument type class
    /// @param serializer the argument type info/serializer for network sync
    <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void registerArgumentType(ResourceLocation id,
                                                                                                  Class<? extends A> clazz,
                                                                                                  ArgumentTypeInfo<A, T> serializer);

    /// Fires a platform-specific player block break event, allowing other mods to cancel the action.
    ///
    /// On Forge this posts a {@code BlockEvent.BreakEvent} to {@code MinecraftForge.EVENT_BUS};
    /// on Fabric it invokes the {@code PlayerBlockBreakEvents.BEFORE} callback.
    ///
    /// @param level the world level where the block is being broken
    /// @param pos the position of the block
    /// @param player the player attempting to break the block
    /// @return {@code true} if the event was canceled (the break should be prevented), {@code false} otherwise
    boolean firePlayerBlockBreakEvent(Level level, BlockPos pos, Player player);

    /// Fires a platform-specific mob block break / griefing event, allowing the action to be blocked.
    ///
    /// On Forge this calls {@code ForgeEventFactory.getMobGriefingEvent}; on Fabric it checks the
    /// {@code RULE_MOBGRIEFING} game rule. The {@code pos} parameter may be {@code null} — both
    /// implementations currently ignore it and base their decision on entity/rule checks alone.
    ///
    /// @param level the world level
    /// @param pos the position of the block (may be {@code null})
    /// @param mob the mob attempting to break the block
    /// @return {@code true} if the event was blocked (the break should be prevented), {@code false} if allowed
    boolean fireMobBlockBreakEvent(Level level, @Nullable BlockPos pos, Mob mob);

    /// Returns the platform-specific artifact integration, providing access to equipment slots for rings,
    /// amulets, and charms.
    ///
    /// Returns a {@code CuriosIntegration} instance on Forge (backed by the Curios API) or a
    /// {@code TrinketsIntegration} instance on Fabric (backed by the Trinkets API). If neither mod
    /// is loaded, the integration instance operates in a no-op mode.
    ///
    /// @return the active artifact integration, never {@code null}
    ArtifactIntegration getArtifactIntegration();
}