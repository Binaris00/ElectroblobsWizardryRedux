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

public class FireBolt extends MagicProjectileEntity {
    public FireBolt(Level world) {
        super(EBEntities.FIRE_BOLT.get(), world);
    }

    public FireBolt(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ItemStack.EMPTY.getItem();
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if(hitResult.getType() == HitResult.Type.ENTITY){
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity entity = entityHitResult.getEntity();

            float damage = Spells.FIREBOLT.property(DefaultProperties.DAMAGE) * damageMultiplier;

            if(!EBMagicDamageSource.isEntityImmune(EBDamageSources.FIRE, entity)) {
                EBMagicDamageSource.causeMagicDamage(this, entity, damage, EBDamageSources.FIRE, false);
                entity.setSecondsOnFire(Spells.FIREBOLT.property(DefaultProperties.EFFECT_DURATION));
            }
        }

        this.playSound(EBSounds.ENTITY_FIREBOLT_HIT.get(), 2, 0.8f + random.nextFloat() * 0.3f);
        if(level().isClientSide()){
            for(int i = 0; i < 8; i++){
                level().addParticle(ParticleTypes.LAVA, getX() + random.nextFloat() - 0.5, getY() + getBbHeight() / 2 + random.nextFloat() - 0.5, getZ() + random.nextFloat() - 0.5, 0, 0, 0);
            }
        }
        this.discard();
        super.onHit(hitResult);
    }

    @Override
    public void tick() {
        super.tick();
        if(level().isClientSide){
            ParticleBuilder.create(EBParticles.MAGIC_FIRE, this).time(14).spawn(level());

            if(this.tickCount > 1){
                double x = xo - getDeltaMovement().x / 2 + random.nextFloat() * 0.2 - 0.1;
                double y = yo + getBbHeight() / 2 - getDeltaMovement().y / 2 + random.nextFloat() * 0.2 - 0.1;
                double z = zo - getDeltaMovement().z / 2 + random.nextFloat() * 0.2 - 0.1;
                ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).time(14).spawn(level());
            }
        }
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
