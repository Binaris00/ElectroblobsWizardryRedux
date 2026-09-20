package com.binaris.wizardry.core.mixin;

import com.binaris.wizardry.api.content.effect.MagicMobEffectInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin {

    @Inject(method = "load", at = @At("RETURN"), cancellable = true)
    private static void EBWIZARDRY$restoreMagicInstance(CompoundTag tag, CallbackInfoReturnable<MobEffectInstance> cir) {
        if (cir.getReturnValue() != null && tag.getBoolean(MagicMobEffectInstance.MAGIC_TAG)) {
            cir.setReturnValue(new MagicMobEffectInstance(cir.getReturnValue()));
        }
    }
}