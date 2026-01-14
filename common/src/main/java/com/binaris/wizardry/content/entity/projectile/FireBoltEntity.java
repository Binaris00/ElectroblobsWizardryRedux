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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class FireBoltEntity extends MagicProjectileEntity {
    public FireBoltEntity(Level world) {
        super(EBEntities.FIRE_BOLT.get(), world);
    }

    public FireBoltEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        super.onHit(hitResult);

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity entity = entityHitResult.getEntity();

            float damage = Spells.FIREBOLT.property(DefaultProperties.DAMAGE) * damageMultiplier;

            if (!MagicDamageSource.isEntityImmune(EBDamageSources.FIRE, entity)) {
                MagicDamageSource.causeMagicDamage(this, entity, damage, EBDamageSources.FIRE);
                entity.setSecondsOnFire(Spells.FIREBOLT.property(DefaultProperties.EFFECT_DURATION));
            }
        }

        this.playSound(EBSounds.ENTITY_FIREBOLT_HIT.get(), 2, 0.8f + random.nextFloat() * 0.3f);
        if (level().isClientSide()) {
            for (int i = 0; i < 8; i++) {
                level().addParticle(ParticleTypes.LAVA, getX() + random.nextFloat() - 0.5, getY() + getBbHeight() / 2 + random.nextFloat() - 0.5, getZ() + random.nextFloat() - 0.5, 0, 0, 0);
            }
        }
        this.discard();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            ParticleBuilder.create(EBParticles.MAGIC_FIRE, this).time(14).spawn(level());

            if (this.tickCount > 1) {
                double x = xo - getDeltaMovement().x / 2 + random.nextFloat() * 0.2 - 0.1;
                double y = yo + getBbHeight() / 2 - getDeltaMovement().y / 2 + random.nextFloat() * 0.2 - 0.1;
                double z = zo - getDeltaMovement().z / 2 + random.nextFloat() * 0.2 - 0.1;
                ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).time(14).spawn(level());
            }
        }
    }


    @Override
    protected @NotNull Item getDefaultItem() {
        return ItemStack.EMPTY.getItem();
    }

    @Override
    public int getLifeTime() {
        return 6;
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
