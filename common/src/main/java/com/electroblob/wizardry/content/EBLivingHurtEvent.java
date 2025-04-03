package com.electroblob.wizardry.content;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Internal use of <b>ElectroBlob's Wizardry</b>
 * <br> <br>
 * Initialized when a {@link LivingEntity} is hurt.
 * This event is called from
 * {@link com.electroblob.wizardry.EBEventHelper#onLivingHurtEvent(EBLivingHurtEvent)}
 * and {@link com.electroblob.wizardry.core.mixin.LivingEntityMixin#EBWIZARDRY$livingEntityHurt(DamageSource, float, CallbackInfoReturnable)}
 * */
public final class EBLivingHurtEvent {
    private LivingEntity damagedEntity;
    private DamageSource source;
    private float amount;
    private boolean cancelled = false;

    private EBLivingHurtEvent(LivingEntity damagedEntity, DamageSource source, float amount) {
        this.damagedEntity = damagedEntity;
        this.source = source;
        this.amount = amount;
    }

    public static EBLivingHurtEvent create(LivingEntity damagedEntity, DamageSource source, float amount) {
        return new EBLivingHurtEvent(damagedEntity, source, amount);
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

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
