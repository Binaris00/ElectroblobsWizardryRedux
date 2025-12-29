package com.electroblob.wizardry.content.entity.construct;

import com.electroblob.wizardry.api.content.entity.construct.ScaledConstructEntity;
import com.electroblob.wizardry.api.content.util.MagicDamageSource;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.earth.Boulder;
import com.electroblob.wizardry.core.ClientSpellSoundManager;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BoulderConstruct extends ScaledConstructEntity {
    private boolean soundStarted = false;
    private static final double AIR_FRICTION = 0.99;
    private static final double GROUND_FRICTION = 0.985;

    public BoulderConstruct(EntityType<?> type, Level world) {
        super(type, world);
        this.noPhysics = false;
        this.setNoGravity(false);
        lifetime = Spells.BOULDER.property(DefaultProperties.DURATION);
    }

    public BoulderConstruct(Level world) {
        super(EBEntities.BOULDER.get(), world);
        this.noPhysics = false;
        this.setNoGravity(false);
        lifetime = Spells.BOULDER.property(DefaultProperties.DURATION);
    }

    public void setHorizontalVelocity(double velX, double velZ) {
        this.setDeltaMovement(velX, this.getDeltaMovement().y, velZ);
    }

    @Override
    public void tick() {
        if (level().isClientSide && !soundStarted && onGround()) {
            soundStarted = true;
            ClientSpellSoundManager.playMovingSound(this, EBSounds.ENTITY_BOULDER_ROLL.get(), SoundSource.PLAYERS, 1, 1, true);
        }

        super.tick();

        Vec3 motion = this.getDeltaMovement();
        double newY = motion.y - 0.04;

        double friction = onGround() ? GROUND_FRICTION : AIR_FRICTION;
        double newX = motion.x * friction;
        double newZ = motion.z * friction;

        this.setDeltaMovement(newX, newY, newZ);
        this.move(MoverType.SELF, this.getDeltaMovement());

        List<LivingEntity> collided = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());

        float damage = Spells.BOULDER.property(DefaultProperties.DAMAGE) * damageMultiplier;
        float knockback = Spells.BOULDER.property(Boulder.KNOCKBACK_STRENGTH).floatValue();

        for (LivingEntity entity : collided) {
            if (!isValidTarget(entity)) continue;

            boolean crushBonus = entity.getY() < this.getY()
                    && entity.getBoundingBox().minX > this.getBoundingBox().minX
                    && entity.getBoundingBox().maxX < this.getBoundingBox().maxX
                    && entity.getBoundingBox().minZ > this.getBoundingBox().minZ
                    && entity.getBoundingBox().maxZ < this.getBoundingBox().maxZ;

            if (EntityUtil.attackEntityWithoutKnockback(entity, MagicDamageSource.causeIndirectMagicDamage(this, getCaster(), EBDamageSources.SORCERY), crushBonus ? damage * 1.5f : damage) && !crushBonus) {
                EntityUtil.applyStandardKnockback(this, entity, knockback);
                entity.setDeltaMovement(entity.getDeltaMovement().add(motion.x, 0, motion.z));
            }
            entity.playSound(EBSounds.ENTITY_BOULDER_HIT.get(), 1, 1);
        }

        if (horizontalCollision) {
            shakeNearbyPlayers();
        }

        // Particles
        if (onGround()) {
            for (int i = 0; i < 10; i++) {
                double particleX = this.getX() + getBbWidth() * 0.7 * (random.nextDouble() - 0.5);
                double particleZ = this.getZ() + getBbWidth() * 0.7 * (random.nextDouble() - 0.5);

                BlockState block = level().getBlockState(this.blockPosition().below());

                if (block.getBlock() != Blocks.AIR) {
                    level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, block),
                            particleX, this.getY(), particleZ, 0, 0.2, 0);
                }
            }
        }

        if (onGround() && motion.horizontalDistance() < 0.005) {
            this.despawn();
        }
    }


    private void shakeNearbyPlayers() {
        // TODO Shake screen
        //EntityUtil.getEntitiesWithinRadius(10, getX(), getY(), getZ(), level(), Player.class).forEach(p -> Wizardry.proxy.shakeScreen(p, 8));
    }

    @Override
    public void despawn() {
        if (level().isClientSide) {
            for (int i = 0; i < 200; i++) {
                double x = getX() + (random.nextDouble() - 0.5) * getBbWidth();
                double y = getY() + random.nextDouble() * getBbHeight();
                double z = getZ() + (random.nextDouble() - 0.5) * getBbWidth();
                level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()),
                        x, y, z, (x - getX()) * 0.1,
                        (y - getY() + getBbHeight() / 2) * 0.1, (z - getZ()) * 0.1);
            }

            level().playLocalSound(getX(), getY(), getZ(), EBSounds.ENTITY_BOULDER_BREAK_BLOCK.get(), SoundSource.BLOCKS, 1, 1, false);
        }

        super.despawn();
    }

    @Override
    public void move(@NotNull MoverType type, @NotNull Vec3 vec) {
        super.move(type, vec);
        double distance = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
        this.setXRot((float) (this.getXRot() + Math.toDegrees(distance / (getBbWidth() / 2))));
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, @NotNull DamageSource source) {
        if (level().isClientSide) {
            for (int i = 0; i < 40; i++) {
                double particleX = this.getX() - 1.5 + 3 * random.nextDouble();
                double particleZ = this.getZ() - 1.5 + 3 * random.nextDouble();
                BlockState block = level().getBlockState(this.blockPosition().atY(this.blockPosition().getY() - 2));

                if (block.getBlock() != Blocks.AIR) {
                    level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, block),
                            particleX, this.getY(), particleZ, particleX - this.getX(), 0, particleZ - this.getZ());
                }
            }

            if (distance > 1.2) {
                level().playLocalSound(getX(), getY(), getZ(), EBSounds.ENTITY_BOULDER_LAND.get(), SoundSource.BLOCKS, Math.min(2, distance / 4), 1, false);
                shakeNearbyPlayers();
            }
        }
        return super.causeFallDamage(distance, damageMultiplier, source);
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return this.getBoundingBox();
    }
}