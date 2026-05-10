package com.binaris.wizardry.core.config;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.core.config.option.BoolConfigOption;
import com.binaris.wizardry.core.config.option.ConfigOption;
import com.binaris.wizardry.core.config.option.ListConfigOption;
import com.binaris.wizardry.core.config.option.NumberConfigOption;
import com.binaris.wizardry.core.config.util.ConfigType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class EBServerConfig implements ConfigProvider {
    private static final ArrayList<ConfigOption<?>> OPTIONS = new ArrayList<>();
    public static final EBServerConfig INSTANCE = new EBServerConfig();

    public static final ConfigOption<Integer> MANA_PER_SHARD = addOption(NumberConfigOption.integer("mana_per_shard", 10, 1, Integer.MAX_VALUE));
    public static final ConfigOption<Integer> MANA_PER_CRYSTAL = addOption(NumberConfigOption.integer("mana_per_crystal", 100, 1, Integer.MAX_VALUE));
    public static final ConfigOption<Integer> GRAND_CRYSTAL_MANA = addOption(NumberConfigOption.integer("grand_crystal_mana", 400, 1, Integer.MAX_VALUE));
    public static final ConfigOption<Integer> NON_ELEMENTAL_UPGRADE_BONUS = addOption(NumberConfigOption.integer("non_elemental_upgrade_bonus", 3, 1, Integer.MAX_VALUE));
    public static final ConfigOption<Float> MAX_PROGRESSION_REDUCTION = addOption(NumberConfigOption.floating("max_progression_reduction", 0.75F, 0.0F, 1.0F));
    public static final ConfigOption<Float> STORAGE_INCREASE_PER_LEVEL = addOption(NumberConfigOption.floating("storage_increase_per_level", 0.15f, 0.0f, 1.0f));
    public static final ConfigOption<Integer> UPGRADE_STACK_LIMIT = addOption(NumberConfigOption.integer("upgrade_stack_limit", 3, 1, Integer.MAX_VALUE));

    public static final ConfigOption<List<ResourceLocation>> LOOT_INJECTION_LOCATIONS_TO_STRUCTURES = addOption(
            ListConfigOption.resourceLocation("loot_injection_locations_to_structures",
                    convertList("minecraft:chests/simple_dungeon", "minecraft:chests/abandoned_mineshaft",
                    "minecraft:chests/desert_pyramid", "minecraft:chests/jungle_temple",
                    "minecraft:chests/stronghold_corridor", "minecraft:chests/stronghold_crossing",
                    "minecraft:chests/stronghold_library", "minecraft:chests/igloo_chest",
                    "minecraft:chests/woodland_mansion", "minecraft:chests/end_city_treasure"))
    );

    public static final ConfigOption<List<ResourceLocation>> MELT_ITEMS_BLACKLIST = addOption(ListConfigOption.resourceLocation("melt_items_blacklist", List.of()));
    public static final ConfigOption<List<ResourceLocation>> LOOT_INJECTION_TO_MOBS = addOption(ListConfigOption.resourceLocation("loot_injection_to_mobs", List.of()));
    public static final ConfigOption<Boolean> INJECT_LOOT_TO_HOSTILE_MOBS = addOption(BoolConfigOption.booleanOption("inject_mob_drops", true));
    public static final ConfigOption<Boolean> PLAYERS_MOVE_EACH_OTHER = addOption(BoolConfigOption.booleanOption("players_move_each_other", true));
    public static final ConfigOption<Boolean> REVERSE_SCROLL_DIRECTION = addOption(BoolConfigOption.booleanOption("reverse_scroll_direction", false));
    public static final ConfigOption<Boolean> REPLACE_VANILLA_FALL_DAMAGE = addOption(BoolConfigOption.booleanOption("replace_vanilla_fall_damage", false));
    public static final ConfigOption<Boolean> PREVENT_BINDING_SAME_SPELL_TWICE_TO_WANDS = addOption(BoolConfigOption.booleanOption("prevent_binding_same_spell_twice_to_wands", false));
    public static final ConfigOption<Boolean> SINGLE_USE_SPELL_BOOKS = addOption(BoolConfigOption.booleanOption("single_use_spell_books", false));
    public static final ConfigOption<Boolean> PASSIVE_MOBS_ARE_ALLIES = addOption(BoolConfigOption.booleanOption("passive_mobs_are_allies", false));
    public static final ConfigOption<Boolean> SHRINE_REGENERATION_ENABLED = addOption(BoolConfigOption.booleanOption("shrine_regeneration_enabled", true));
    public static final ConfigOption<Boolean> PLAYER_BLOCK_DAMAGE = addOption(BoolConfigOption.booleanOption("player_block_damage", true));
    public static final ConfigOption<Boolean> BLOCK_PLAYERS_ALLIES_DAMAGE = addOption(BoolConfigOption.booleanOption("block_players_allies_damage", true));
    public static final ConfigOption<Boolean> BLOCK_OWNED_ALLIES_DAMAGE = addOption(BoolConfigOption.booleanOption("block_owned_allies_damage", true));
    public static final ConfigOption<Float> FORFEIT_CHANCE = addOption(NumberConfigOption.floating("forfeit_chance", 0.2F, 0.0F, 1.0F));

    private static <T> ConfigOption<T> addOption(ConfigOption<T> option) {
        OPTIONS.add(option);
        return option;
    }

    private static List<ResourceLocation> convertList(String... locations) {
        return Arrays.stream(locations).map(ResourceLocation::new).collect(Collectors.toList());
    }

    public static boolean isOnList(ConfigOption<List<ResourceLocation>> list, ItemStack stack) {
        ResourceLocation itemRL = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return list.get().stream().anyMatch(itemRL::equals);
    }

    @Override
    public String getModid() {
        return WizardryMainMod.MOD_ID;
    }

    @Override
    public ConfigType getType() {
        return ConfigType.SERVER;
    }

    @Override
    public Collection<ConfigOption<?>> build() {
        return OPTIONS;
    }
}
