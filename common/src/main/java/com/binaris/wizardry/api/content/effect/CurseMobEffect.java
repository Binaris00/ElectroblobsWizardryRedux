package com.binaris.wizardry.api.content.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.level.Level;

public class CurseMobEffect extends MagicMobEffect {
    public CurseMobEffect(MobEffectCategory mobEffectCategory, int i) {
        super(mobEffectCategory, i);
    }

    @Override
    public void spawnCustomParticle(Level world, double x, double y, double z) {

    }
}
