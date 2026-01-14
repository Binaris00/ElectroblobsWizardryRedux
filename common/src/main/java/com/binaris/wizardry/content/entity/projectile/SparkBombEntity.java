package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.BombEntity;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class SparkBombEntity extends BombEntity {
    public SparkBombEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public SparkBombEntity(Level world) {
        super(EBEntities.SPARK_BOMB.get(), world);
    }

    public SparkBombEntity(LivingEntity livingEntity, Level world) {
        super(EBEntities.SPARK_BOMB.get(), livingEntity, world);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        this.playSound(EBSounds.ENTITY_SPARK_BOMB_HIT_BLOCK.get(), 0.5f, 0.5f);
        super.onHitBlock(blockHitResult);

        LivingEntity closestEntity = this.level().getNearestEntity(LivingEntity.class, TargetingConditions.DEFAULT, (LivingEntity) this.getOwner(),
                this.getX(), this.getY(), this.getZ(), this.getBoundingBox().inflate(3)
        );

        if (closestEntity != null)
            MagicDamageSource.causeMagicDamage(this, closestEntity, 4, EBDamageSources.SHOCK);

        ParticleBuilder.spawnShockParticles(this.level(), this.getX(), this.getY() + this.getBbHeight(), this.getZ());
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        float damage = 6;
        MagicDamageSource.causeMagicDamage(this, result.getEntity(), damage, EBDamageSources.SHOCK);
        this.playSound(EBSounds.ENTITY_SPARK_BOMB_HIT.get(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));

        ParticleBuilder.create(EBParticles.LIGHTNING).pos(this.position()).target(result.getEntity()).spawn(level());
        ParticleBuilder.spawnShockParticles(this.level(), this.getX(), this.getY() + this.getBbHeight() / 2, this.getZ());
        super.onHitEntity(result);
    }

    @Override
    public int getRemainingFireTicks() {
        return -1;
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return EBItems.SPARK_BOMB.get();
    }
}
