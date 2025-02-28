package com.electroblob.wizardry.common.content.entity.projectile;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.entity.projectile.MagicArrowEntity;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import com.electroblob.wizardry.setup.registries.client.EBSounds;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class IceShard extends MagicArrowEntity {
    public IceShard(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    public IceShard(Level world) {
        //super(EBEntities.ICE_SHARD.get(), world);
        super(null, world);
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public double getDamage() {
        return 6;
    }

    @Override
    public int getLifetime() {
        return -1;
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation("ebwizardry", "textures/entity/ice_shard.png");
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                    200,
                    0, false, false));
        }

        //this.playSound(EBSounds.ENTITY_ICE_SHARD_HIT.get(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        super.onHitEntity(entityHitResult);
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
//        if (this.level().isClientSide()) {
//            Vec3 pos = blockHitResult.getLocation();
//            ParticleBuilder.create(EBParticles.FLASH).pos(pos).color(0.75f, 1.0f, 1.0f).spawn(level());
//
//            for (int i = 0; i < 8; i++) {
//                ParticleBuilder.create(EBParticles.ICE, new Random(), this.getX(), this.getY(), this.getZ(), 0.5, true)
//                        .time(20 + this.random.nextInt(10)).gravity(true).spawn(this.level());
//            }
//        }
//
//        this.playSound(EBSounds.ENTITY_ICE_SHARD_SMASH.get(), 1.0F, random.nextFloat() * 0.4F + 1.2F);
        super.onHitBlock(blockHitResult);
    }

    @Override
    public void tickInGround() {
        if (this.ticksInGround > 40) {
            this.discard();
        }
    }
}

