package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class EBWorldGen {
    public static final ResourceKey<PlacedFeature> CRYSTAL_ORE = ResourceKey.create(Registries.PLACED_FEATURE, WizardryMainMod.location("crystal_ore"));
    public static final ResourceKey<PlacedFeature> CRYSTAL_FLOWER = ResourceKey.create(Registries.PLACED_FEATURE, WizardryMainMod.location("crystal_flower"));

    private EBWorldGen() {}
}
