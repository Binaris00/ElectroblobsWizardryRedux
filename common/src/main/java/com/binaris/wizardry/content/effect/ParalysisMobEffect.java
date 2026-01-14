package com.binaris.wizardry.content.effect;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.effect.MagicMobEffect;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.level.Level;

public class ParalysisMobEffect extends MagicMobEffect {
    public ParalysisMobEffect() {
        super(MobEffectCategory.HARMFUL, 0);
    }

    @Override
    public void spawnCustomParticle(Level world, double x, double y, double z) {
        ParticleBuilder.create(EBParticles.SPARK).pos(x, y, z).spawn(world);
    }
}
