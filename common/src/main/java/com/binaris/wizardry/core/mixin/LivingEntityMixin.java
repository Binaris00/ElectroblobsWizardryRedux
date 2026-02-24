package com.binaris.wizardry.core.mixin;

import com.binaris.wizardry.api.content.event.EBLivingDeathEvent;
import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.api.content.event.EBLivingTick;
import com.binaris.wizardry.content.effect.FrostStepEffect;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.binaris.wizardry.core.integrations.accessories.EBAccessoriesIntegration;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique
    LivingEntity livingEntity = (LivingEntity) (Object) this;

    @Inject(at = @At("HEAD"), method = "tick")
    public void EBWIZARDRY$tick(CallbackInfo ci) {
        WizardryEventBus.getInstance().fire(new EBLivingTick(livingEntity, livingEntity.level()));
    }

    @Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
    public void EBWIZARDRY$livingCanBeAffected(MobEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        if (!(livingEntity instanceof Player player)) return;

        if (EBAccessoriesIntegration.isEquipped(player, EBItems.AMULET_ICE_IMMUNITY.get()))
            if (effect.getEffect() == EBMobEffects.FROST.get()) cir.setReturnValue(false);

        if (EBAccessoriesIntegration.isEquipped(player, EBItems.AMULET_WITHER_IMMUNITY.get()))
            if (effect.getEffect() == MobEffects.WITHER) cir.setReturnValue(false);
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void EBWIZARDRY$livingEntityHurt(DamageSource source, float f, CallbackInfoReturnable<Boolean> cir) {
        if (WizardryEventBus.getInstance().fire(new EBLivingHurtEvent(livingEntity, source, f))) cir.cancel();
    }


    @Inject(at = @At("HEAD"), method = "jumpFromGround")
    public void EBWIZARDRY$LivingEntityJump(CallbackInfo ci) {
        if (livingEntity.hasEffect(EBMobEffects.FROST.get())) {
            if (livingEntity.getEffect(EBMobEffects.FROST.get()).getAmplifier() == 0) {
                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().x, 0.5, livingEntity.getDeltaMovement().z);
            } else {
                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().x, 0, livingEntity.getDeltaMovement().y);
            }
        }
    }

    @Inject(method = "die", at = @At("HEAD"))
    public void EBWIZARDRY$LivingEntityDie(DamageSource damageSource, CallbackInfo ci) {
        WizardryEventBus.getInstance().fire(new EBLivingDeathEvent(livingEntity, damageSource));
    }

    @Inject(method = "shouldDropLoot", at = @At(value = "RETURN"), cancellable = true)
    public void EBWIZARDRY$dropLoot(CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity instanceof Mob mob) {
            if (Services.OBJECT_DATA.isMinion(mob)) cir.setReturnValue(false);
        }
    }

    @Inject(method = "shouldDropExperience", at = @At(value = "RETURN"), cancellable = true)
    public void EBWIZARDRY$dropExperience(CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity instanceof Mob mob) {
            if (Services.OBJECT_DATA.isMinion(mob)) cir.setReturnValue(false);
        }
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    public boolean EBWIZARDRY$avoidDamageOnFrost(LivingEntity living, DamageSource entity, float ev) {
        if (living.hasEffect(EBMobEffects.FROST.get())) {
            return false;
        }
        return living.hurt(entity, ev);
    }

    @Inject(method = "onChangedBlock", at = @At("HEAD"))
    public void EBWIZARDRY$frostStep(BlockPos pos, CallbackInfo ci) {
        if (livingEntity.hasEffect(EBMobEffects.FROST_STEP.get())) {
            FrostStepEffect.onEntityMoved(livingEntity, livingEntity.level(), pos,
                    livingEntity.getEffect(EBMobEffects.FROST_STEP.get()).getAmplifier());
        }
    }
}
