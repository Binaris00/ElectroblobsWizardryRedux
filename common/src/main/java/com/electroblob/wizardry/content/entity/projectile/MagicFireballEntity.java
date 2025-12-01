package com.electroblob.wizardry.content.entity.projectile;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.entity.projectile.MagicProjectileEntity;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.Spells;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class MagicFireballEntity extends MagicProjectileEntity {
    protected float damage = -1;
    protected int burnDuration = -1;
    protected int lifetime = 16;

    public MagicFireballEntity(EntityType<MagicFireballEntity> entityMagicMissileEntityType, Level world) {
        super(entityMagicMissileEntityType, world);
    }

    public MagicFireballEntity(Level world) {
        super(EBEntities.MAGIC_FIREBALL.get(), world);
    }

    public double getDamage() {
        return damage == -1 ? Spells.FIREBALL.property(DefaultProperties.DAMAGE) : damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public int getBurnDuration() {
        return burnDuration == -1 ? Spells.FIREBALL.property(DefaultProperties.DAMAGE).intValue() : burnDuration;
    }

    public void setBurnDuration(int burnDuration) {
        this.burnDuration = burnDuration;
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        if (level().isClientSide) return;

        Entity entity = result.getEntity();
        EBMagicDamageSource.causeMagicDamage(this, entity, (float) getDamage(), EBDamageSources.FIRE, false);
        if (!EBMagicDamageSource.isEntityImmune(EBDamageSources.FIRE, entity))
            entity.setSecondsOnFire(getBurnDuration());
        this.discard();
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);
        if (level().isClientSide) return;

        BlockPos pos = result.getBlockPos().relative(result.getDirection());
        this.level().setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
        this.discard();
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

        if (this.lifetime >= 140) {
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
    public boolean save(CompoundTag tag) {
        tag.putInt("lifetime", lifetime);
        return super.save(tag);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        lifetime = tag.getInt("lifetime");
        super.load(tag);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ItemStack.EMPTY.getItem();
    }
}
