package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.BombEntity;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IceChargeEntity extends BombEntity {
    public static final SpellProperty<Integer> ICE_SHARDS = SpellProperty.intProperty("ice_shards");

    public IceChargeEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public IceChargeEntity(Level world) {
        super(EBEntities.ICE_CHARGE.get(), world);
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        super.onHit(hitResult);
        Entity entity = hitResult.getType() == HitResult.Type.ENTITY ? ((EntityHitResult) hitResult).getEntity() : null;

        if (entity != null) {
            float damage = Spells.ICE_CHARGE.property(DefaultProperties.DAMAGE) * damageMultiplier;
            MagicDamageSource.causeMagicDamage(this, entity, damage, EBDamageSources.FROST);

            if (entity instanceof LivingEntity livingEntity && !level().isClientSide)
                livingEntity.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(),
                        Spells.ICE_CHARGE.property(DefaultProperties.EFFECT_DURATION),
                        Spells.ICE_CHARGE.property(DefaultProperties.EFFECT_STRENGTH)));
        }

        if (this.level().isClientSide()) {
            spawnParticles();
            return;
        }

        this.playSound(EBSounds.ENTITY_ICE_CHARGE_SMASH.get(), 1.5f, random.nextFloat() * 0.4f + 0.6f);
        this.playSound(EBSounds.ENTITY_ICE_CHARGE_ICE.get(), 1.2f, random.nextFloat() * 0.4f + 1.2f);

        double radius = 3 * blastMultiplier;
        List<LivingEntity> targets = EntityUtil.getLivingEntitiesInRange(level(), this.getX(), this.getY(), this.getZ(), radius);

        for (LivingEntity target : targets) {
            if (target != entity && target != this.getOwner() && !level().isClientSide) {
                target.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(),
                        Spells.ICE_CHARGE.property(DefaultProperties.EFFECT_DURATION),
                        Spells.ICE_CHARGE.property(DefaultProperties.EFFECT_STRENGTH)));
            }
        }

        freezeNearbyBlocks();
        for (int i = 0; i < Spells.ICE_CHARGE.property(ICE_SHARDS); i++) {
            double dx = random.nextDouble() - 0.5;
            double dy = random.nextDouble() - 0.5;
            double dz = random.nextDouble() - 0.5;
            IceShardEntity iceShard = new IceShardEntity(level());
            iceShard.setPos(this.getX() + dx, this.getY() + dy, this.getZ() + dz);
            iceShard.setDeltaMovement(dx * 1.5, dy * 1.5, dz * 1.5);
            level().addFreshEntity(iceShard);
        }

        this.discard();
    }

    private void freezeNearbyBlocks() {
        Level level = this.level();
        BlockPos basePos = BlockPos.containing(position());

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                BlockPos pos = basePos.offset(i, 0, j);
                Integer y = BlockUtil.getNearestSurface(level, pos, Direction.UP, 7, true, BlockUtil.SurfaceCriteria.SOLID_LIQUID_TO_AIR);

                if (y != null) {
                    pos = new BlockPos(pos.getX(), y, pos.getZ());
                    double dist = this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());

                    if (random.nextInt((int) dist * 2 + 1) < 1 && dist < 2) {
                        BlockState belowState = level.getBlockState(pos.below());
                        level.setBlock(pos, belowState.getBlock() == Blocks.WATER ? Blocks.ICE.defaultBlockState() : Blocks.SNOW.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    private void spawnParticles() {
        Level level = this.level();
        level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 0, 0, 0);

        for (int i = 0; i < 30 * blastMultiplier; i++) {
            ParticleBuilder.create(EBParticles.ICE, level.getRandom(), this.getX(), this.getY(), this.getZ(), 2 * blastMultiplier, false)
                    .time(35).gravity(true).spawn(level);

            float brightness = 0.4f + level.getRandom().nextFloat() * 0.5f;
            ParticleBuilder.create(EBParticles.DARK_MAGIC, level.getRandom(), this.getX(), this.getY(), this.getZ(), 2 * blastMultiplier, false)
                    .color(brightness, brightness + 0.1f, 1.0f).spawn(level);
        }
    }


    @Override
    protected @NotNull Item getDefaultItem() {
        return ItemStack.EMPTY.getItem();
    }
}
