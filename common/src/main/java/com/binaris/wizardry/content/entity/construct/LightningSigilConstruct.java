package com.binaris.wizardry.content.entity.construct;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.construct.ScaledConstructEntity;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.Spells;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LightningSigilConstruct extends ScaledConstructEntity {
    public static final SpellProperty<Integer> SECOND_RANGE = SpellProperty.intProperty("second_range");

    public LightningSigilConstruct(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public LightningSigilConstruct(Level world) {
        super(EBEntities.LIGHTNING_SIGIL.get(), world);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount > 600 && this.getCaster() == null && !this.level().isClientSide) {
            this.discard();
        }

        List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(getBbWidth() / 2, getX(), getY(), getZ(), level());
        for (LivingEntity target : targets) {
            if (!isValidTarget(target)) continue;

            Vec3 originalVec = target.getDeltaMovement();

            // Inline damage check to avoid extra local variable
            if (!MagicDamageSource.causeMagicDamage(this, target,
                    Spells.LIGHTNING_SIGIL.property(DefaultProperties.DAMAGE) * damageMultiplier,
                    EBDamageSources.SHOCK)) continue;

            playSound(EBSounds.ENTITY_LIGHTNING_SIGIL_TRIGGER.get(), 1.0f, 1.0f);
            target.setDeltaMovement(originalVec);

            double seekerRange = Spells.LIGHTNING_SIGIL.property(DefaultProperties.RANGE);
            List<LivingEntity> secondaryTargets = EntityUtil.getLivingWithinRadius(seekerRange, target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(), level());
            int maxTargets = Spells.LIGHTNING_SIGIL.property(DefaultProperties.MAX_TARGETS);
            int hitCount = 0;
            for (LivingEntity secondary : secondaryTargets) {
                if (hitCount >= maxTargets) break;
                if (secondary == target || !isValidTarget(secondary)) continue;
                hitCount++;

                if (level().isClientSide) {
                    ParticleBuilder.create(EBParticles.LIGHTNING).entity(target)
                            .pos(0, target.getBbHeight() / 2, 0).target(secondary)
                            .spawn(level());
                    ParticleBuilder.spawnShockParticles(level(),
                            secondary.getX(), secondary.getY() + secondary.getBbHeight() / 2, secondary.getZ());
                } else {
                    secondary.playSound(EBSounds.ENTITY_LIGHTNING_SIGIL_TRIGGER.get(),
                            1.0F, level().random.nextFloat() * 0.4F + 1.5F);

                    secondary.hurt(MagicDamageSource.causeIndirectMagicDamage(this, getCaster(), EBDamageSources.SHOCK),
                            Spells.LIGHTNING_SIGIL.property(DefaultProperties.DAMAGE) * damageMultiplier);
                }
            }

            if (!level().isClientSide) this.discard();
        }

        if (level().isClientSide && random.nextInt(15) == 0) {
            double radius = (0.5 + random.nextDouble() * 0.3) * getBbWidth() / 2;
            float angle = random.nextFloat() * (float) Math.PI * 2;
            ParticleBuilder.create(EBParticles.SPARK)
                    .pos(getX() + radius * Mth.cos(angle), getY() + 0.1, getZ() + radius * Mth.sin(angle))
                    .spawn(level());
        }
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return EntityDimensions.scalable(Spells.LIGHTNING_SIGIL.property(DefaultProperties.EFFECT_RADIUS) * 2, 0.2f);
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }
}
