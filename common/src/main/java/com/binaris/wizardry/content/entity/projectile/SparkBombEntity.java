package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.BombEntity;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SparkBombEntity extends BombEntity {
    public SparkBombEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public SparkBombEntity(Level world) {
        super(EBEntities.SPARK_BOMB.get(), world);
    }

    public SparkBombEntity(LivingEntity livingEntity, Level world) {
        super(EBEntities.SPARK_BOMB.get(), livingEntity, world);
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        if (result instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity target) {
            float damage = Spells.SPARK_BOMB.property(DefaultProperties.DIRECT_DAMAGE);

            target.hurt(MagicDamageSource.causeIndirectMagicDamage(this, this.getOwner(), EBDamageSources.SHOCK), damage);
        }

        double range = Spells.SPARK_BOMB.property(DefaultProperties.EFFECT_RADIUS);
        List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(range, getX(), getY(), getZ(), level());


        for(int i = 0; i < Math.min(targets.size(), Spells.SPARK_BOMB.property(DefaultProperties.MAX_TARGETS)); i++){
            if (result instanceof EntityHitResult entityHit && targets.get(i) == entityHit.getEntity()) continue;
            if (this.getOwner() instanceof LivingEntity && (targets.get(i) == this.getOwner() || !AllyDesignation.isAllied((LivingEntity) this.getOwner(), targets.get(i)))) continue;
            if (targets.get(i) instanceof Player player && player.isCreative()) continue;

            LivingEntity target = targets.get(i);

            target.playSound(EBSounds.ENTITY_SPARK_BOMB_CHAIN.get(), 1.0F, random.nextFloat() * 0.4F + 1.5F);
            MagicDamageSource.causeMagicDamage(this, target, Spells.SPARK_BOMB.property(DefaultProperties.SPLASH_DAMAGE), EBDamageSources.SHOCK);
            ParticleBuilder.create(EBParticles.LIGHTNING)
                    .pos(this.position()).target(target).time(1)
                    .allowServer(true).spawn(level());
        }

        this.level().broadcastEntityEvent(this, (byte) 3);
        this.playSound(EBSounds.ENTITY_SPARK_BOMB_HIT_BLOCK.get(), 0.5f, 0.5f);
        this.discard();
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ParticleBuilder.spawnShockParticles(this.level(), this.getX(), this.getY() + this.getBbHeight(), this.getZ());
        } else if (id == 4) {
            ParticleBuilder.spawnShockParticles(this.level(), this.getX(), this.getY() + this.getBbHeight() / 2, this.getZ());
        }
    }

    @Override
    public int getRemainingFireTicks() {
        return -1;
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return EBItems.SPARK_BOMB.get();
    }
}
