package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.MagicProjectileEntity;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.Spells;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class SparkEntity extends MagicProjectileEntity {
    public SparkEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public SparkEntity(Level world) {
        super(EBEntities.SPARK.get(), world);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            float damage = Spells.HOMING_SPARK.property(DefaultProperties.DAMAGE) * damageMultiplier;
            Entity entity = entityHitResult.getEntity();
            MagicDamageSource.causeMagicDamage(this, entity, damage, EBDamageSources.SHOCK);
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
    protected @NotNull Item getDefaultItem() {
        return ItemStack.EMPTY.getItem();
    }

    @Override
    public float getSeekingStrength() {
        return Spells.HOMING_SPARK.property(DefaultProperties.SEEKING_STRENGTH);
    }
}
