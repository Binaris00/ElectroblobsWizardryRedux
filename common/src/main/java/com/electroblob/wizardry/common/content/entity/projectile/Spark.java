package com.electroblob.wizardry.common.content.entity.projectile;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.entity.projectile.MagicProjectileEntity;
import com.electroblob.wizardry.api.common.util.EBMagicDamageSource;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.Spells;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class Spark extends MagicProjectileEntity {

    public Spark(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ItemStack.EMPTY.getItem();
    }

    public Spark(Level world) {
        super(EBEntities.SPARK.get(), world);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            float damage = 6 * damageMultiplier;
            Entity entity = entityHitResult.getEntity();
            EBMagicDamageSource.causeMagicDamage(this, entity, damage, EBDamageSources.SHOCK, false);
        }

        this.playSound(EBSounds.ENTITY_HOMING_SPARK_HIT.get(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));

        if (this.level().isClientSide()) {
            for (int i = 0; i < 8; i++) {
                double x = this.xo + random.nextDouble() - 0.5;
                double y = this.yo + this.getBbHeight() / 2 + random.nextDouble() - 0.5;
                double z = this.zo + random.nextDouble() - 0.5;
                ParticleBuilder.create(EBParticles.SPARK).pos(x, y, z).spawn(this.level());
            }
        }

        super.onHit(hitResult);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public int getLifeTime() {
        return 50;
    }

    @Override
    public float getSeekingStrength() {
        return Spells.HOMING_SPARK.property(DefaultProperties.SEEKING_STRENGTH);
    }
}
