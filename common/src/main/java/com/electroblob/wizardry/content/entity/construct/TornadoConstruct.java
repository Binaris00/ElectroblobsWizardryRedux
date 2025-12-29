package com.electroblob.wizardry.content.entity.construct;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.entity.construct.ScaledConstructEntity;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.api.content.util.MagicDamageSource;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.client.particle.ParticleTornado;
import com.electroblob.wizardry.core.ClientSpellSoundManager;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
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
    private double velX, velZ;

    public TornadoConstruct(EntityType<?> type, Level world) {
        super(type, world);
    }

    public TornadoConstruct(Level world) {
        super(EBEntities.TORNADO.get(), world);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return EntityDimensions.scalable(4, 8);
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    public void setHorizontalVelocity(double velX, double velZ) {
        this.velX = velX;
        this.velZ = velZ;
    }

    @Override
    public void tick() {
        super.tick();
        double radius = getBbWidth() / 2;

        if (this.tickCount % 120 == 1 && level().isClientSide) {
            ClientSpellSoundManager.playMovingSound(this, EBSounds.ENTITY_TORNADO_AMBIENT.get(), SoundSource.HOSTILE, 1.0f, 1.0f, false);
        }

        this.move(MoverType.SELF, new Vec3(velX, this.getDeltaMovement().y, velZ));
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

        float damage = 1 * damageMultiplier;
        MagicDamageSource.causeMagicDamage(this, target, damage, EBDamageSources.SORCERY);
        target.setDeltaMovement(dx, velY + 0.2, dz);

        if (target instanceof ServerPlayer sp)
            sp.connection.send(new ClientboundSetEntityMotionPacket(target));
    }

    private void spawnParticles() {
        for (int i = 1; i < 10; i++) {
            double yPos = random.nextDouble() * 8;
            BlockPos pos = BlockPos.containing(this.getX() - 2 + random.nextInt(4), (int) (this.getY() + 3), this.getZ() - 2 + random.nextInt(4));
            Integer blockY = BlockUtil.getNearestSurface(level(), pos, Direction.UP, 5, true, BlockUtil.SurfaceCriteria.NOT_AIR_TO_AIR);

            if (blockY != null) {
                BlockState block = this.level().getBlockState(pos.above(blockY - pos.getY()));
                ParticleTornado.spawnTornadoParticle(level(), this.getX(), this.getY() + yPos, this.getZ(), velX, velZ, yPos / 3 + 0.5d, 100, block, pos);

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
                    .time(40 + random.nextInt(10))
                    .spawn(level());
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        velX = tag.getDouble("velX");
        velZ = tag.getDouble("velZ");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putDouble("velX", velX);
        tag.putDouble("velZ", velZ);
    }
}
