package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.MagicProjectileEntity;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.Spells;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class ThunderboltEntity extends MagicProjectileEntity {
    public ThunderboltEntity(Level world) {
        super(EBEntities.THUNDERBOLT.get(), world);
    }

    public ThunderboltEntity(EntityType<ThunderboltEntity> entityThunderboltEntityType, Level world) {
        super(entityThunderboltEntityType, world);
    }

    @Override
    public int getLifeTime() {
        return 8;
    }

    @Override
    public void onHitTargetExtraEffects(@NotNull EntityHitResult hitResult) {
        if (!(hitResult.getEntity() instanceof LivingEntity livingEntity)) return;
        livingEntity.knockback(Spells.THUNDERBOLT.property(DefaultProperties.KNOCKBACK) * 0.5F,
                Mth.sin(this.getYRot() * 0.017453292F), -Mth.cos(this.getYRot() * 0.017453292F));
    }

    @Override
    public float getDamage(@NotNull EntityHitResult hitResult) {
        return Spells.THUNDERBOLT.property(DefaultProperties.DAMAGE);
    }

    @Override
    public ResourceKey<DamageType> getDamageType(@NotNull EntityHitResult hitResult) {
        return EBDamageSources.SHOCK;
    }

    @Override
    public @NotNull SoundEvent getSoundEvent(HitResult result) {
        return EBSounds.ENTITY_THUNDERBOLT_HIT.get();
    }

    @Override
    protected void spawnHitParticles(HitResult.Type type) {
        this.level().addParticle(ParticleTypes.EXPLOSION, this.xo, this.yo, this.zo, 0, 0, 0);
    }

    @Override
    public void tick() {
        super.tick();

        // Particle when moving
        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 5);
        }
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 5) {
            ParticleBuilder.create(EBParticles.SPARK, level().getRandom(), this.xo, this.yo + this.getBbHeight() / 2, this.zo, 0.1, false).spawn(this.level());
            for (int i = 0; i < 4; i++) {
                this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.xo + random.nextFloat() * 0.2 - 0.1,
                        this.yo + this.getBbHeight() / 2 + random.nextFloat() * 0.2 - 0.1,
                        this.zo + random.nextFloat() * 0.2 - 0.1, 0, 0, 0);
            }
        }
        super.handleEntityEvent(status);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }
}
