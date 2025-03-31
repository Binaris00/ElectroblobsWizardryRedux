package com.electroblob.wizardry.common.content.entity.projectile;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.entity.projectile.MagicProjectileEntity;
import com.electroblob.wizardry.api.common.util.BlockUtil;
import com.electroblob.wizardry.api.common.util.EBMagicDamageSource;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.Spells;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.SNOWBALL;
    }

    public IceBall(Level world) {
        super(EBEntities.ICE_BALL.get(), world);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult entityHitResult) {
        if (!level().isClientSide()) {
            Entity entity = entityHitResult.getEntity();
            float damage = Spells.ICE_BALL.property(DefaultProperties.DAMAGE) * damageMultiplier;

            EBMagicDamageSource.causeMagicDamage(this, entity, damage, EBDamageSources.FROST, false);

            if (entity instanceof LivingEntity livingEntity) {
                // TODO: Effect Frost
//                    livingEntity.addEffect(new MobEffectInstance(EBEffects.FROST,
//                            Spells.ICE_BALL.getIntProperty(Spell.EFFECT_DURATION),
//                            Spells.ICE_BALL.getIntProperty(Spell.EFFECT_STRENGTH)));
            }
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
