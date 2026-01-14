package com.binaris.wizardry.content.effect;

import com.binaris.wizardry.api.content.effect.MagicMobEffect;
import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.level.Level;

public class WardMobEffect extends MagicMobEffect {
    public WardMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xc991d0);
    }

    public static void onLivingHurt(EBLivingHurtEvent event) {
        if (event.isCanceled()) return;

        if (event.getDamagedEntity().hasEffect(EBMobEffects.WARD.get())) {
            EBDamageSources.TYPES.forEach(damageType -> {
                if (event.getSource().is(damageType)) {
                    float f = event.getAmount();
                    f *= Math.max(0, 1 - 0.2f * (1 + event.getDamagedEntity().getEffect(EBMobEffects.WARD.get()).getAmplifier()));
                    event.setAmount(f);
                }
            });
        }
    }

    @Override
    public void spawnCustomParticle(Level world, double x, double y, double z) {

    }
}
