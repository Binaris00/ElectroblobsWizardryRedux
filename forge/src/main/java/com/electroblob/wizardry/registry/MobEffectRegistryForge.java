package com.electroblob.wizardry.registry;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.setup.registries.EBRegister;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class MobEffectRegistryForge {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, WizardryMainMod.MOD_ID);

    public static void register() {
        EBRegister.registerMobEffects((collection) -> collection.forEach(EFFECTS::register));
    }

    private MobEffectRegistryForge() {}
}
