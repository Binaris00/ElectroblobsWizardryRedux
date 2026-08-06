package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.BombEntity;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

public class SmokeBombEntity extends BombEntity {
    public SmokeBombEntity(EntityType<? extends ThrowableProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public SmokeBombEntity(LivingEntity livingEntity, Level world) {
        super(EBEntities.SMOKE_BOMB.get(), livingEntity, world);
    }

    public SmokeBombEntity(Level world) {
        super(EBEntities.SMOKE_BOMB.get(), world);
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
        return Spells.SMOKE_BOMB.property(DefaultProperties.EFFECT_STRENGTH);
    }

    @Override
    public ResourceKey<DamageType> getDamageType(@NotNull EntityHitResult hitResult) {
        return EBDamageSources.POISON;
    }

    @Override
    protected void playHitSound(HitResult result) {
        this.playSound(EBSounds.ENTITY_SMOKE_BOMB_SMASH.get(), 1.5F, random.nextFloat() * 0.4F + 0.6F);
        this.playSound(EBSounds.ENTITY_SMOKE_BOMB_SMOKE.get(), 1, 1);
    }

    protected void splashEffect() {
        List<LivingEntity> livingEntities = EntityUtil.getLivingEntitiesInRange(this.level(), this.getX(), this.getY(), this.getZ(), Spells.SMOKE_BOMB.property(DefaultProperties.EFFECT_RADIUS));

        for (LivingEntity entity : livingEntities) {
            if (this.isValidTarget(entity)) {
                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,
                        Spells.SMOKE_BOMB.property(DefaultProperties.EFFECT_DURATION),
                        Spells.SMOKE_BOMB.property(DefaultProperties.EFFECT_STRENGTH)));
            }
        }
    }

    @Override
    protected void spawnHitParticles(HitResult.Type type) {
        this.level().addParticle(ParticleTypes.EXPLOSION, this.xo, this.yo, this.zo, 0, 0, 0);
        for (int i = 0; i < 60 * blastMultiplier; i++) {
            float brightness = random.nextFloat() * 0.1f + 0.1f;
            ParticleBuilder.create(EBParticles.CLOUD, level().getRandom(), this.xo, this.yo, this.zo, 2 * blastMultiplier, false)
                    .color(brightness, brightness, brightness).time(80 + this.random.nextInt(12)).shaded(true).scale(5).spawn(this.level());

            brightness = random.nextFloat() * 0.3f;
            ParticleBuilder.create(EBParticles.DARK_MAGIC, level().getRandom(), this.xo, this.yo, this.zo, 2 * blastMultiplier, false)
                    .color(brightness, brightness, brightness).spawn(this.level());
        }
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return EBItems.SMOKE_BOMB.get();
    }

    @Override
    public int getRemainingFireTicks() {
        return -1;
    }
}

