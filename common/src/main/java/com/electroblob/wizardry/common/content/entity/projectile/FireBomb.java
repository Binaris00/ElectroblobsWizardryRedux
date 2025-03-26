package com.electroblob.wizardry.common.content.entity.projectile;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.entity.projectile.BombEntity;
import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.api.common.util.EBMagicDamageSource;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
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

public class FireBomb extends BombEntity {
    public FireBomb(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public FireBomb(LivingEntity livingEntity, Level world) {
        super(EBEntities.FIRE_BOMB.get(), livingEntity, world);
    }

    public FireBomb(Level world) {
        super(EBEntities.FIRE_BOMB.get(), world);
    }

    @Override
    public int getRemainingFireTicks() {
        return -1;
    }


    @Override
    protected @NotNull Item getDefaultItem() {
        return EBItems.FIREBOMB.get();
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        super.onHit(hitResult);
        if(hitResult instanceof EntityHitResult entityHitResult){
            Entity entity = entityHitResult.getEntity();

            float damage = 5;
            EBMagicDamageSource.causeMagicDamage(this, entity, damage, EBDamageSources.FIRE, false);
        }

        if(hitResult instanceof BlockHitResult){
            List<LivingEntity> livingEntities = EntityUtil.getLivingEntitiesInRange(level(), getX(), getY(), getZ(), 10);

            for(LivingEntity entity: livingEntities){
                EBMagicDamageSource.causeMagicDamage(this, entity, 3 * blastMultiplier, EBDamageSources.FIRE, false);
                if(!EBMagicDamageSource.isEntityImmune(EBDamageSources.FIRE, entity)) entity.setSecondsOnFire(7);
            }
        }

        if(!level().isClientSide()){
            this.playSound(EBSounds.ENTITY_FIREBOMB_SMASH.get(), 1.5F, random.nextFloat() * 0.4F + 0.6F);
            this.playSound(EBSounds.ENTITY_FIREBOMB_FIRE.get(), 1, 1);

            // Spawn particles
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    public void handleEntityEvent(byte b) {
        if(b == 3){
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
}
