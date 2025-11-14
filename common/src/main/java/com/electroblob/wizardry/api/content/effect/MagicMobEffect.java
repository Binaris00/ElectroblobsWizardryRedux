package com.electroblob.wizardry.api.content.effect;

import com.electroblob.wizardry.api.content.event.EBLivingTick;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;

public abstract class MagicMobEffect extends MobEffect implements CustomMobEffectParticles {
    public MagicMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

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
}
