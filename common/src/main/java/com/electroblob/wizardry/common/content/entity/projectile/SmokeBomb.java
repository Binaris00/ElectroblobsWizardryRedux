package com.electroblob.wizardry.common.content.entity.projectile;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.entity.projectile.BombEntity;
import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.Spells;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SmokeBomb extends BombEntity {

    public SmokeBomb(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public SmokeBomb(LivingEntity livingEntity, Level world) {
        super(EBEntities.SMOKE_BOMB.get(), livingEntity, world);
    }

    public SmokeBomb(Level world) {
        super(EBEntities.SMOKE_BOMB.get(), world);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return EBItems.SMOKE_BOMB.get();
    }

    @Override
    public int getRemainingFireTicks() {
        return -1;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        List<LivingEntity> livingEntities = EntityUtil.getLivingEntitiesInRange(this.level(), this.getX(), this.getY(), this.getZ(), 10);

        for (LivingEntity entity : livingEntities) {
            if (entity != null && entity != this.getOwner()) {
                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,
                        Spells.SMOKE_BOMB.property(DefaultProperties.EFFECT_DURATION),
                        Spells.SMOKE_BOMB.property(DefaultProperties.EFFECT_STRENGTH)));
            }
        }

        if (hitResult instanceof BlockHitResult) {
            if (!this.level().isClientSide()) {
                this.playSound(EBSounds.ENTITY_SMOKE_BOMB_SMASH.get(), 1.5F, random.nextFloat() * 0.4F + 0.6F);
                this.playSound(EBSounds.ENTITY_SMOKE_BOMB_SMOKE.get(), 1.2F, 1.0F);

                // Particle effect
                this.level().broadcastEntityEvent(this, (byte) 3);
                this.discard();
            }
        }

        super.onHit(hitResult);
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 3) {
            this.level().addParticle(ParticleTypes.EXPLOSION, this.xo, this.yo, this.zo, 0, 0, 0);

            for (int i = 0; i < 60 * blastMultiplier; i++) {
                float brightness = random.nextFloat() * 0.1f + 0.1f;
                ParticleBuilder.create(EBParticles.CLOUD, level().getRandom(), this.xo, this.yo, this.zo, 2 * blastMultiplier, false)
                        .color(brightness, brightness, brightness).time(80 + this.random.nextInt(12)).shaded(true).scale(5).spawn(this.level());

                brightness = random.nextFloat() * 0.3f;
                ParticleBuilder.create(EBParticles.DARK_MAGIC, level().getRandom(), this.xo, this.yo, this.zo, 2 * blastMultiplier, false)
                        .color(brightness, brightness, brightness).spawn(this.level());
            }
        }

        super.handleEntityEvent(status);
    }
}

