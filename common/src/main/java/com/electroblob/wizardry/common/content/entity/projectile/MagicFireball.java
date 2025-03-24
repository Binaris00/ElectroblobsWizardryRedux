package com.electroblob.wizardry.common.content.entity.projectile;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.entity.projectile.MagicProjectileEntity;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MagicFireball extends MagicProjectileEntity {

    protected static final int ACCELERATION_CONVERSION_FACTOR = 10;
    protected float damage = -1;
    protected int burnDuration = -1;
    protected int lifetime = 16;

    public MagicFireball(EntityType<MagicFireball> entityMagicMissileEntityType, Level world) {
        super(entityMagicMissileEntityType, world);
    }

    public MagicFireball(Level world) {
        super(EBEntities.MAGIC_FIREBALL.get(), world);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setBurnDuration(int burnDuration) {
        this.burnDuration = burnDuration;
    }

    public double getDamage() {
        return damage == -1 ? 5 : damage;
    }

    public int getBurnDuration() {
        return burnDuration == -1 ? 5 : burnDuration;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            if (hitResult instanceof EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                entity.hurt(entity.damageSources().indirectMagic(this, this.getOwner()), (float) getDamage());
                entity.setSecondsOnFire(getBurnDuration());
            } else if (hitResult instanceof BlockHitResult blockHitResult) {
                BlockPos pos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
                this.level().setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
            }
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            for (int i = 0; i < 5; i++) {
                double dx = (random.nextDouble() - 0.5) * this.getBbWidth();
                double dy = (random.nextDouble() - 0.5) * this.getBbHeight() + this.getBbHeight() / 2 - 0.1;
                double dz = (random.nextDouble() - 0.5) * this.getBbWidth();
                double v = 0.06;
                ParticleBuilder.create(EBParticles.MAGIC_FIRE)
                        .pos(this.position().add(dx - this.getDeltaMovement().x / 2, dy, dz - this.getDeltaMovement().z / 2))
                        .velocity(-v * dx, -v * dy, -v * dz)
                        .scale(this.getBbWidth() * 2)
                        .time(10)
                        .spawn(this.level());

                if (tickCount > 1) {
                    dx = (random.nextDouble() - 0.5) * this.getBbWidth();
                    dy = (random.nextDouble() - 0.5) * this.getBbHeight() + this.getBbHeight() / 2 - 0.1;
                    dz = (random.nextDouble() - 0.5) * this.getBbWidth();
                    ParticleBuilder.create(EBParticles.MAGIC_FIRE)
                            .pos(this.position().add(dx - this.getDeltaMovement().x, dy, dz - this.getDeltaMovement().z))
                            .velocity(-v * dx, -v * dy, -v * dz)
                            .scale(this.getBbWidth() * 2)
                            .time(10)
                            .spawn(this.level());
                }
            }
        }

        if(this.lifetime >= 140){
            this.discard();
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return true;
    }

    @Override
    public float getPickRadius() {
        return 1.0F;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            this.markHurt();
            if (source.getEntity() != null) {
                Vec3 vec3d = source.getEntity().position();
                double speed = Mth.sqrt((float) (getDeltaMovement().x * getDeltaMovement().x + getDeltaMovement().y * getDeltaMovement().y + getDeltaMovement().z * getDeltaMovement().z));
                this.setDeltaMovement(vec3d.scale(speed));
                this.lifetime = 160;
                if (source.getEntity() instanceof LivingEntity livingEntity) {
                    this.setOwner(livingEntity);
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public boolean save(CompoundTag nbt) {
        nbt.putInt("lifetime", lifetime);
        return super.save(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        lifetime = nbt.getInt("lifetime");
        super.load(nbt);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ItemStack.EMPTY.getItem();
    }
}
