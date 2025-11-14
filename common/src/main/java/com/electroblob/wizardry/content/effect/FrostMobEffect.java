package com.electroblob.wizardry.content.effect;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.effect.MagicMobEffect;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class FrostMobEffect extends MagicMobEffect {
    public FrostMobEffect() {
        super(MobEffectCategory.HARMFUL, 0);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "35dded48-2f19-4541-8510-b29e2dc2cd51", -0.5, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int i) {
        if (livingEntity.isOnFire()) {
            if (livingEntity.hasEffect(this)) {
                livingEntity.removeEffect(this);
                livingEntity.setSecondsOnFire(0);
            }
        }

        livingEntity.setIsInPowderSnow(true);

        super.applyEffectTick(livingEntity, i);
    }

    @Override
    public boolean isDurationEffectTick(int i, int j) {
        return true;
    }

    @Override
    public void spawnCustomParticle(Level world, double x, double y, double z) {
        ParticleBuilder.create(EBParticles.SNOW).pos(x, y, z).time(15 + world.random.nextInt(5)).spawn(world);
    }
}
