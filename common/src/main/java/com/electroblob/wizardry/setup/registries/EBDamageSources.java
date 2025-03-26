package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public final class EBDamageSources {
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


    public static void init(){

    }

    private static ResourceKey<DamageType> createType(String name){
        return ResourceKey.create(Registries.DAMAGE_TYPE, WizardryMainMod.location(name));
    }
}
