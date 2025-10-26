package com.electroblob.wizardry.core.mixin;

import com.electroblob.wizardry.api.content.event.EBLivingDeathEvent;
import com.electroblob.wizardry.api.content.event.EBLivingHurtEvent;
import com.electroblob.wizardry.api.content.event.EBLivingTick;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.platform.Services;
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
        WizardryEventBus.getInstance().fire(new EBLivingTick(livingEntity, livingEntity.level()));
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void EBWIZARDRY$livingEntityHurt(DamageSource source, float f, CallbackInfoReturnable<Boolean> cir){
        if (WizardryEventBus.getInstance().fire(new EBLivingHurtEvent(livingEntity, source, f))) cir.cancel();
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

    @Inject(method = "die", at = @At("HEAD"))
    public void EBWIZARDRY$LivingEntityDie(DamageSource damageSource, CallbackInfo ci){
        WizardryEventBus.getInstance().fire(new EBLivingDeathEvent(livingEntity, damageSource));
    }

    @Inject(method = "shouldDropLoot", at = @At(value = "RETURN"), cancellable = true)
    public void EBWIZARDRY$dropLoot(CallbackInfoReturnable<Boolean> cir){
        if(Services.OBJECT_DATA.isMinion(livingEntity)){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "shouldDropExperience", at = @At(value = "RETURN"), cancellable = true)
    public void EBWIZARDRY$dropExperience(CallbackInfoReturnable<Boolean> cir){
        if(Services.OBJECT_DATA.isMinion(livingEntity)){
            cir.setReturnValue(false);
        }
    }
}
