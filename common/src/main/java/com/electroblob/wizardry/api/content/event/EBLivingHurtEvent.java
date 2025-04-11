package com.electroblob.wizardry.api.content.event;

import com.electroblob.wizardry.api.content.event.abstr.WizardryCancelableEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This applies to loaders with the Mixin
 * {@link com.electroblob.wizardry.core.mixin.LivingEntityMixin#EBWIZARDRY$livingEntityHurt(DamageSource, float, CallbackInfoReturnable) LivingEntityMixin#EBWIZARDRY$livingEntityHurt}
 * */
public class EBLivingHurtEvent extends WizardryCancelableEvent {
    private LivingEntity damagedEntity;
    private DamageSource source;
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

    public void setSource(DamageSource source) {
        this.source = source;
    }

    public void setDamagedEntity(LivingEntity damagedEntity) {
        this.damagedEntity = damagedEntity;
    }
}
