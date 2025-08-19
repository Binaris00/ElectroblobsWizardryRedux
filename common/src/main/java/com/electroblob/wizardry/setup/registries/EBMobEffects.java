package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.effect.CurseMobEffect;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import com.electroblob.wizardry.content.effect.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class EBMobEffects {
    static Map<String, DeferredObject<MobEffect>> MOB_EFFECTS = new HashMap<>();

    public static final DeferredObject<MobEffect> ARCANE_JAMMER = mobEffect("arcane_jammer", () -> new CurseMobEffect(MobEffectCategory.HARMFUL, 0)); // TODO
    public static final DeferredObject<MobEffect> FROST = mobEffect("frost", FrostMobEffect::new);
    public static final DeferredObject<MobEffect> STATIC_AURA = mobEffect("static_aura", StaticAuraMobEffect::new);
    public static final DeferredObject<MobEffect> WARD = mobEffect("ward", WardMobEffect::new);
    public static final DeferredObject<MobEffect> FIRE_SKIN = mobEffect("fire_skin", FireSkinMobEffect::new);
    public static final DeferredObject<MobEffect> OAKFLESH = mobEffect("oakflesh", OakFleshMobEffect::new);
    public static final DeferredObject<MobEffect> CURSE_OF_ENFEEBLEMENT = mobEffect("curse_of_enfeeblement", EnfeeblementCurse::new);
    public static final DeferredObject<MobEffect> CURSE_OF_UNDEATH = mobEffect("curse_of_undeath", UndeathCurse::new);
    public static final DeferredObject<MobEffect> DECAY = mobEffect("decay", DecayMobEffect::new);
    public static final DeferredObject<MobEffect> CURSE_OF_SOULBINDING = mobEffect("curse_of_soulbinding", () -> new CurseMobEffect(MobEffectCategory.HARMFUL, 0x0f000f));

    // ======= Registry =======
    public static void register(RegisterFunction<MobEffect> function){
        MOB_EFFECTS.forEach(((id, mobEffect) ->
                function.register(BuiltInRegistries.MOB_EFFECT, WizardryMainMod.location(id), mobEffect.get())));
    }

    // ======= Helpers =======
    private static DeferredObject<MobEffect> mobEffect(String name, Supplier<MobEffect> effect) {
        DeferredObject<MobEffect> ret = new DeferredObject<>(effect);
        MOB_EFFECTS.put(name, ret);
        return ret;
    }

}
