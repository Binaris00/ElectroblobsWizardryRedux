package com.electroblob.wizardry.common.content.entity.projectile;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.entity.projectile.MagicProjectileEntity;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import com.electroblob.wizardry.setup.registries.client.EBSounds;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.particles.ParticleTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class Thunderbolt extends MagicProjectileEntity {
    public Thunderbolt(Level world) {
        super(null, world);
        //super(EBEntities.THUNDERBOLT.get(), world);
    }

    public Thunderbolt(EntityType<Thunderbolt> entityThunderboltEntityType, Level world) {
        super(entityThunderboltEntityType, world);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (hitResult instanceof EntityHitResult entityHitResult) {
            Entity entity = entityHitResult.getEntity();
            float damage = 3 * damageMultiplier;

            entity.hurt(entity.damageSources().indirectMagic(this, this.getOwner()), damage);
            float knockbackStrength = 0.2F;

            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).knockback(knockbackStrength * 0.5F, Mth.sin(this.getYRot() * 0.017453292F), -Mth.cos(this.getYRot() * 0.017453292F));
            }
        }

        //this.playSound(EBSounds.ENTITY_THUNDERBOLT_HIT.get(), 1.4F, 0.5F + this.random.nextFloat() * 0.1F);

        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 2);
            this.discard();
        }

        super.onHit(hitResult);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 3);
        }
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 2) {
            this.level().addParticle(ParticleTypes.EXPLOSION, this.xo, this.yo, this.zo, 0, 0, 0);
        }

        if (status == 3) {
            //ParticleBuilder.create(EBParticles.SPARK, new Random(), this.xo, this.yo + this.getBbHeight() / 2, this.zo, 0.1, false).spawn(this.level());
            for (int i = 0; i < 4; i++) {
                this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.xo + random.nextFloat() * 0.2 - 0.1,
                        this.yo + this.getBbHeight() / 2 + random.nextFloat() * 0.2 - 0.1,
                        this.zo + random.nextFloat() * 0.2 - 0.1, 0, 0, 0);
            }
        }

        super.handleEntityEvent(status);
    }

    @Override
    public int getLifeTime() {
        return 8;
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ItemStack.EMPTY.getItem();
    }
}
