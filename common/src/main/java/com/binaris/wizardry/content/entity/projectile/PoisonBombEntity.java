package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.BombEntity;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

public class PoisonBombEntity extends BombEntity {
    public PoisonBombEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public PoisonBombEntity(LivingEntity livingEntity, Level world) {
        super(EBEntities.POISON_BOMB.get(), livingEntity, world);
    }

    public PoisonBombEntity(Level world) {
        super(EBEntities.POISON_BOMB.get(), world);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return EBItems.POISON_BOMB.get();
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        if (level().isClientSide) return;
        float damage = Spells.POISON_BOMB.property(DefaultProperties.DAMAGE) * damageMultiplier;

        if (hitResult instanceof EntityHitResult entityHitResult) {
            Entity entity = entityHitResult.getEntity();
            MagicDamageSource.causeMagicDamage(this, entity, damage, EBDamageSources.POISON);
        }

        if (hitResult instanceof BlockHitResult) {
            double range = Spells.POISON_BOMB.property(DefaultProperties.EFFECT_RADIUS);
            List<LivingEntity> livingEntities = EntityUtil.getLivingEntitiesInRange(this.level(), this.getX(), this.getY(), this.getZ(), range);
            for (LivingEntity entity : livingEntities) {
                if (entity != null && entity != this.getOwner()) {
                    MagicDamageSource.causeMagicDamage(this, entity, damage, EBDamageSources.FIRE);
                    if (!MagicDamageSource.isEntityImmune(EBDamageSources.POISON, entity))
                        entity.addEffect(new MobEffectInstance(MobEffects.POISON,
                                Spells.POISON_BOMB.property(DefaultProperties.EFFECT_DURATION),
                                Spells.POISON_BOMB.property(DefaultProperties.EFFECT_STRENGTH)));
                }
            }
        }

        this.playSound(EBSounds.ENTITY_POISON_BOMB_SMASH.get(), 1.5F, random.nextFloat() * 0.4F + 0.6F);
        this.playSound(EBSounds.ENTITY_POISON_BOMB_POISON.get(), 1.2F, 1.0F);

        this.level().broadcastEntityEvent(this, (byte) 3);
        this.discard();

    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 3) {
            for (int i = 0; i < 60 * blastMultiplier; i++) {
                ParticleBuilder.create(EBParticles.SPARKLE, level().getRandom(), this.xo, this.yo, this.zo, 2 * blastMultiplier).scale(2)
                        .color(0.2f + random.nextFloat() * 0.3f, 0.6f, 0.0f).time(35).spawn(this.level());

                ParticleBuilder.create(EBParticles.DARK_MAGIC, level().getRandom(), this.xo, this.yo, this.zo, 2 * blastMultiplier, false)
                        .color(0.2f + random.nextFloat() * 0.2f, 0.8f, 0.0f).spawn(this.level());
            }
            this.level().addParticle(ParticleTypes.EXPLOSION, this.xo, this.yo, this.zo, 0, 0, 0);
        }
        super.handleEntityEvent(status);
    }
}
