package com.electroblob.wizardry.common.content.entity.construct;

import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.api.common.util.EBMagicDamageSource;
import com.electroblob.wizardry.common.content.entity.abstr.ScaledConstructEntity;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityFireRing extends ScaledConstructEntity {
    public EntityFireRing(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public EntityFireRing(Level world) {
        super(EBEntities.RING_OF_FIRE.get(), world);
        this.lifetime = Spells.RING_OF_FIRE.property(DefaultProperties.DURATION);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return EntityDimensions.scalable((float) (Spells.RING_OF_FIRE.property(DefaultProperties.EFFECT_RADIUS) * 2), 1);
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    @Override
    public void tick() {
        if (this.tickCount % 40 == 1) {
            this.playSound(EBSounds.ENTITY_FIRE_RING_AMBIENT.get(), 4.0f, 0.7f);
        }

        super.tick();

        if (this.tickCount % 5 == 0 && !this.level().isClientSide) {
            List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(getBbWidth() / 2, this.getX(), this.getY(),
                    this.getZ(), this.level());

            for (LivingEntity target : targets) {
                if (this.isValidTarget(target) && !EBMagicDamageSource.isEntityImmune(EBDamageSources.FIRE, target)) {
                    Vec3 originalVec = target.getDeltaMovement();

                    target.setSecondsOnFire(Spells.RING_OF_FIRE.property(DefaultProperties.EFFECT_DURATION));

                    float damage = Spells.RING_OF_FIRE.property(DefaultProperties.DAMAGE) * damageMultiplier;

                    EBMagicDamageSource.causeMagicDamage(this, target, damage, EBDamageSources.FIRE, false);

                    target.setDeltaMovement(originalVec);
                }
            }
        }
    }
}
