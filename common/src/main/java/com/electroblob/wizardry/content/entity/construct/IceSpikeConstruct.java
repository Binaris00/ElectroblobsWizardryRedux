package com.electroblob.wizardry.content.entity.construct;

import com.electroblob.wizardry.api.content.entity.construct.ScaledConstructEntity;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.api.content.util.GeometryUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.*;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class IceSpikeConstruct extends ScaledConstructEntity {
    private Direction facing;

    public IceSpikeConstruct(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public IceSpikeConstruct(Level world) {
        super(EBEntities.ICE_SPICKES.get(), world);
    }

    @Override
    public void tick() {
        super.tick();
        if (lifetime - this.tickCount == 30) this.playSound(EBSounds.ENTITY_ICE_SPIKE_EXTEND.get(), 1, 2.5f);
        if (level().isClientSide) return;
        double extensionSpeed = 0;

        if (lifetime - this.tickCount < 15) {
            extensionSpeed = -0.01 * (this.tickCount - (lifetime - 15));
        } else if (lifetime - this.tickCount < 25) {
            extensionSpeed = 0;
        } else if (lifetime - this.tickCount < 28) {
            extensionSpeed = 0.25;
        }

        if (facing != null) this.move(MoverType.SELF, new Vec3(this.facing.getStepX() * extensionSpeed,
                this.facing.getStepY() * extensionSpeed, this.facing.getStepZ() * extensionSpeed));

        for (Object entity : this.level().getEntities(this, this.getBoundingBox())) {
            if (entity instanceof LivingEntity livingEntity && this.isValidTarget(livingEntity)) {
                if (EBMagicDamageSource.causeMagicDamage(this, livingEntity, 5 * this.damageMultiplier, EBDamageSources.FROST, false))
                    livingEntity.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(),
                            Spells.ICE_SPICKES.property(DefaultProperties.EFFECT_DURATION),
                            Spells.ICE_SPICKES.property(DefaultProperties.EFFECT_STRENGTH)));
            }
        }
    }

    public Direction getFacing() {
        return facing;
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
}
