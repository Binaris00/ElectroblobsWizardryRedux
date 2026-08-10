package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.BombEntity;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
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

public class FireBombEntity extends BombEntity {
    public FireBombEntity(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public FireBombEntity(LivingEntity livingEntity, Level world) {
        super(EBEntities.FIRE_BOMB.get(), livingEntity, world);
    }

    public FireBombEntity(Level world) {
        super(EBEntities.FIRE_BOMB.get(), world);
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
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);
        splashEffect();
    }

    @Override
    public float getDamage(@NotNull EntityHitResult hitResult) {
        return Spells.FIREBOMB.property(DefaultProperties.DAMAGE);
    }

    @Override
    public ResourceKey<DamageType> getDamageType(@NotNull EntityHitResult hitResult) {
        return EBDamageSources.FIRE;
    }

    public void splashEffect() {
        List<LivingEntity> livingEntities = EntityUtil.getLivingEntitiesInRange(level(), getX(), getY(), getZ(), Spells.FIREBOMB.property(DefaultProperties.EFFECT_RADIUS));

        for (LivingEntity entity : livingEntities) {
            if (!isValidTarget(entity)) continue;
            MagicDamageSource.causeMagicDamage(this, entity, Spells.FIREBOMB.property(DefaultProperties.SPLASH_DAMAGE) * blastMultiplier, EBDamageSources.FIRE);
            entity.setSecondsOnFire(Spells.FIREBOMB.property(DefaultProperties.EFFECT_DURATION));
        }
    }

    @Override
    protected void playHitSound(HitResult result) {
        this.playSound(EBSounds.ENTITY_FIREBOMB_SMASH.get(), 1.5F, random.nextFloat() * 0.4F + 0.6F);
        this.playSound(EBSounds.ENTITY_FIREBOMB_FIRE.get(), 1, 1);
    }

    @Override
    protected void spawnHitParticles(HitResult.Type type) {
        ParticleBuilder.create(EBParticles.FLASH).pos(this.position()).scale(5 * blastMultiplier).color(1, 0.6f, 0).spawn(level());

        for (int i = 0; i < 60 * blastMultiplier; i++) {
            ParticleBuilder.create(EBParticles.MAGIC_FIRE, level().getRandom(), xo, yo, zo, 2 * blastMultiplier, false)
                    .time(10 + random.nextInt(4)).scale(1 + random.nextFloat()).spawn(level());

            ParticleBuilder.create(EBParticles.DARK_MAGIC, level().getRandom(), xo, yo, zo, 2 * blastMultiplier, false)
                    .color(1.0f, 0.2f + random.nextFloat() * 0.4f, 0.0f).spawn(level());
        }
        level().addParticle(ParticleTypes.EXPLOSION, xo, yo, zo, 0, 0, 0);
    }

    @Override
    public int getRemainingFireTicks() {
        return -1;
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return EBItems.FIREBOMB.get();
    }
}
