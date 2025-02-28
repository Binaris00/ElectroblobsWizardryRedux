package com.electroblob.wizardry.common.content.effect;

import com.electroblob.wizardry.api.common.effect.MagicMobEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class FireSkinMobEffect extends MagicMobEffect {
    public FireSkinMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0);
    }

    @Override
    public void spawnCustomParticle(Level world, double x, double y, double z) {
        world.addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int i) {
        livingEntity.clearFire();
    }

    @Override
    public boolean isDurationEffectTick(int i, int j) {
        return true;
    }
}
