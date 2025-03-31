package com.electroblob.wizardry.common.content.entity.projectile;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.entity.projectile.MagicProjectileEntity;
import com.electroblob.wizardry.api.common.util.EBMagicDamageSource;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.Spells;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class DarknessOrb extends MagicProjectileEntity {
    public DarknessOrb(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public DarknessOrb(Level world) {
        super(EBEntities.DARKNESS_ORB.get(), world);
    }

    @Override
    protected void onHit(@NotNull HitResult rayTrace) {
        super.onHit(rayTrace);
        Entity target = rayTrace.getType() == HitResult.Type.ENTITY ? ((EntityHitResult) rayTrace).getEntity() : null;

        if (target != null && !EBMagicDamageSource.isEntityImmune(EBDamageSources.WITHER, target)) {
            float damage = Spells.DARKNESS_ORB.property(DefaultProperties.DAMAGE) * damageMultiplier;
            EBMagicDamageSource.causeMagicDamage(this, target, damage, EBDamageSources.WITHER, false);

            if (target instanceof LivingEntity livingEntity)
                livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER,
                        Spells.DARKNESS_ORB.property(DefaultProperties.EFFECT_DURATION),
                        Spells.DARKNESS_ORB.property(DefaultProperties.EFFECT_STRENGTH)));

            this.playSound(EBSounds.ENTITY_DARKNESS_ORB_HIT.get(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        }

        this.discard();
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            float brightness = random.nextFloat() * 0.2f;

            ParticleBuilder.create(EBParticles.SPARKLE, this).time(20 + random.nextInt(10))
                    .color(brightness, 0.0f, brightness).spawn(level());

            ParticleBuilder.create(EBParticles.DARK_MAGIC, this).color(0.1f, 0.0f, 0.0f).spawn(level());
        }

        this.setDeltaMovement(this.getDeltaMovement().x / 0.99, this.getDeltaMovement().y / 0.99, this.getDeltaMovement().z / 0.99);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public int getLifeTime() {
        return 60;
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ItemStack.EMPTY.getItem();
    }
}
