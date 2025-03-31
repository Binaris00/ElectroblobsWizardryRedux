package com.electroblob.wizardry.common.content.entity.projectile;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.entity.projectile.BombEntity;
import com.electroblob.wizardry.api.common.spell.properties.SpellProperty;
import com.electroblob.wizardry.api.common.util.BlockUtil;
import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.api.common.util.EBMagicDamageSource;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.Spells;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IceCharge extends BombEntity {
    public static final SpellProperty<Integer> ICE_SHARDS = SpellProperty.intProperty("ice_shards");

    public IceCharge(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ItemStack.EMPTY.getItem();
    }

    public IceCharge(Level world) {
        super(EBEntities.ICE_CHARGE.get(), world);
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        super.onHit(hitResult);
        Entity entity = hitResult.getType() == HitResult.Type.ENTITY ? ((EntityHitResult) hitResult).getEntity() : null;

        if (entity != null) {
            float damage = Spells.ICE_CHARGE.property(DefaultProperties.DAMAGE) * damageMultiplier;

            EBMagicDamageSource.causeMagicDamage(this, entity, damage, EBDamageSources.FROST, false);

            if (entity instanceof LivingEntity livingEntity) {
                // TODO: Effect Frost
//                livingEntity.addEffect(new MobEffectInstance(EBEffects.FROST,
//                        Spells.ICE_CHARGE.getIntProperty(Spell.DIRECT_EFFECT_DURATION),
//                        Spells.ICE_CHARGE.getIntProperty(Spell.DIRECT_EFFECT_STRENGTH)));
            }
        }

        if (this.level().isClientSide()) {
            this.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            for (int i = 0; i < 30 * blastMultiplier; i++) {
                ParticleBuilder.create(EBParticles.ICE, level().getRandom(), this.getX(), this.getY(), this.getZ(), 2 * blastMultiplier, false)
                        .time(35).gravity(true).spawn(level());

                float brightness = 0.4f + random.nextFloat() * 0.5f;
                ParticleBuilder.create(EBParticles.DARK_MAGIC, level().getRandom(), this.getX(), this.getY(), this.getZ(), 2 * blastMultiplier, false)
                        .color(brightness, brightness + 0.1f, 1.0f).spawn(level());
            }
        }

        if (!this.level().isClientSide()) {
            this.playSound(EBSounds.ENTITY_ICE_CHARGE_SMASH.get(), 1.5f, random.nextFloat() * 0.4f + 0.6f);
            this.playSound(EBSounds.ENTITY_ICE_CHARGE_ICE.get(), 1.2f, random.nextFloat() * 0.4f + 1.2f);

            double radius = 3 * blastMultiplier;
            List<LivingEntity> targets = EntityUtil.getLivingEntitiesInRange(level(), this.getX(), this.getY(), this.getZ(), radius);

            for (LivingEntity target : targets) {
                // TODO: Effect Frost
//                if (target != entity && target != this.getOwner()) {
//                    target.addEffect(new MobEffectInstance(EBEffects.FROST,
//                            120,
//                            1));
//                }
            }

            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    BlockPos pos = new BlockPos((int) (this.getX() + i), (int) this.getY(), (int) (this.getZ() + j));
                    Integer y = BlockUtil.getNearestSurface(level(), pos, Direction.UP, 7, true, BlockUtil.SurfaceCriteria.SOLID_LIQUID_TO_AIR);

                    if (y != null) {
                        pos = new BlockPos(pos.getX(), y, pos.getZ());

                        double dist = this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());

                        if (random.nextInt((int) dist * 2 + 1) < 1 && dist < 2) {
                            if (level().getBlockState(pos.below()).getBlock() == Blocks.WATER) {
                                level().setBlock(pos.below(), Blocks.ICE.defaultBlockState(), 3);
                            } else {
                                level().setBlock(pos, Blocks.SNOW.defaultBlockState(), 3);
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < Spells.ICE_CHARGE.property(ICE_SHARDS); i++) {
                double dx = random.nextDouble() - 0.5;
                double dy = random.nextDouble() - 0.5;
                double dz = random.nextDouble() - 0.5;
                IceShard iceShard = new IceShard(level());
                iceShard.setPos(this.getX() + dx, this.getY() + dy, this.getZ() + dz);
                iceShard.setDeltaMovement(dx * 1.5, dy * 1.5, dz * 1.5);
                level().addFreshEntity(iceShard);
            }

            this.discard();
        }
    }
}
