package com.electroblob.wizardry.content.entity.projectile;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.entity.projectile.MagicArrowEntity;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.*;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class IceShardEntity extends MagicArrowEntity {
    public IceShardEntity(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    public IceShardEntity(Level world) {
        super(EBEntities.ICE_SHARD.get(), world);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult hitResult) {
        if (hitResult.getEntity() instanceof LivingEntity livingEntity) {
            if(!EBMagicDamageSource.isEntityImmune(EBDamageSources.FROST, livingEntity))
                livingEntity.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(),
                        Spells.ICE_SHARD.property(DefaultProperties.EFFECT_DURATION),
                        Spells.ICE_SHARD.property(DefaultProperties.EFFECT_STRENGTH), false, false));
        }

        this.playSound(EBSounds.ENTITY_ICE_SHARD_HIT.get(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        super.onHitEntity(hitResult);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        if (this.level().isClientSide()) {
            Vec3 pos = blockHitResult.getLocation();
            ParticleBuilder.create(EBParticles.FLASH).pos(pos).color(0.75f, 1.0f, 1.0f).spawn(level());

            for (int i = 0; i < 8; i++) {
                ParticleBuilder.create(EBParticles.ICE, this.random, this.getX(), this.getY(), this.getZ(), 0.5, true)
                        .time(20 + this.random.nextInt(10)).gravity(true).spawn(this.level());
            }
        }

        this.playSound(EBSounds.ENTITY_ICE_SHARD_SMASH.get(), 1.0F, random.nextFloat() * 0.4F + 1.2F);
        super.onHitBlock(blockHitResult);
    }

    @Override
    public void tickInGround() {
        if (this.ticksInGround > 40) {
            this.discard();
        }
    }


    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public double getDamage() {
        return Spells.ICE_SHARD.property(DefaultProperties.DAMAGE);
    }

    @Override
    public int getLifetime() {
        return -1;
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/ice_shard.png");
    }

    @Override
    public ResourceKey<DamageType> getDamageType() {
        return EBDamageSources.FROST;
    }
}

