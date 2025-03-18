package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.DeferredObject;
import com.electroblob.wizardry.api.common.effect.MagicMobEffect;
import com.electroblob.wizardry.common.content.effect.*;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EBMobEffects {
    public static final DeferredObject<MobEffect> FROST;
    public static final DeferredObject<MobEffect> STATIC_AURA;
    public static final DeferredObject<MobEffect> WARD;
    public static final DeferredObject<MobEffect> FIRE_SKIN;
    public static final DeferredObject<MobEffect> OAKFLESH;
    public static final DeferredObject<MobEffect> CURSE_OF_ENFEEBLEMENT;
    public static final DeferredObject<MobEffect> CURSE_OF_UNDEATH;

    static Map<String, DeferredObject<MobEffect>> mobEffects = new HashMap<>();

    static void handleRegistration(Consumer<Map<String, DeferredObject<MobEffect>>> handler) {
        handler.accept(mobEffects);
    }

    static {
        STATIC_AURA = registerEffect("static_aura", () -> new MagicMobEffect(MobEffectCategory.BENEFICIAL, 0) {
            @Override
            public void spawnCustomParticle(Level world, double x, double y, double z) {
                ParticleBuilder.create(EBParticles.SPARK).pos(x, y, z).spawn(world);
            }
        });

        WARD = registerEffect("ward", () -> new MagicMobEffect(MobEffectCategory.BENEFICIAL, 0xc991d0) {
            @Override
            public void spawnCustomParticle(Level world, double x, double y, double z) {
            }
        });

        FIRE_SKIN = registerEffect("fire_skin", FireSkinMobEffect::new);
        FROST = registerEffect("frost", FrostMobEffect::new);
        OAKFLESH = registerEffect("oakflesh", OakFleshMobEffect::new);

        CURSE_OF_ENFEEBLEMENT = registerEffect("curse_of_enfeeblement", EnfeeblementCurse::new);
        CURSE_OF_UNDEATH = registerEffect("curse_of_undeath", UndeathCurse::new);
    }

    private static DeferredObject<MobEffect> registerEffect(String name, Supplier<MobEffect> effect) {
        DeferredObject<MobEffect> ret = new DeferredObject<>(effect);
        mobEffects.put(name, ret);
        return ret;
    }

}
