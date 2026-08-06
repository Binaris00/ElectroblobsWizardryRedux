package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.MagicProjectileEntity;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.Spells;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class MagicFireballEntity extends MagicProjectileEntity {
    public MagicFireballEntity(EntityType<MagicFireballEntity> entityType, Level world) {
        super(entityType, world);
    }

    public MagicFireballEntity(Level world) {
        super(EBEntities.MAGIC_FIREBALL.get(), world);
    }

    @Override
    public int getLifeTime() {
        return 10;
    }

    @Override
    public float getDamage(@NotNull EntityHitResult result) {
        return Spells.FIREBALL.property(DefaultProperties.DAMAGE);
    }

    @Override
    public ResourceKey<DamageType> getDamageType(@NotNull EntityHitResult result) {
        return EBDamageSources.FIRE;
    }

    @Override
    public void onHitTargetExtraEffects(@NotNull EntityHitResult result) {
        result.getEntity().setSecondsOnFire(Spells.FIREBALL.property(DefaultProperties.EFFECT_DURATION));
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            BlockPos pos = result.getBlockPos().relative(result.getDirection());
            this.level().setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) return;
        for (int i = 0; i < 5; i++) {
            double dx = (random.nextDouble() - 0.5) * this.getBbWidth();
            double dy = (random.nextDouble() - 0.5) * this.getBbHeight() + this.getBbHeight() / 2;
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

    @Override
    public boolean canCollideWith(@NotNull Entity entity) {
        return true;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }
}