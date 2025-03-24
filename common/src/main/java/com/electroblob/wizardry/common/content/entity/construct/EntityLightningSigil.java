package com.electroblob.wizardry.common.content.entity.construct;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.common.content.entity.abstr.ScaledConstructEntity;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityLightningSigil extends ScaledConstructEntity {
    public EntityLightningSigil(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public EntityLightningSigil(Level world) {
        super(EBEntities.LIGHTNING_SIGIL.get(), world);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(1 * 2, 0.2f);
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount > 600 && this.getCaster() == null && !this.level().isClientSide) {
            this.discard();
        }

        List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(getBbWidth() / 2, this.getX(), this.getY(), this.getZ(), this.level());

        for (LivingEntity target : targets) {
            if (this.isValidTarget(target)) {
                Vec3 originalVec = target.getDeltaMovement();

                if (target.hurt(getCaster() != null ?
                        this.damageSources().indirectMagic(this, getCaster()) : this.damageSources().magic(), 6 * damageMultiplier)) {
                    target.setDeltaMovement(originalVec);

                    this.playSound(EBSounds.ENTITY_LIGHTNING_SIGIL_TRIGGER.get(), 1.0f, 1.0f);

                    double seekerRange = 1;

                    List<LivingEntity> secondaryTargets = EntityUtil.getLivingWithinRadius(seekerRange, target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(), level());

                    for (int j = 0; j < Math.min(secondaryTargets.size(), 3); j++) {
                        LivingEntity secondaryTarget = secondaryTargets.get(j);

                        if (secondaryTarget != target && this.isValidTarget(secondaryTarget)) {
                            if (level().isClientSide) {
                                ParticleBuilder.create(EBParticles.LIGHTNING).entity(target)
                                        .pos(0, target.getBbHeight() / 2, 0).target(secondaryTarget).spawn(level());

                                ParticleBuilder.spawnShockParticles(level(), secondaryTarget.getX(), secondaryTarget.getY() + secondaryTarget.getBbHeight() / 2, secondaryTarget.getZ());
                            }

                            secondaryTarget.playSound(EBSounds.ENTITY_LIGHTNING_SIGIL_TRIGGER.get(), 1.0F, level().random.nextFloat() * 0.4F + 1.5F);


                            secondaryTarget.hurt(this.damageSources().indirectMagic(this, getCaster()), 4 * damageMultiplier);
                        }

                    }
                    this.discard();
                }
            }
        }

        if (this.level().isClientSide && this.random.nextInt(15) == 0) {
            double radius = (0.5 + random.nextDouble() * 0.3) * getBbWidth() / 2;
            float angle = random.nextFloat() * (float) Math.PI * 2;
            ParticleBuilder.create(EBParticles.SPARK)
                    .pos(this.getX() + radius * Mth.cos(angle), this.getY() + 0.1, this.getZ() + radius * Mth.sin(angle))
                    .spawn(level());
        }
    }
}
