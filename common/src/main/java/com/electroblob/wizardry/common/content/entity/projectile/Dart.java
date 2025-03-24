package com.electroblob.wizardry.common.content.entity.projectile;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.entity.projectile.MagicArrowEntity;
import com.electroblob.wizardry.setup.registries.EBEntities;
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


public class Dart extends MagicArrowEntity {
    public Dart(Level world) {
        super(EBEntities.DART.get(), world);
    }

    public Dart(EntityType<Dart> entityDartEntityType, Level world) {
        super(entityDartEntityType, world);
    }

    @Override
    public double getDamage() {
        return 4;
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
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200,
                    1, false, false));
        }

        super.onHitEntity(entityHitResult);
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
    }

    @Override
    public void ticksInAir() {
        if(this.level().isClientSide()){
            ParticleBuilder.create(EBParticles.LEAF, this).time(10 + random.nextInt(5)).spawn(level());
        }
    }

    @Override
    public void tickInGround() {
        if(this.ticksInGround > 60){
            this.discard();
        }
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }
}
