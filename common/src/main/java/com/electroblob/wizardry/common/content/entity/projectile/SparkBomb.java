package com.electroblob.wizardry.common.content.entity.projectile;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.entity.projectile.BombEntity;
import com.electroblob.wizardry.api.common.util.EBMagicDamageSource;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.EBSounds;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class SparkBomb extends BombEntity {

    public SparkBomb(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public SparkBomb(Level world) {
        super(EBEntities.SPARK_BOMB.get(), world);
    }

    public SparkBomb(LivingEntity livingEntity, Level world) {
        super(EBEntities.SPARK_BOMB.get(), livingEntity, world);
    }

    @Override
    public int getRemainingFireTicks() {
        return -1;
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return EBItems.SPARK_BOMB.get();
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        this.playSound(EBSounds.ENTITY_SPARK_BOMB_HIT_BLOCK.get(), 0.5f, 0.5f);
        super.onHitBlock(blockHitResult);

        LivingEntity closestEntity = this.level().getNearestEntity(
                LivingEntity.class,
                TargetingConditions.DEFAULT,
                (LivingEntity) this.getOwner(),
                this.getX(),
                this.getY(),
                this.getZ(),
                this.getBoundingBox().inflate(3)
        );

        if (closestEntity != null)
            EBMagicDamageSource.causeMagicDamage(this, closestEntity, 4, EBDamageSources.SHOCK, false);

        ParticleBuilder.spawnShockParticles(this.level(), this.getX(), this.getY() + this.getBbHeight(), this.getZ());
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        // Direct hit damage
        float damage = 6;
        EBMagicDamageSource.causeMagicDamage(this, entityHitResult.getEntity(), damage, EBDamageSources.SHOCK, false);
        this.playSound(EBSounds.ENTITY_SPARK_BOMB_HIT.get(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));

        ParticleBuilder.spawnShockParticles(this.level(), this.getX(), this.getY() + this.getBbHeight() / 2, this.getZ());

        super.onHitEntity(entityHitResult);
    }
}
