package com.electroblob.wizardry.common.content.entity.projectile;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.entity.projectile.MagicProjectileEntity;
import com.electroblob.wizardry.setup.registries.EBEntities;
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

        // TODO BIN !MagicDamage.isEntityImmune(this.damageSources().wither(), target) here
        if (target != null) {
            float damage = 8 * damageMultiplier;

            target.hurt(this.damageSources().wither(), damage);

            // TODO BIN !MagicDamage.isEntityImmune(this.damageSources().wither(), target) here
            if (target instanceof LivingEntity)
                ((LivingEntity) target).addEffect(
                        new MobEffectInstance(MobEffects.WITHER, 150, 1));

            // TODO BIN  ENTITY HIT
            //this.playSound(WizardrySounds.ENTITY_DARKNESS_ORB_HIT.get(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
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
