package com.binaris.wizardry.core.config;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EBConfig implements IConfigProvider {
    private static final ArrayList<ConfigOption<?>> options = new ArrayList<>();

    // Client options
    public static final ConfigOption<Boolean> SPELL_HUD_FLIP_X = booleanOption("spell_hud_flip_x", false);
    public static final ConfigOption<Boolean> SPELL_HUD_FLIP_Y = booleanOption("spell_hud_flip_y", false);
    public static final ConfigOption<Boolean> SPELL_HUD_DYNAMIC_POSITIONING = booleanOption("spell_hud_dynamic_positioning", false);
    public static final ConfigOption<Boolean> SHOW_CHARGE_METER = booleanOption("show_charge_meter", true);
    public static final ConfigOption<Boolean> SHOW_SPELL_HUD = booleanOption("show_spell_hud", true);

    // Server options (saved on the world)
    public static final ConfigOption<Integer> MANA_PER_SHARD = intOption("mana_per_shard", 10);
    public static final ConfigOption<Integer> MANA_PER_CRYSTAL = intOption("mana_per_crystal", 100);
    public static final ConfigOption<Integer> GRAND_CRYSTAL_MANA = intOption("grand_crystal_mana", 400);
    public static final ConfigOption<Integer> NON_ELEMENTAL_UPGRADE_BONUS = intOption("non_elemental_upgrade_bonus", 3);
    public static final ConfigOption<Float> MAX_PROGRESSION_REDUCTION = floatOption("max_progression_reduction", 0.75F);
    public static final ConfigOption<Float> STORAGE_INCREASE_PER_LEVEL = floatOption("storage_increase_per_level", 0.15f);
    public static final ConfigOption<Integer> UPGRADE_STACK_LIMIT = intOption("upgrade_stack_limit", 3);

    public static final ConfigOption<List<ResourceLocation>> LOOT_INJECTION_LOCATIONS_TO_STRUCTURES = locationOption(
            "loot_injection_locations_to_structures",
            "minecraft:chests/simple_dungeon", "minecraft:chests/abandoned_mineshaft",
            "minecraft:chests/desert_pyramid", "minecraft:chests/jungle_temple",
            "minecraft:chests/stronghold_corridor", "minecraft:chests/stronghold_crossing",
            "minecraft:chests/stronghold_library", "minecraft:chests/igloo_chest",
            "minecraft:chests/woodland_mansion", "minecraft:chests/end_city_treasure"
    );

    public static final ConfigOption<List<ResourceLocation>> MELT_ITEMS_BLACKLIST = locationOption("melt_items_blacklist");
    public static final ConfigOption<List<ResourceLocation>> LOOT_INJECTION_TO_MOBS = locationOption("loot_injection_to_mobs");
    public static final ConfigOption<Boolean> INJECT_LOOT_TO_HOSTILE_MOBS = booleanOption("inject_mob_drops", true);
    public static final ConfigOption<Boolean> PLAYERS_MOVE_EACH_OTHER = booleanOption("players_move_each_other", true);
    public static final ConfigOption<Boolean> REVERSE_SCROLL_DIRECTION = booleanOption("reverse_scroll_direction", false);
    public static final ConfigOption<Boolean> REPLACE_VANILLA_FALL_DAMAGE = booleanOption("replace_vanilla_fall_damage", false);
    public static final ConfigOption<Boolean> PREVENT_BINDING_SAME_SPELL_TWICE_TO_WANDS = booleanOption("prevent_binding_same_spell_twice_to_wands", false);
    public static final ConfigOption<Boolean> SINGLE_USE_SPELL_BOOKS = booleanOption("single_use_spell_books", false);
    public static final ConfigOption<Boolean> PASSIVE_MOBS_ARE_ALLIES = booleanOption("passive_mobs_are_allies", false);
    public static final ConfigOption<Boolean> SHRINE_REGENERATION_ENABLED = booleanOption("shrine_regeneration_enabled", true);
    public static final ConfigOption<Boolean> PLAYER_BLOCK_DAMAGE = booleanOption("player_block_damage", true);
    public static final ConfigOption<Boolean> BLOCK_PLAYERS_ALLIES_DAMAGE = booleanOption("block_players_allies_damage", true);
    public static final ConfigOption<Boolean> BLOCK_OWNED_ALLIES_DAMAGE = booleanOption("block_owned_allies_damage", true);
    public static final ConfigOption<Float> FORFEIT_CHANCE = floatOption("forfeit_chance", 0.2F);

    private static ConfigOption<Boolean> booleanOption(String key, Boolean defaultValue) {
        return addOption(new ConfigOption<>(key, defaultValue, Codec.BOOL));
    }

    private static ConfigOption<Integer> intOption(String key, Integer defaultValue) {
        return addOption(new ConfigOption<>(key, defaultValue, Codec.INT));
    }

    private static ConfigOption<Float> floatOption(String key, Float defaultValue) {
        return addOption(new ConfigOption<>(key, defaultValue, Codec.FLOAT));
    }

    private static ConfigOption<List<ResourceLocation>> locationOption(String key, String... defaultValue) {
        List<ResourceLocation> locations = new ArrayList<>();
        for (String s : defaultValue) {
            locations.add(new ResourceLocation(s));
        }
        return addOption(new ConfigOption<>(key, locations, ResourceLocation.CODEC.listOf()));
    }

    private static <T> ConfigOption<T> addOption(ConfigOption<T> option) {
        options.add(option);
        return option;
    }

    public static boolean isOnList(ConfigOption<List<ResourceLocation>> list, ItemStack stack) {
        ResourceLocation itemRL = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return list.get().stream().anyMatch(itemRL::equals);
    }

    @Override
    public List<ConfigOption<?>> getOptions() {
        return options;
    }
}
