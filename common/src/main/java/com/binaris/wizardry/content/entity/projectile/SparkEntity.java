package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.MagicProjectileEntity;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.Spells;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class SparkEntity extends MagicProjectileEntity {
    public SparkEntity(EntityType<? extends ThrowableProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public SparkEntity(Level world) {
        super(EBEntities.SPARK.get(), world);
    }

    @Override
    protected void spawnHitParticles(HitResult.Type type) {
        for (int i = 0; i < 8; i++) {
            double x = this.xo + random.nextDouble() - 0.5;
            double y = this.yo + this.getBbHeight() / 2 + random.nextDouble() - 0.5;
            double z = this.zo + random.nextDouble() - 0.5;
            ParticleBuilder.create(EBParticles.SPARK).pos(x, y, z).spawn(this.level());
        }
    }

    @Override
    public int getLifeTime() {
        return 50;
    }

    @Override
    public float getDamage(@NotNull EntityHitResult hitResult) {
        return Spells.HOMING_SPARK.property(DefaultProperties.DAMAGE);
    }

    @Override
    public ResourceKey<DamageType> getDamageType(@NotNull EntityHitResult hitResult) {
        return EBDamageSources.SHOCK;
    }

    @Override
    public @NotNull SoundEvent getSoundEvent(HitResult result) {
        return EBSounds.ENTITY_HOMING_SPARK_HIT.get();
    }

    @Override
    public float getSeekingStrength() {
        return Spells.HOMING_SPARK.property(DefaultProperties.SEEKING_STRENGTH);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }
}
