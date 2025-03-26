package com.electroblob.wizardry.common.content.entity.construct;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.api.common.util.EBMagicDamageSource;
import com.electroblob.wizardry.common.content.entity.abstr.ScaledConstructEntity;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBMobEffects;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityBlizzard extends ScaledConstructEntity {
    public EntityBlizzard(EntityType<?> type, Level level) {
        super(type, level);
    }

    public EntityBlizzard(Level world) {
        super(EBEntities.BLIZZARD.get(), world);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return EntityDimensions.scalable(3 * 2, 3);
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    @Override
    public void tick() {
        if (this.tickCount % 120 == 1) {
            this.playSound(EBSounds.ENTITY_BLIZZARD_AMBIENT.get(), 1.0f, 1.0f);
        }

        super.tick();

        double radius = 3 * sizeMultiplier;

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
}
