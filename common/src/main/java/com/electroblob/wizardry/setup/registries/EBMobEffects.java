package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import com.electroblob.wizardry.content.effect.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EBMobEffects {
    static Map<String, DeferredObject<MobEffect>> MOB_EFFECTS = new HashMap<>();

    public static final DeferredObject<MobEffect> FROST;
    public static final DeferredObject<MobEffect> STATIC_AURA;
    public static final DeferredObject<MobEffect> WARD;
    public static final DeferredObject<MobEffect> FIRE_SKIN;
    public static final DeferredObject<MobEffect> OAKFLESH;
    public static final DeferredObject<MobEffect> CURSE_OF_ENFEEBLEMENT;
    public static final DeferredObject<MobEffect> CURSE_OF_UNDEATH;
    public static final DeferredObject<MobEffect> DECAY;


    static {
        STATIC_AURA = mobEffect("static_aura", StaticAuraMobEffect::new);
        WARD = mobEffect("ward", WardMobEffect::new);
        FIRE_SKIN = mobEffect("fire_skin", FireSkinMobEffect::new);
        FROST = mobEffect("frost", FrostMobEffect::new);
        OAKFLESH = mobEffect("oakflesh", OakFleshMobEffect::new);

        CURSE_OF_ENFEEBLEMENT = mobEffect("curse_of_enfeeblement", EnfeeblementCurse::new);
        CURSE_OF_UNDEATH = mobEffect("curse_of_undeath", UndeathCurse::new);

        DECAY = mobEffect("decay", DecayMobEffect::new);
    }


    // ======= Registry =======
    public static void register(RegisterFunction<MobEffect> function){
        MOB_EFFECTS.forEach(((id, mobEffect) -> {
            function.register(BuiltInRegistries.MOB_EFFECT, WizardryMainMod.location(id), mobEffect.get());
        }));
    }

    // ======= Helpers =======
    private static DeferredObject<MobEffect> mobEffect(String name, Supplier<MobEffect> effect) {
        DeferredObject<MobEffect> ret = new DeferredObject<>(effect);
        MOB_EFFECTS.put(name, ret);
        return ret;
    }

}
