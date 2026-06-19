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
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base class for all custom mob effects (potions) in the mod. Implementations of this class can override event callbacks
 * from {@link IMobEventEffect} to execute behavior when events are fired for entities holding this effect.
 *
 * @see IMobEventEffect
 */
public abstract class MagicMobEffect extends MobEffect implements CustomMobEffectParticles, IMobEventEffect {
    public MagicMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    // TODO Remove this
    public static void onLivingTick(EBLivingTick event) {
        if (!event.getLevel().isClientSide) return;
        for (MobEffectInstance effect : event.getEntity().getActiveEffects()) {
            if (effect.getEffect() instanceof CustomMobEffectParticles) {
                double x = event.getEntity().getX()
                        + (event.getLevel().random.nextDouble() - 0.5) * event.getEntity().getBbWidth();
                double y = event.getEntity().getY()
                        + event.getLevel().random.nextDouble() * event.getEntity().getBbHeight();
                double z = event.getEntity().getZ()
                        + (event.getLevel().random.nextDouble() - 0.5) * event.getEntity().getBbWidth();

                ((CustomMobEffectParticles) effect.getEffect()).spawnCustomParticle(event.getLevel(), x, y, z);
            }
        }
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
