package com.electroblob.wizardry.core.mixin;

import com.electroblob.wizardry.EBEventHelper;
import com.electroblob.wizardry.content.EBLivingHurtEvent;
import com.electroblob.wizardry.content.EBLivingTick;
import com.electroblob.wizardry.setup.registries.EBMobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique LivingEntity livingEntity = (LivingEntity)(Object)this;

    @Inject(at = @At("HEAD"), method = "tick")
    public void EBWIZARDRY$tick(CallbackInfo ci){
        EBLivingTick event = EBLivingTick.create(livingEntity, livingEntity.level());
        EBEventHelper.onLivingTickEvent(event);
    }

    @Inject(method = "hurt", at = @At("HEAD"))
    public void EBWIZARDRY$livingEntityHurt(DamageSource source, float f, CallbackInfoReturnable<Boolean> cir){
        EBLivingHurtEvent event = EBLivingHurtEvent.create(livingEntity, source, f);
        EBEventHelper.onLivingHurtEvent(event);
        if (event.isCancelled()) cir.cancel();
    }


    @Inject(at = @At("HEAD"), method = "jumpFromGround")
    public void EBWIZARDRY$LivingEntityJump(CallbackInfo ci){
        if(livingEntity.hasEffect(EBMobEffects.FROST.get())){
            if(livingEntity.getEffect(EBMobEffects.FROST.get()).getAmplifier() == 0){
                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().x, 0.5, livingEntity.getDeltaMovement().z);
            } else {
                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().x, 0, livingEntity.getDeltaMovement().y);
            }
        }
    }
}
