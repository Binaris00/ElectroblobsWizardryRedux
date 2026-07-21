package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.BombEntity;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SparkBombEntity extends BombEntity {
    public SparkBombEntity(EntityType<? extends ThrowableProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public SparkBombEntity(Level world) {
        super(EBEntities.SPARK_BOMB.get(), world);
    }

    public SparkBombEntity(LivingEntity livingEntity, Level world) {
        super(EBEntities.SPARK_BOMB.get(), livingEntity, world);
    }

    protected void splashEffect() {
        double range = Spells.SPARK_BOMB.property(DefaultProperties.EFFECT_RADIUS);
        List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(range, getX(), getY(), getZ(), level());

        for(int i = 0; i < Math.min(targets.size(), Spells.SPARK_BOMB.property(DefaultProperties.MAX_TARGETS)); i++){
            if (!this.isValidTarget(targets.get(i))) continue;

            LivingEntity target = targets.get(i);

            target.playSound(EBSounds.ENTITY_SPARK_BOMB_CHAIN.get(), 1.0F, random.nextFloat() * 0.4F + 1.5F);
            MagicDamageSource.causeMagicDamage(this, target, Spells.SPARK_BOMB.property(DefaultProperties.SPLASH_DAMAGE), EBDamageSources.SHOCK);
            ParticleBuilder.create(EBParticles.LIGHTNING)
                    .pos(this.position()).target(target).time(1)
                    .allowServer(true).spawn(level());
        }
    }

    @Override
    public int getLifeTime() {
        return -1;
    }

    @Override
    public void onHitTargetExtraEffects(@NotNull EntityHitResult hitResult) {
        splashEffect();
    }

    @Override
    public void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);
        splashEffect();
    }

    @Override
    public float getDamage(@NotNull EntityHitResult hitResult) {
        return Spells.SPARK_BOMB.property(DefaultProperties.DIRECT_DAMAGE);
    }

    @Override
    public ResourceKey<DamageType> getDamageType(@NotNull EntityHitResult hitResult) {
        return EBDamageSources.SHOCK;
    }

    @Override
    public @NotNull SoundEvent getSoundEvent(HitResult result) {
        return result instanceof BlockHitResult ? EBSounds.ENTITY_SPARK_BOMB_HIT_BLOCK.get() : SoundEvents.EMPTY;
    }

    @Override
    protected void spawnHitParticles(HitResult.Type type) {
        ParticleBuilder.spawnShockParticles(this.level(), this.getX(), this.getY() + this.getBbHeight(), this.getZ());
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
