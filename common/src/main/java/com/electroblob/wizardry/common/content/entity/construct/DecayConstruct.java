package com.electroblob.wizardry.common.content.entity.construct;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.common.content.entity.abstr.ScaledConstructEntity;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBMobEffects;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.Spells;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class DecayConstruct extends ScaledConstructEntity {
    public int textureIndex;

    public DecayConstruct(Level world) {
        this(EBEntities.DECAY.get(), world);
    }

    public DecayConstruct(EntityType<?> entityType, Level world) {
        super(entityType, world);
        textureIndex = this.random.nextInt(10);
        this.noCulling = true;
        this.lifetime = Spells.DECAY.property(DefaultProperties.DURATION);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.random.nextInt(700) == 0 && this.tickCount + 100 < lifetime) {
            this.playSound(EBSounds.ENTITY_DECAY_AMBIENT.get(), 0.2F + random.nextFloat() * 0.2F, 0.6F + random.nextFloat() * 0.15F);
        }
        if (!this.level().isClientSide) {
            List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(1.0d, this.getX(), this.getY(), this.getZ(), this.level());
            for (LivingEntity target : targets) {
                if (target != this.getCaster()) {
                    if (!target.hasEffect(EBMobEffects.DECAY.get()))
                        target.addEffect(new MobEffectInstance(EBMobEffects.DECAY.get(), Spells.DECAY.property(DefaultProperties.EFFECT_DURATION)));
                }
            }
        } else if (this.random.nextInt(15) == 0) {
            double radius = random.nextDouble() * 0.8;
            float angle = random.nextFloat() * (float) Math.PI * 2;
            float brightness = random.nextFloat() * 0.4f;

            ParticleBuilder.create(EBParticles.DARK_MAGIC)
                    .pos(this.getX() + radius * Mth.cos(angle), this.getY(), this.getZ() + radius * Mth.sin(angle))
                    .color(brightness, 0, brightness + 0.1f)
                    .spawn(level());
        }
    }
}
