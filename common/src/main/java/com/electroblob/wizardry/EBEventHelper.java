package com.electroblob.wizardry;

import com.electroblob.wizardry.api.content.effect.MagicMobEffect;
import com.electroblob.wizardry.core.event.EBLivingHurtEvent;
import com.electroblob.wizardry.core.event.EBLivingTick;
import com.electroblob.wizardry.content.effect.FireSkinMobEffect;
import com.electroblob.wizardry.content.effect.StaticAuraMobEffect;
import com.electroblob.wizardry.content.effect.WardMobEffect;
import com.electroblob.wizardry.content.entity.construct.BubbleConstruct;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Simple class to save all the event helper methods
// This is internal use, you're not supposed to use this
public final class EBEventHelper {
    private EBEventHelper() {}

    /**
     * This applies to loaders with the Mixin
     * {@link com.electroblob.wizardry.core.mixin.LivingEntityMixin#EBWIZARDRY$livingEntityHurt(DamageSource, float, CallbackInfoReturnable) LivingEntityMixin#EBWIZARDRY$livingEntityHurt}
     * */
    public static void onLivingHurtEvent(EBLivingHurtEvent event) {
        StaticAuraMobEffect.onLivingHurt(event);
        FireSkinMobEffect.onLivingHurt(event);
        WardMobEffect.onLivingHurt(event);

        BubbleConstruct.onLivingHurt(event);
    }

    /**
     * This applies to loaders with the Mixin
     * {@link com.electroblob.wizardry.core.mixin.LivingEntityMixin#EBWIZARDRY$tick(CallbackInfo) LivingEntityMixin#EBWIZARDRY$tick}
     * */
    public static void onLivingTickEvent(EBLivingTick event) {
        MagicMobEffect.onLivingTick(event);
    }
}
