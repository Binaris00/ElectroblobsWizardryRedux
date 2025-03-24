package com.electroblob.wizardry.common.content.entity.construct;

import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.common.content.entity.abstr.ScaledConstructEntity;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityFireSigil extends ScaledConstructEntity {
    public EntityFireSigil(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public EntityFireSigil(Level world) {
        super(EBEntities.FIRE_SIGIL.get(), world);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return EntityDimensions.scalable(1 * 2, 0.2f);
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(getBbWidth() / 2, getX(), getY(), getZ(), level());

            for (LivingEntity target : targets) {
                if (this.isValidTarget(target)) {
                    Vec3 originalVec = target.getDeltaMovement();

                    target.hurt(this.getCaster() != null
                            ? target.damageSources().onFire()
                            : target.damageSources().magic(), 6
                            * damageMultiplier);

                    target.setDeltaMovement(originalVec);

                    // TODO MAGIC DAMAGE
//                    if (!MagicDamage.isEntityImmune(DamageType.FIRE, target))
                    target.setSecondsOnFire(10);

                    this.playSound(EBSounds.ENTITY_FIRE_SIGIL_TRIGGER.get(), 1, 1);

                    this.discard();
                }
            }
        } else if (this.random.nextInt(15) == 0) {
            double radius = (0.5 + random.nextDouble() * 0.3) * getBbWidth() / 2;
            float angle = random.nextFloat() * (float) Math.PI * 2;
            level().addParticle(ParticleTypes.FLAME, this.getX() + radius * Mth.cos(angle), this.getY() + 0.1, this.getZ() + radius * Mth.sin(angle), 0, 0, 0);
        }
    }
}
