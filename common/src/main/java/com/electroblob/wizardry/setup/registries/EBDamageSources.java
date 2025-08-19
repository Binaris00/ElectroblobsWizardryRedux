package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

import java.util.ArrayList;

public final class EBDamageSources {
    public static ArrayList<ResourceKey<DamageType>> TYPES = new ArrayList<>();
    private EBDamageSources(){}

    public static final ResourceKey<DamageType> SORCERY = createType("sorcery");
    public static final ResourceKey<DamageType> FIRE = createType("fire");
    public static final ResourceKey<DamageType> FROST = createType("frost");
    public static final ResourceKey<DamageType> SHOCK = createType("shock");
    public static final ResourceKey<DamageType> WITHER = createType("wither");
    public static final ResourceKey<DamageType> POISON = createType("poison");
    public static final ResourceKey<DamageType> FORCE = createType("force");
    public static final ResourceKey<DamageType> BLAST = createType("blast");
    public static final ResourceKey<DamageType> RADIANT = createType("radiant");

    public static void init(){}

    private static ResourceKey<DamageType> createType(String name){
        var key = ResourceKey.create(Registries.DAMAGE_TYPE, WizardryMainMod.location(name));
        TYPES.add(key);
        return key;
    }

    /** TODO (Check addon compatibility) Could be used for addons, add a custom magic damage type to the mod list */
    public static ResourceKey<DamageType> createType(ResourceLocation location){
        var key = ResourceKey.create(Registries.DAMAGE_TYPE, location);
        TYPES.add(key);
        return key;
    }
}
