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
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class FireBoltEntity extends MagicProjectileEntity {
    public FireBoltEntity(Level world) {
        super(EBEntities.FIRE_BOLT.get(), world);
    }

    public FireBoltEntity(EntityType<? extends ThrowableProjectile> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide || this.tickCount <= 1) return;
        ParticleBuilder.create(EBParticles.MAGIC_FIRE, this).time(14).spawn(level());

        if (this.tickCount <= 1) return;
        double x = xo - getDeltaMovement().x / 2 + random.nextFloat() * 0.2 - 0.1;
        double y = yo + getBbHeight() / 2 - getDeltaMovement().y / 2 + random.nextFloat() * 0.2 - 0.1;
        double z = zo - getDeltaMovement().z / 2 + random.nextFloat() * 0.2 - 0.1;
        ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).time(14).spawn(level());
    }

    @Override
    protected void spawnHitParticles(HitResult.Type type) {
        for (int i = 0; i < 8; i++) {
            level().addParticle(ParticleTypes.LAVA, getX() + random.nextFloat() - 0.5, getY() + getBbHeight() / 2 + random.nextFloat() - 0.5, getZ() + random.nextFloat() - 0.5, 0, 0, 0);
        }
    }

    @Override
    public void onHitTargetExtraEffects(@NotNull EntityHitResult hitResult) {
        if (!(hitResult.getEntity() instanceof LivingEntity entity)) return;
        entity.setSecondsOnFire(Spells.FIREBOLT.property(DefaultProperties.EFFECT_DURATION));
    }

    @Override
    public float getDamage(@NotNull EntityHitResult hitResult) {
        return Spells.FIREBOLT.property(DefaultProperties.DAMAGE);
    }

    @Override
    public ResourceKey<DamageType> getDamageType(@NotNull EntityHitResult hitResult) {
        return EBDamageSources.FIRE;
    }

    @Override
    public @NotNull SoundEvent getSoundEvent(HitResult result) {
        return EBSounds.ENTITY_FIREBOLT_HIT.get();
    }

    @Override
    public int getLifeTime() {
        return 6;
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
