package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.MagicProjectileEntity;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.Spells;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class MagicFireballEntity extends MagicProjectileEntity {
    public MagicFireballEntity(EntityType<MagicFireballEntity> entityMagicMissileEntityType, Level world) {
        super(entityMagicMissileEntityType, world);
    }

    public MagicFireballEntity(Level world) {
        super(EBEntities.MAGIC_FIREBALL.get(), world);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        if (level().isClientSide) return;

        Entity entity = result.getEntity();
        MagicDamageSource.causeMagicDamage(this, entity, Spells.FIREBALL.property(DefaultProperties.DAMAGE), EBDamageSources.FIRE);
        if (!MagicDamageSource.isEntityImmune(EBDamageSources.FIRE, entity))
            entity.setSecondsOnFire(Spells.FIREBALL.property(DefaultProperties.DAMAGE).intValue());
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

        if (this.tickCount >= 60) {
            this.discard();
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

    @Override
    protected @NotNull Item getDefaultItem() {
        return ItemStack.EMPTY.getItem();
    }
}
