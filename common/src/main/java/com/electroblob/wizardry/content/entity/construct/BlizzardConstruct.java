package com.electroblob.wizardry.content.entity.construct;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.api.content.entity.construct.ScaledConstructEntity;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.*;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlizzardConstruct extends ScaledConstructEntity {
    public BlizzardConstruct(EntityType<?> type, Level level) {
        super(type, level);
        this.lifetime = Spells.BLIZZARD.property(DefaultProperties.DURATION);
    }

    public BlizzardConstruct(Level world) {
        super(EBEntities.BLIZZARD.get(), world);
        this.lifetime = Spells.BLIZZARD.property(DefaultProperties.DURATION);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount % 120 == 1) this.playSound(EBSounds.ENTITY_BLIZZARD_AMBIENT.get(), 1.0f, 1.0f);
        double radius = Spells.BLIZZARD.property(DefaultProperties.EFFECT_RADIUS) * sizeMultiplier;

        if (!this.level().isClientSide) {
            List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(radius, this.getX(), this.getY(), this.getZ(), level());

            for (LivingEntity target : targets) {
                if (this.isValidTarget(target)) {
                    EntityUtil.attackEntityWithoutKnockback(target, getCaster() != null ? EBMagicDamageSource.causeIndirectMagicDamage(this, getCaster(), EBDamageSources.FROST)
                                    : EBMagicDamageSource.causeDirectMagicDamage(this, EBDamageSources.SORCERY),
                            1 * damageMultiplier);
                }
                target.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(), 20));
            }

        } else {
            for (int i = 0; i < 6; i++) {
                double speed = (random.nextBoolean() ? 1 : -1) * (0.1 + 0.05 * random.nextDouble());
                ParticleBuilder.create(EBParticles.SNOW).pos(this.getX(), this.getY() + random.nextDouble() * getBbHeight(), this.getZ()).velocity(0, 0, 0)
                        .time(100).scale(2).spin(random.nextDouble() * (radius - 0.5) + 0.5, speed).shaded(true).spawn(level());
            }

            for (int i = 0; i < 3; i++) {
                double speed = (random.nextBoolean() ? 1 : -1) * (0.05 + 0.02 * random.nextDouble());
                ParticleBuilder.create(EBParticles.CLOUD).pos(this.getX(), this.getY() + random.nextDouble() * (getBbHeight() - 0.5), this.getZ()).color(0xffffff).shaded(true).spin(random.nextDouble() * (radius - 1) + 0.5, speed).spawn(level());
            }
        }
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return EntityDimensions.scalable(Spells.BLIZZARD.property(DefaultProperties.EFFECT_RADIUS) * 2, 3);
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }
}
