package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.MagicItemProjectileEntity;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class IceBall extends MagicItemProjectileEntity {
    public IceBall(EntityType<? extends ThrowableProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public IceBall(Level world) {
        super(EBEntities.ICE_BALL.get(), world);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.SNOWBALL;
    }

    @Override
    public int getLifeTime() {
        return 16;
    }

    @Override
    public float getDamage(@NotNull EntityHitResult hitResult) {
        return Spells.ICE_BALL.property(DefaultProperties.DAMAGE);
    }

    @Override
    public void onHitTargetExtraEffects(@NotNull EntityHitResult hitResult) {
        if (hitResult.getEntity() instanceof LivingEntity livingEntity)
            livingEntity.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(),
                    Spells.ICE_BALL.property(DefaultProperties.EFFECT_DURATION),
                    Spells.ICE_BALL.property(DefaultProperties.EFFECT_STRENGTH)));
    }

    @Override
    public ResourceKey<DamageType> getDamageType(@NotNull EntityHitResult hitResult) {
        return EBDamageSources.FROST;
    }

    @Override
    public @NotNull SoundEvent getSoundEvent(HitResult result) {
        return EBSounds.ENTITY_ICEBALL_HIT.get();
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        BlockPos pos = blockHitResult.getBlockPos();

        if (blockHitResult.getDirection() == Direction.UP && !level().isClientSide() && level().getBlockState(pos).isFaceSturdy(level(), pos, Direction.UP)
                && BlockUtil.canBlockBeReplaced(level(), pos.above()) && BlockUtil.canPlaceBlock((LivingEntity) getOwner(), level(), pos)) {
            level().setBlock(pos.above(), Blocks.SNOW.defaultBlockState(), 3);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide()) return;
        for (int i = 0; i < 5; i++) {
            ParticleBuilder.create(EBParticles.SNOW, level().getRandom(), xo, yo, zo, 0.4, false).scale(2)
                    .time(8 + random.nextInt(4)).spawn(level());
        }
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
