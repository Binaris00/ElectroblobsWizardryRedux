package com.binaris.wizardry.core;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class EBConfig {
    // Mana values
    public static final int MANA_PER_SHARD = 10;
    public static final int MANA_PER_CRYSTAL = 100;
    public static final int GRAND_CRYSTAL_MANA = 400;

    // Progression and upgrades
    public static final int NON_ELEMENTAL_UPGRADE_BONUS = 3;
    public static final float MAX_PROGRESSION_REDUCTION = 0.75F;
    public static final float STORAGE_INCREASE_PER_LEVEL = 0.15f;
    public static final int UPGRADE_STACK_LIMIT = 3;

    // Defaults and lists
    private static final String[] DEFAULT_LOOT_INJECTION_LOCATIONS = {
            "minecraft:chests/simple_dungeon", "minecraft:chests/abandoned_mineshaft",
            "minecraft:chests/desert_pyramid", "minecraft:chests/jungle_temple",
            "minecraft:chests/stronghold_corridor", "minecraft:chests/stronghold_crossing",
            "minecraft:chests/stronghold_library", "minecraft:chests/igloo_chest",
            "minecraft:chests/woodland_mansion", "minecraft:chests/end_city_treasure"
    };

    public static ResourceLocation[] lootInjectionLocations = toResourceLocations(DEFAULT_LOOT_INJECTION_LOCATIONS);
    public static ResourceLocation[] meltItemsBlackList = {new ResourceLocation("minecraft:tropical_fish")};

    // Mob lists
    public static ResourceLocation[] modifiableMobs = new ResourceLocation[]{};
    public static ResourceLocation[] blacklistedHostileMobs = new ResourceLocation[]{
            new ResourceLocation("minecraft:ender_dragon"),
            new ResourceLocation("minecraft:wither"),
            new ResourceLocation("minecraft:giant"),
            new ResourceLocation("minecraft:shulker")
    };

    // Feature toggles
    public static boolean playersMoveEachOther = true;
    public static boolean replaceVanillaFallDamage = true;
    public static boolean showSpellHUD = true;
    public static boolean showChargeMeter = true;
    public static boolean preventBindingSameSpellTwiceToWands = false;
    public static boolean singleUseSpellBooks = false;
    public static boolean injectMobDrops = true;
    public static boolean passiveMobsAreAllies = false;
    public static boolean reverseScrollDirection = false;
    public static boolean shrineRegenerationEnabled = true;
    public static boolean playerBlockDamage = true;
    public static boolean spellHUDDynamicPositioning = false;
    public static boolean spellHUDFlipX = false;
    public static boolean spellHUDFlipY = false;
    public static boolean blockPlayersAlliesDamage = true;
    public static boolean blockOwnedAlliesDamage = true;
    public static double forfeitChance = 0.2;

    // Currency and other maps
    public static Map<Pair<ResourceLocation, Short>, Integer> currencyItems = new HashMap<>();

    private EBConfig() {
    }

    private static ResourceLocation[] toResourceLocations(String... strings) {
        return Arrays.stream(strings)
                .map(s -> new ResourceLocation(s.toLowerCase(Locale.ROOT).trim()))
                .toArray(ResourceLocation[]::new);
    }

    public static boolean isOnList(ResourceLocation[] list, ItemStack stack) {
        return Arrays.stream(list)
                .anyMatch(rl -> rl.equals(BuiltInRegistries.ITEM.getKey(stack.getItem())));
    }
}
