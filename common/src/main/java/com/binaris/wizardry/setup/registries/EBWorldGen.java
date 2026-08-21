package com.binaris.wizardry.setup.registries;

import com.binaris.wizardry.WizardryMainMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;

public final class EBWorldGen {
    public static final TagKey<Biome> SPAWNS_CRYSTAL_ORE = TagKey.create(Registries.BIOME, WizardryMainMod.location("spawns_crystal_ore"));
    public static final TagKey<Biome> SPAWNS_CRYSTAL_FLOWER = TagKey.create(Registries.BIOME, WizardryMainMod.location("spawns_crystal_flower"));
    public static final TagKey<Biome> SPAWNS_EVIL_WIZARD = TagKey.create(Registries.BIOME, WizardryMainMod.location("spawns_evil_wizard"));
    public static final ResourceKey<PlacedFeature> CRYSTAL_ORE = ResourceKey.create(Registries.PLACED_FEATURE, WizardryMainMod.location("crystal_ore"));
    public static final ResourceKey<PlacedFeature> CRYSTAL_FLOWER = ResourceKey.create(Registries.PLACED_FEATURE, WizardryMainMod.location("crystal_flower"));
    public static final ResourceKey<Structure> WIZARD_TOWER = ResourceKey.create(Registries.STRUCTURE, WizardryMainMod.location("wizard_tower"));
    public static final ResourceKey<Structure> SHRINE = ResourceKey.create(Registries.STRUCTURE, WizardryMainMod.location("shrine"));
    public static final ResourceKey<Structure> LIBRARY_RUINS = ResourceKey.create(Registries.STRUCTURE, WizardryMainMod.location("library_ruins"));
    public static final ResourceKey<Structure> UNDERGROUND_LIBRARY_RUINS = ResourceKey.create(Registries.STRUCTURE, WizardryMainMod.location("underground_library_ruins")); // todo underground library ruins

    private EBWorldGen() {
    }
}
