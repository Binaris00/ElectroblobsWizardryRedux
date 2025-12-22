package com.electroblob.wizardry.core;

import com.electroblob.wizardry.api.content.ConfigValue;
import com.electroblob.wizardry.api.content.event.EBLivingDeathEvent;
import com.google.gson.Gson;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

// Todo EBConfig? Still no used, contains some fields that were in the Constant class, need to rewrite (like a lot)
// I'm just trowing everything that could be part of the config here for now
public final class EBConfig {
    public static final Gson GSON = new Gson();

    public static final double MIN_PRECISE_DOUBLE = -9_007_199_254_740_992.0;
    public static final double MAX_PRECISE_DOUBLE = 9_007_199_254_740_991.0;
    public static final float MIN_PRECISE_FLOAT = -16_777_216;
    public static final float MAX_PRECISE_FLOAT = 16_777_215;

    public static final int MANA_PER_SHARD = 10;
    public static final int MANA_PER_CRYSTAL = 100;
    public static final int GRAND_CRYSTAL_MANA = 400;

    public static final int NON_ELEMENTAL_UPGRADE_BONUS = 3;
    public static final float MAX_PROGRESSION_REDUCTION = 0.75F;
    public static final float STORAGE_INCREASE_PER_LEVEL = 0.15f;
    public static final int CONDENSER_TICK_INTERVAL = 50;
    public static final int UPGRADE_STACK_LIMIT = 3;
    /**
     * Items that cannot be melted by the Pocket Furnace spell.
     */
    public static final ResourceLocation[] meltItemsBlackList = {new ResourceLocation("minecraft:potato")};
    /**
     * The amount of mana given for a kill for each level of siphon upgrade. A random amount from 0 to this number - 1
     * is also added.
     *
     * @see com.electroblob.wizardry.content.item.WandUpgradeItem#onLivingDeath(EBLivingDeathEvent)
     */
    public static final int SIPHON_MANA_PER_LEVEL = 5;
    public static final int MAX_RECENT_SPELLS = 5;
    public static final int RECENT_SPELL_EXPIRY_TICKS = 1200; // 60 seconds
    private static final String[] DEFAULT_LOOT_INJECTION_LOCATIONS = {"minecraft:chests/simple_dungeon",
            "minecraft:chests/abandoned_mineshaft", "minecraft:chests/desert_pyramid", "minecraft:chests/jungle_temple",
            "minecraft:chests/stronghold_corridor", "minecraft:chests/stronghold_crossing",
            "minecraft:chests/stronghold_library", "minecraft:chests/igloo_chest", "minecraft:chests/woodland_mansion",
            "minecraft:chests/end_city_treasure"};
    public static boolean damageTypePerElement = false;
    public static boolean playersMoveEachOther = true;
    public static boolean replaceVanillaFallDamage = true;
    public static boolean showSpellHUD = true;
    public static boolean showChargeMeter = true;
    public static boolean preventBindingSameSpellTwiceToWands = false;
    public static boolean singleUseSpellBooks = false;
    public static boolean wandsMustBeHeldToDecrementCooldown = false;
    public static double forfeitChance = 0.2; // we use
    public static float COOLDOWN_REDUCTION_PER_LEVEL = 0.15f;
    public static float POTENCY_INCREASE_PER_TIER = 0.15f;
    public static float DURATION_INCREASE_PER_LEVEL = 0.25f;
    public static float RANGE_INCREASE_PER_LEVEL = 0.25f;
    public static float BLAST_RADIUS_INCREASE_PER_LEVEL = 0.25f;
    public static boolean booksPauseGame = true;
    public static boolean blockPlayersAlliesDamage = true;
    public static boolean blockOwnedAlliesDamage = true;
    public static ResourceLocation[] lootInjectionLocations = toResourceLocations(DEFAULT_LOOT_INJECTION_LOCATIONS);
    public static Map<Pair<ResourceLocation, Short>, Integer> currencyItems = new HashMap<>();
    public static boolean injectMobDrops = true;
    /**
     * Passive mobs that will have the normal additional loot added to their drops. Passive Mobs (not hostile ones)
     * not on this list will be ignored. This is to prevent things like cows dropping spell books.
     */
    public static ResourceLocation[] modifiableMobs = new ResourceLocation[]{};
    /**
     * Hostile mobs that will not have the normal additional loot added to their drops. Hostile mobs (not passive
     * ones) not on this list will be affected. This is to prevent things like the Ender Dragon dropping spell
     * books.
     */
    public static ResourceLocation[] blacklistedHostileMobs = List.of(
            new ResourceLocation("minecraft:ender_dragon"),
            new ResourceLocation("minecraft:wither"),
            new ResourceLocation("minecraft:giant"),
            new ResourceLocation("minecraft:shulker")
    ).toArray(new ResourceLocation[0]);
    public static boolean passiveMobsAreAllies = false;
    public static boolean shiftScrolling = true;
    public static boolean reverseScrollDirection = false;
    public static boolean shrineRegenerationEnabled = true;
    public static boolean dispenserBlockDamage = false;
    public static boolean playerBlockDamage = true;
    public final ConfigValue<Double> defaultMana = new ConfigValue<>(100.0, MIN_PRECISE_DOUBLE, MAX_PRECISE_DOUBLE);


    private EBConfig() {
    }

    private static ResourceLocation[] toResourceLocations(String... strings) {
        return Arrays.stream(strings).map(s -> new ResourceLocation(s.toLowerCase(Locale.ROOT).trim())).toArray(ResourceLocation[]::new);
    }

    public static boolean isOnList(ResourceLocation[] list, ItemStack stack) {
        return Arrays.stream(list).anyMatch(rl -> rl.equals(BuiltInRegistries.ITEM.getKey(stack.getItem())));
    }
}
