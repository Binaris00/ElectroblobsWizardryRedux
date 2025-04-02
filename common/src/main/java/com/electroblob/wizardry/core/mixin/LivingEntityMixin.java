package com.electroblob.wizardry.core.mixin;

import com.electroblob.wizardry.api.content.effect.CustomMobEffectParticles;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


// TODO ???
@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Unique
    LivingEntity livingEntity = (LivingEntity)(Object)this;

    @Inject(at = @At("HEAD"), method = "jumpFromGround")
    public void EBWIZARDRY$LivingEntityJump(CallbackInfo ci){
//        if(livingEntity.hasEffect(EBMobEffects.FROST.get())){
//            if(livingEntity.getEffect(EBMobEffects.FROST.get()).getAmplifier() == 0){
//                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().x, 0.5, livingEntity.getDeltaMovement().z);
//            } else {
//                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().x, 0, livingEntity.getDeltaMovement().y);
//            }
//        }
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void EBWIZARDRY$tick(CallbackInfo ci){
        if (livingEntity.level().isClientSide) {
            for (MobEffectInstance effect : livingEntity.getActiveEffects()) {

                if (effect.getEffect() instanceof CustomMobEffectParticles) {

                    double x = livingEntity.getX()
                            + (livingEntity.level().random.nextDouble() - 0.5) * livingEntity.getBbWidth();
                    double y = livingEntity.getY()
                            + livingEntity.level().random.nextDouble() * livingEntity.getBbHeight();
                    double z = livingEntity.getZ()
                            + (livingEntity.level().random.nextDouble() - 0.5) * livingEntity.getBbWidth();

                    ((CustomMobEffectParticles) effect.getEffect()).spawnCustomParticle(livingEntity.level(), x, y, z);
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "hurt")
    public void EBWIZARDRY$LivingEntityModifyApplyDamage(DamageSource source, float f, CallbackInfoReturnable<Boolean> cir){
//        if(livingEntity.hasEffect(EBMobEffects.STATIC_AURA.get())){
//            if(source.getEntity() != null) {
//                source.getEntity().hurt(livingEntity.damageSources().indirectMagic(livingEntity, livingEntity), f / 2);
//            }
//        }
    }

    @Inject(at = @At("RETURN"), method = "getDamageAfterMagicAbsorb", cancellable = true)
    public void EBWIZARDRY$modifyDamage(DamageSource source, float f, CallbackInfoReturnable<Float> cir){
//        if(livingEntity.hasEffect(EBMobEffects.WARD.get())){
//            // reduces if indirect magic damage
//            if(source == livingEntity.damageSources().magic() || source == livingEntity.damageSources().indirectMagic(source.getDirectEntity(), source.getEntity())){
//                f *= Math.max(0, 1 - 0.2f * (1 + livingEntity.getEffect(EBMobEffects.WARD.get()).getAmplifier()));
//                cir.setReturnValue(f);
//            }
//
//        }
    }

}
