package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.MagicProjectileEntity;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.Spells;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class DarknessOrbEntity extends MagicProjectileEntity {
    public DarknessOrbEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public DarknessOrbEntity(Level world) {
        super(EBEntities.DARKNESS_ORB.get(), world);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        if (level().isClientSide) return;

        if (!(result.getEntity() instanceof LivingEntity livingEntity)
                || MagicDamageSource.isEntityImmune(EBDamageSources.WITHER, livingEntity)) return;

        float damage = Spells.DARKNESS_ORB.property(DefaultProperties.DAMAGE) * damageMultiplier;
        MagicDamageSource.causeMagicDamage(this, livingEntity, damage, EBDamageSources.WITHER);

        livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER,
                Spells.DARKNESS_ORB.property(DefaultProperties.EFFECT_DURATION),
                Spells.DARKNESS_ORB.property(DefaultProperties.EFFECT_STRENGTH)));

        this.playSound(EBSounds.ENTITY_DARKNESS_ORB_HIT.get(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        this.discard();
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);
        this.discard();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            float brightness = random.nextFloat() * 0.2f;
            ParticleBuilder.create(EBParticles.SPARKLE, this).time(20 + random.nextInt(10)).color(brightness, 0.0f, brightness).spawn(level());
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
