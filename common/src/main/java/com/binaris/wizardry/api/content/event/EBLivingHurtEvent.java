package com.binaris.wizardry.api.content.event;

import com.binaris.wizardry.api.content.event.abstr.WizardryCancelableEvent;
import com.binaris.wizardry.core.mixin.LivingEntityMixin;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This applies to loaders with the Mixin
 * {@link LivingEntityMixin#EBWIZARDRY$livingEntityHurt(DamageSource, float, CallbackInfoReturnable) LivingEntityMixin#EBWIZARDRY$livingEntityHurt}
 *
 */
public class EBLivingHurtEvent extends WizardryCancelableEvent {
    private final LivingEntity damagedEntity;
    private final DamageSource source;
    private float amount;

    public EBLivingHurtEvent(LivingEntity damagedEntity, DamageSource source, float amount) {
        this.damagedEntity = damagedEntity;
        this.source = source;
        this.amount = amount;
    }

    public LivingEntity getDamagedEntity() {
        return damagedEntity;
    }

    public DamageSource getSource() {
        return source;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}