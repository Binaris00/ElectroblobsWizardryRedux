package com.electroblob.wizardry.common.content.entity.construct;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.DeferredObject;
import com.electroblob.wizardry.api.common.util.BlockUtil;
import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.api.common.util.EBMagicDamageSource;
import com.electroblob.wizardry.client.particle.ParticleTornado;
import com.electroblob.wizardry.common.content.entity.abstr.ScaledConstructEntity;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityTornado extends ScaledConstructEntity {
    private double velX, velZ;

    public EntityTornado(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public EntityTornado(Level world) {
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
            // TODO ENTITY MOVING SOUND
            //Wizardry.proxy.playMovingSound(this, WizardrySounds.ENTITY_TORNADO_AMBIENT.get(), SoundSource.PLAYERS, 1.0f, 1.0f, false);
        }

        this.move(MoverType.SELF, new Vec3(velX, this.getDeltaMovement().y, velZ));

        BlockPos pos = this.blockPosition();
        Integer y = BlockUtil.getNearestSurface(level(), pos.above(3), Direction.UP, 5, true,
                BlockUtil.SurfaceCriteria.NOT_AIR_TO_AIR);

        if (y != null) {
            pos = new BlockPos(pos.getX(), y, pos.getZ());

            // TODO: Maybe this is not the best way to detect lava
            if (this.level().getBlockState(pos).is(Blocks.LAVA)) {
                this.setSecondsOnFire(5);
            }
        }

        if (!this.level().isClientSide) {
            List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(radius, this.getX(), this.getY(), this.getZ(), this.level());

            for (LivingEntity target : targets) {
                // TODO !Wizardry.settings.playersMoveEachOther
                if (target instanceof Player && ((getCaster() instanceof Player))) {
                    continue;
                }

                if (this.isValidTarget(target)) {
                    double velY = target.getDeltaMovement().y;

                    double dx = (this.getX() - target.getX() > 0 ? 0.5 : -0.5) - (this.getX() - target.getX()) * 0.125;
                    double dz = (this.getZ() - target.getZ() > 0 ? 0.5 : -0.5) - (this.getZ() - target.getZ()) * 0.125;

                    if (this.isOnFire()) {
                        target.setSecondsOnFire(4);
                    }

                    float damage = 1 * damageMultiplier;
                    EBMagicDamageSource.causeMagicDamage(this, target, damage, EBDamageSources.SORCERY, false);

                    target.setDeltaMovement(dx, velY + 0.2, dz);

                    if (target instanceof ServerPlayer) {
                        ((ServerPlayer) target).connection.send(new ClientboundSetEntityMotionPacket(target));
                    }
                }
            }
        } else {
            for (int i = 1; i < 10; i++) {
                double yPos = random.nextDouble() * 8;

                int blockX = (int) this.getX() - 2 + this.random.nextInt(4);
                int blockZ = (int) this.getZ() - 2 + this.random.nextInt(4);

                BlockPos pos1 = new BlockPos(blockX, (int) (this.getY() + 3), blockZ);

                Integer blockY = BlockUtil.getNearestSurface(level(), pos1, Direction.UP, 5, true,
                        BlockUtil.SurfaceCriteria.NOT_AIR_TO_AIR);

                if (blockY != null) {
                    blockY--;

                    pos1 = new BlockPos(pos1.getX(), blockY, pos1.getZ());

                    BlockState block = this.level().getBlockState(pos1);

//                    if (!canTornadoPickUpBitsOf(block)) {
//                        //TODO
//                        //block = level.getBiome(pos1).get().topBlock;
//                    }


                    ParticleTornado.spawnTornadoParticle(level(), this.getX(), this.getY() + yPos, this.getZ(), this.velX, this.velZ,
                            yPos / 3 + 0.5d, 100, block, pos1);
                    ParticleTornado.spawnTornadoParticle(level(), this.getX(), this.getY() + yPos, this.getZ(), this.velX, this.velZ,
                            yPos / 3 + 0.5d, 100, block, pos1);

                    if (this.random.nextInt(3) == 0) {
                        DeferredObject<SimpleParticleType> type = null;
                        if(block.is(BlockTags.LEAVES)) type = EBParticles.LEAF;
                        if(block.is(BlockTags.SNOW)) type = EBParticles.SNOW;

                        if (type != null) {
                            double yPos1 = random.nextDouble() * 8;
                            ParticleBuilder.create(type)
                                    .pos(this.getX() + (random.nextDouble() * 2 - 1) * (yPos1 / 3 + 0.5d), this.getY() + yPos1,
                                            this.getZ() + (random.nextDouble() * 2 - 1) * (yPos1 / 3 + 0.5d))
                                    .time(40 + random.nextInt(10))
                                    .spawn(level());
                        }
                    }
                }
            }
        }
    }

//    private static boolean canTornadoPickUpBitsOf(BlockState block) {
//        Material material = block.getMaterial();
//        return material == Material.TOP_SNOW || material == Material.DIRT || material == Material.GRASS
//                || material == Material.LAVA || material == Material.SAND || material == Material.SNOW
//                || material == Material.WATER || material == Material.PLANT || material == Material.LEAVES
//                ;//|| material == Material.VINE;
//    }

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
