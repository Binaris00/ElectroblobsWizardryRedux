package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.BombEntity;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FireBombEntity extends BombEntity {
    public FireBombEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public FireBombEntity(LivingEntity livingEntity, Level world) {
        super(EBEntities.FIRE_BOMB.get(), livingEntity, world);
    }

    public FireBombEntity(Level world) {
        super(EBEntities.FIRE_BOMB.get(), world);
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        super.onHit(hitResult);
        if (hitResult instanceof EntityHitResult entityHitResult) {
            Entity entity = entityHitResult.getEntity();

            float damage = Spells.FIREBOMB.property(DefaultProperties.DAMAGE);
            MagicDamageSource.causeMagicDamage(this, entity, damage, EBDamageSources.FIRE);
        } else if (hitResult instanceof BlockHitResult) {
            List<LivingEntity> livingEntities = EntityUtil.getLivingEntitiesInRange(level(), getX(), getY(), getZ(), Spells.FIREBOMB.property(DefaultProperties.EFFECT_RADIUS));

            for (LivingEntity entity : livingEntities) {
                MagicDamageSource.causeMagicDamage(this, entity, Spells.FIREBOMB.property(DefaultProperties.SPLASH_DAMAGE) * blastMultiplier, EBDamageSources.FIRE);
                if (!MagicDamageSource.isEntityImmune(EBDamageSources.FIRE, entity))
                    entity.setSecondsOnFire(Spells.FIREBOMB.property(DefaultProperties.EFFECT_DURATION));
            }
        }

        if (!level().isClientSide()) {
            this.playSound(EBSounds.ENTITY_FIREBOMB_SMASH.get(), 1.5F, random.nextFloat() * 0.4F + 0.6F);
            this.playSound(EBSounds.ENTITY_FIREBOMB_FIRE.get(), 1, 1);

            // Spawn particles
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    public void handleEntityEvent(byte b) {
        if (b == 3) {
            ParticleBuilder.create(EBParticles.FLASH).pos(this.position()).scale(5 * blastMultiplier).color(1, 0.6f, 0).spawn(level());

            for (int i = 0; i < 60 * blastMultiplier; i++) {
                ParticleBuilder.create(EBParticles.MAGIC_FIRE, level().getRandom(), xo, yo, zo, 2 * blastMultiplier, false)
                        .time(10 + random.nextInt(4)).scale(1 + random.nextFloat()).spawn(level());

                ParticleBuilder.create(EBParticles.DARK_MAGIC, level().getRandom(), xo, yo, zo, 2 * blastMultiplier, false)
                        .color(1.0f, 0.2f + random.nextFloat() * 0.4f, 0.0f).spawn(level());
            }
            level().addParticle(ParticleTypes.EXPLOSION, xo, yo, zo, 0, 0, 0);
        }
    }


    @Override
    public int getRemainingFireTicks() {
        return -1;
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return EBItems.FIREBOMB.get();
    }
}
