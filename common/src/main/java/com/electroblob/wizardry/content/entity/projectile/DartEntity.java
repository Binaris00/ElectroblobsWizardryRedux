package com.electroblob.wizardry.content.entity.projectile;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.entity.projectile.MagicArrowEntity;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.Spells;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;


public class DartEntity extends MagicArrowEntity {
    public DartEntity(EntityType<DartEntity> entityDartEntityType, Level world) {
        super(entityDartEntityType, world);
    }

    public DartEntity(Level world) {
        super(EBEntities.DART.get(), world);
    }

    @Override
    public double getDamage() {
        return Spells.DART.property(DefaultProperties.DAMAGE);
    }

    @Override
    public int getLifetime() {
        return -1;
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/dart.png");
    }

    @Override
    public boolean doDeceleration() {
        return true;
    }


    @Override
    protected void onHitEntity(@NotNull EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        if (entity instanceof LivingEntity livingEntity && !level().isClientSide) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, Spells.DART.property(DefaultProperties.EFFECT_DURATION),
                    Spells.DART.property(DefaultProperties.EFFECT_STRENGTH), false, false));
        }

        super.onHitEntity(hitResult);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        this.playSound(EBSounds.ENTITY_DART_HIT_BLOCK.get(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        super.onHitBlock(blockHitResult);
    }

    @Override
    public void ticksInAir() {
        if (this.level().isClientSide()) {
            ParticleBuilder.create(EBParticles.LEAF, this).time(10 + random.nextInt(5)).spawn(level());
        }
    }

    @Override
    public void tickInGround() {
        if (this.ticksInGround > 60) {
            this.discard();
        }
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }


}
