package com.binaris.wizardry.api.content.effect;

import com.binaris.wizardry.api.content.event.EBLivingDeathEvent;
import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.api.content.event.EBLivingTick;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.core.IMobEventEffect;
import com.binaris.wizardry.core.MobEffectContext;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base class for all custom mob effects (potions) in the mod. Implementations of this class can override event callbacks
 * from {@link IMobEventEffect} to execute behavior when events are fired for entities holding this effect.
 *
 * @see IMobEventEffect
 */
public abstract class MagicMobEffect extends MobEffect implements CustomMobEffectParticles, IMobEventEffect {
    private int particleCount = 1;
    private double particleOffsetScale = 1.0;
    private int particleTickInterval = 1;
    private boolean hideVanillaParticles = true;

    public MagicMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public static void onParticleTick(EBLivingTick event) {
        if (!event.getLevel().isClientSide) return;
        LivingEntity entity = event.getEntity();
        Level level = event.getLevel();

        for (MobEffectInstance instance : entity.getActiveEffects()) {
            if (!(instance.getEffect() instanceof MagicMobEffect magic)) continue;
            if (entity.tickCount % magic.particleTickInterval != 0) continue;
            if (!instance.isVisible()) continue;

            for (int i = 0; i < magic.particleCount; i++) {
                double x = entity.getX() + (level.random.nextDouble() - 0.5) * entity.getBbWidth() * magic.particleOffsetScale;
                double y = entity.getY() + level.random.nextDouble() * entity.getBbHeight();
                double z = entity.getZ() + (level.random.nextDouble() - 0.5) * entity.getBbWidth() * magic.particleOffsetScale;
                magic.spawnCustomParticle(level, x, y, z);
            }
        }
    }

    protected MagicMobEffect particleCount(int count) {
        this.particleCount = count;
        return this;
    }

    protected MagicMobEffect particleOffset(double scale) {
        this.particleOffsetScale = scale;
        return this;
    }

    protected MagicMobEffect particleInterval(int ticks) {
        this.particleTickInterval = ticks;
        return this;
    }

    protected MagicMobEffect hideVanillaParticles() {
        this.hideVanillaParticles = true;
        return this;
    }

    public boolean shouldHideVanillaParticles() {
        return hideVanillaParticles;
    }

    /**
     * This class is used to call the event methods of the MagicMobEffect, you shouldn't use it directly.
     */
    public static class EventCaller {
        public static void callTickEvent(EBLivingTick event) {
            for (MobEffectInstance instance : new ArrayList<>(event.getEntity().getActiveEffects())) {
                if (instance.getEffect() instanceof MagicMobEffect magicEffect) {
                    magicEffect.onTick(event.getEntity(), event.getLevel(), new MobEffectContext(instance));
                }
            }
        }

        public static void callHurtEntityEvent(EBLivingHurtEvent event) {
            if (!(event.getSource().getEntity() instanceof LivingEntity livingEntity)) return;
            AtomicDouble amount = new AtomicDouble(event.getAmount());
            AtomicBoolean canceled = new AtomicBoolean(event.isCanceled());
            for (MobEffectInstance instance : new ArrayList<>(livingEntity.getActiveEffects())) {
                if (instance.getEffect() instanceof MagicMobEffect magicEffect) {
                    magicEffect.onHurtEntity(livingEntity, event.getDamagedEntity(), event.getSource(), amount, canceled, new MobEffectContext(instance));
                }
            }
            if (amount.floatValue() != event.getAmount()) event.setAmount(amount.floatValue());
            if (canceled.get()) event.setCanceled(true);
        }

        public static void callUserHurtEvent(EBLivingHurtEvent event) {
            LivingEntity user = event.getDamagedEntity();
            AtomicDouble amount = new AtomicDouble(event.getAmount());
            AtomicBoolean canceled = new AtomicBoolean(event.isCanceled());
            for (MobEffectInstance instance : new ArrayList<>(user.getActiveEffects())) {
                if (instance.getEffect() instanceof MagicMobEffect magicEffect) {
                    magicEffect.onUserHurt(user, event.getSource(), amount, canceled, new MobEffectContext(instance));
                }
            }
            if (amount.floatValue() != event.getAmount()) event.setAmount(amount.floatValue());
            if (canceled.get()) event.setCanceled(true);
        }

        public static void callKillEntity(EBLivingDeathEvent event) {
            if (!(event.getSource().getEntity() instanceof LivingEntity livingEntity)) return;
            for (MobEffectInstance mobEffectInstance : new ArrayList<>(livingEntity.getActiveEffects())) {
                if (mobEffectInstance.getEffect() instanceof MagicMobEffect magicMobEffect) {
                    magicMobEffect.onKillEntity(livingEntity, event.getEntity(), event.getSource(), new MobEffectContext(mobEffectInstance));
                }
            }
        }

        public static void callSpellPreCast(SpellCastEvent.Pre event) {
            if (event.getCaster() == null) return;
            for (MobEffectInstance mobEffectInstance : new ArrayList<>(event.getCaster().getActiveEffects())) {
                if (mobEffectInstance.getEffect() instanceof MagicMobEffect magicMobEffect) {
                    magicMobEffect.onSpellPreCast(event, new MobEffectContext(mobEffectInstance));
                }
            }
        }

        public static void callSpellPostCast(SpellCastEvent.Post event) {
            if (event.getCaster() == null) return;
            for (MobEffectInstance mobEffectInstance : new ArrayList<>(event.getCaster().getActiveEffects())) {
                if (mobEffectInstance.getEffect() instanceof MagicMobEffect magicMobEffect) {
                    magicMobEffect.onSpellPostCast(event, new MobEffectContext(mobEffectInstance));
                }
            }
        }
    }
}
