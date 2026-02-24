package com.binaris.wizardry.content.entity.construct;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.DeferredObject;
import com.binaris.wizardry.api.content.entity.construct.ScaledConstructEntity;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.client.particle.ParticleTornado;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.ClientSpellSoundManager;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.Spells;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TornadoConstruct extends ScaledConstructEntity {
    private static final EntityDataAccessor<Float> VEL_X = SynchedEntityData.defineId(TornadoConstruct.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> VEL_Z = SynchedEntityData.defineId(TornadoConstruct.class, EntityDataSerializers.FLOAT);

    public TornadoConstruct(EntityType<?> type, Level world) {
        super(type, world);
    }

    public TornadoConstruct(Level world) {
        super(EBEntities.TORNADO.get(), world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VEL_X, 0.0F);
        this.entityData.define(VEL_Z, 0.0F);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return EntityDimensions.scalable(Spells.TORNADO.property(DefaultProperties.EFFECT_RADIUS), 8);
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    public void setHorizontalVelocity(float velX, float velZ) {
        this.entityData.set(VEL_X, velX);
        this.entityData.set(VEL_Z, velZ);
    }

    public double getVelX() {
        return this.entityData.get(VEL_X);
    }

    public double getVelZ() {
        return this.entityData.get(VEL_Z);
    }

    @Override
    public void tick() {
        super.tick();
        double radius = getBbWidth() / 2;

        if (this.tickCount % 120 == 1 && level().isClientSide) {
            ClientSpellSoundManager.playMovingSound(this, EBSounds.ENTITY_TORNADO_AMBIENT.get(), SoundSource.HOSTILE, 1.0f, 1.0f, false);
        }

        this.move(MoverType.SELF, new Vec3(getVelX(), this.getDeltaMovement().y, getVelZ()));
        BlockPos pos = this.blockPosition();
        Integer y = BlockUtil.getNearestSurface(level(), pos.above(3), Direction.UP, 5, true,
                BlockUtil.SurfaceCriteria.NOT_AIR_TO_AIR);

        if (y != null && this.level().getBlockState(pos.above(y - pos.getY())).is(Blocks.LAVA)) {
            this.setSecondsOnFire(5);
        }

        if (!this.level().isClientSide) {
            List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(radius, this.getX(), this.getY(), this.getZ(), this.level());

            for (LivingEntity target : targets) {
                if (target instanceof Player && getCaster() instanceof Player) continue;

                if (this.isValidTarget(target)) {
                    applyTornadoEffects(target);
                }
            }
        } else {
            spawnParticles();
        }
    }

    private void applyTornadoEffects(LivingEntity target) {
        double velY = target.getDeltaMovement().y;
        double dx = (this.getX() - target.getX() > 0 ? 0.5 : -0.5) - (this.getX() - target.getX()) * 0.125;
        double dz = (this.getZ() - target.getZ() > 0 ? 0.5 : -0.5) - (this.getZ() - target.getZ()) * 0.125;

        if (this.isOnFire()) target.setSecondsOnFire(4);

        float damage = Spells.TORNADO.property(DefaultProperties.DAMAGE) * damageMultiplier;
        MagicDamageSource.causeMagicDamage(this, target, damage, EBDamageSources.SORCERY);
        target.setDeltaMovement(dx, velY + Spells.TORNADO.property(DefaultProperties.ACCELERATION), dz);

        if (target instanceof ServerPlayer sp)
            sp.connection.send(new ClientboundSetEntityMotionPacket(target));
    }

    private void spawnParticles() {
        for (int i = 1; i < 10; i++) {
            double yPos = random.nextDouble() * 8;
            int blockX = (int) this.getX() - 2 + this.random.nextInt(4);
            int blockZ = (int) this.getZ() - 2 + this.random.nextInt(4);

            BlockPos pos1 = new BlockPos(blockX, (int) (this.getY() + 3), blockZ);

            Integer blockY = BlockUtil.getNearestSurface(level(), pos1, Direction.UP, 5, true, BlockUtil.SurfaceCriteria.NOT_AIR_TO_AIR);

            if (blockY != null) {
                blockY--;
                pos1 = new BlockPos(pos1.getX(), blockY, pos1.getZ());
                BlockState block = this.level().getBlockState(pos1);

                // Seems better to have 2 tornado particles per block to make it look more continuous, especially for larger radius
                ParticleTornado.spawnTornadoParticle(level(), this.getX(), this.getY() + yPos, this.getZ(), this.getVelX(), this.getVelZ(),
                        yPos / 3 + 0.5d, 100, block, pos1);
                ParticleTornado.spawnTornadoParticle(level(), this.getX(), this.getY() + yPos, this.getZ(), this.getVelX(), this.getVelZ(),
                        yPos / 3 + 0.5d, 100, block, pos1);

                if (random.nextInt(3) == 0) {
                    spawnAdditionalParticles(block);
                }
            }
        }
    }

    private void spawnAdditionalParticles(BlockState block) {
        DeferredObject<SimpleParticleType> type = null;
        if (block.is(BlockTags.LEAVES)) type = EBParticles.LEAF;
        if (block.is(BlockTags.SNOW)) type = EBParticles.SNOW;

        if (type != null) {
            double yPos = random.nextDouble() * 8;
            ParticleBuilder.create(type)
                    .pos(this.getX() + (random.nextDouble() * 2 - 1) * (yPos / 3 + 0.5d), this.getY() + yPos,
                            this.getZ() + (random.nextDouble() * 2 - 1) * (yPos / 3 + 0.5d))
                    .time(20 + random.nextInt(10))
                    .spawn(level());
        }
    }
}
