package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.MagicArrowEntity;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class IceLanceEntity extends MagicArrowEntity {

    public IceLanceEntity(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    public IceLanceEntity(Level world) {
        super(EBEntities.ICE_LANCE.get(), world);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        if (entity instanceof LivingEntity livingEntity && !level().isClientSide) {
            livingEntity.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(),
                    Spells.ICE_LANCE.property(DefaultProperties.EFFECT_DURATION),
                    0));
        }
        this.playSound(EBSounds.ENTITY_ICE_LANCE_HIT.get(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        super.onHitEntity(hitResult);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        if (this.level().isClientSide()) {
            for (int j = 0; j < 10; j++) {
                ParticleBuilder.create(EBParticles.ICE, level().getRandom(), this.getX(), this.getY(), this.getZ(), 0.5, true)
                        .time(20 + random.nextInt(10)).gravity(true).spawn(this.level());
            }
        }
        this.playSound(EBSounds.ENTITY_ICE_LANCE_SMASH.get(), 1.0F, random.nextFloat() * 0.4F + 1.2F);
        super.onHitBlock(blockHitResult);
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public double getDamage() {
        return Spells.ICE_LANCE.property(DefaultProperties.DAMAGE);
    }

    @Override
    public int getLifetime() {
        return -1;
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/ice_lance.png");
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public ResourceKey<DamageType> getDamageType() {
        return EBDamageSources.FROST;
    }
}
