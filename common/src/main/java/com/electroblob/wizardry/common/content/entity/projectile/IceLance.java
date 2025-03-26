package com.electroblob.wizardry.common.content.entity.projectile;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.entity.projectile.MagicArrowEntity;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class IceLance extends MagicArrowEntity {

    public IceLance(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    public IceLance(Level world){
        super(EBEntities.ICE_LANCE.get(), world);
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public double getDamage() {
        return 15;
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
    protected void onHitEntity(@NotNull EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            // TODO: Effect Frost
//            livingEntity.addEffect(new MobEffectInstance(WizardryEffects.FROST, Spells.ICE_LANCE.getIntProperty(Spell.EFFECT_DURATION),
//                    0));
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
    public ResourceKey<DamageType> getDamageType() {
        return EBDamageSources.FROST;
    }
}
