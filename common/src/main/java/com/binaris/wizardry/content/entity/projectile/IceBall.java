package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.MagicProjectileEntity;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;


public class IceBall extends MagicProjectileEntity {
    public IceBall(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
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
    protected void onHitEntity(@NotNull EntityHitResult entityHitResult) {
        if (!level().isClientSide()) {
            Entity entity = entityHitResult.getEntity();
            float damage = Spells.ICE_BALL.property(DefaultProperties.DAMAGE) * damageMultiplier;

            MagicDamageSource.causeMagicDamage(this, entity, damage, EBDamageSources.FROST);

            if (entity instanceof LivingEntity livingEntity)
                livingEntity.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(),
                        Spells.ICE_BALL.property(DefaultProperties.EFFECT_DURATION),
                        Spells.ICE_BALL.property(DefaultProperties.EFFECT_STRENGTH)));
        }

        this.playSound(EBSounds.ENTITY_ICEBALL_HIT.get(), 2.0F, 0.8F + random.nextFloat() * 0.3F);
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        BlockPos pos = blockHitResult.getBlockPos();

        if (blockHitResult.getDirection() == Direction.UP && !level().isClientSide() && level().getBlockState(pos).isFaceSturdy(level(), pos, Direction.UP)
                && BlockUtil.canBlockBeReplaced(level(), pos.above()) && BlockUtil.canPlaceBlock(getOwner(), level(), pos)) {
            level().setBlock(pos.above(), Blocks.SNOW.defaultBlockState(), 3);
        }

        this.playSound(EBSounds.ENTITY_ICEBALL_HIT.get(), 2.0F, 0.8F + random.nextFloat() * 0.3F);
        this.discard();

        super.onHitBlock(blockHitResult);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 3);
        }
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 3) {
            for (int i = 0; i < 5; i++) {
                ParticleBuilder.create(EBParticles.SNOW, level().getRandom(), xo, yo, zo, 0.4, false).scale(2)
                        .time(8 + random.nextInt(4)).spawn(level());
            }
        }
        super.handleEntityEvent(status);
    }

    @Override
    public int getLifeTime() {
        return 16;
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
