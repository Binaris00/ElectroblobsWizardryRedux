package com.electroblob.wizardry.common.content.entity.construct;

import com.electroblob.wizardry.api.common.util.GeometryUtil;
import com.electroblob.wizardry.common.content.entity.abstr.ScaledConstructEntity;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBMobEffects;
import com.electroblob.wizardry.setup.registries.EBSounds;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityIceSpike extends ScaledConstructEntity {
    private Direction facing;

    public EntityIceSpike(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public EntityIceSpike(Level world) {
        super(EBEntities.ICE_SPICKES.get(), world);
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
        this.setRot(-facing.toYRot(), GeometryUtil.getPitch(facing));
        float yaw = (-facing.toYRot()) * (float) Math.PI / 180;
        float pitch = (GeometryUtil.getPitch(facing) - 90) * (float) Math.PI / 180;
        Vec3 min = this.position().add(new Vec3(-getBbWidth() / 2, 0, -getBbWidth() / 2).xRot(pitch).yRot(yaw));
        Vec3 max = this.position().add(new Vec3(getBbWidth() / 2, getBbHeight(), getBbWidth() / 2).xRot(pitch).yRot(yaw));
        this.setBoundingBox(new AABB(min.x, min.y, min.z, max.x, max.y, max.z));
    }

    public Direction getFacing() {
        return facing;
    }

    @Override
    public void tick() {
        double extensionSpeed = 0;

        if (!level().isClientSide) {
            if (lifetime - this.tickCount < 15) {
                extensionSpeed = -0.01 * (this.tickCount - (lifetime - 15));
            } else if (lifetime - this.tickCount < 25) {
                extensionSpeed = 0;
            } else if (lifetime - this.tickCount < 28) {
                extensionSpeed = 0.25;
            }

            if (facing != null) {
                this.move(MoverType.SELF, new Vec3(this.facing.getStepX() * extensionSpeed, this.facing.getStepY() * extensionSpeed, this.facing.getStepZ() * extensionSpeed));
            }
        }

        if (lifetime - this.tickCount == 30) this.playSound(EBSounds.ENTITY_ICE_SPIKE_EXTEND.get(), 1, 2.5f);

        if (!this.level().isClientSide) {
            for (Object entity : this.level().getEntities(this, this.getBoundingBox())) {
                if (entity instanceof LivingEntity livingEntity && this.isValidTarget(livingEntity)) {
                    DamageSource source = this.getCaster() == null ?
                            this.damageSources().magic() : this.damageSources().indirectMagic(this, this.getCaster());
                    if (livingEntity.hurt(source, 5 * this.damageMultiplier))
                        livingEntity.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(), 100, 0));
                }
            }
        }

        super.tick();
    }
}
