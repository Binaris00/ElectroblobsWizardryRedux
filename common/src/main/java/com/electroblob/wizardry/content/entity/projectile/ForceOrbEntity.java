package com.electroblob.wizardry.content.entity.projectile;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.entity.projectile.BombEntity;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.Spells;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ForceOrbEntity extends BombEntity {
    public ForceOrbEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public ForceOrbEntity(Level level) {
        super(EBEntities.FORCE_ORB.get(), level);
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        super.onHit(hitResult);
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            this.playSound(EBSounds.ENTITY_FORCE_ORB_HIT.get(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        }

        if (this.level().isClientSide) {
            for (int j = 0; j < 20; j++) {
                float brightness = 0.5f + (random.nextFloat() / 2);
                ParticleBuilder.create(EBParticles.SPARKLE, random, xo, yo, zo, 0.25, true).time(6)
                        .color(brightness, 1.0f, brightness + 0.2f).spawn(level());
            }
            this.level().addParticle(ParticleTypes.EXPLOSION, this.xo, this.yo, this.zo, 0, 0, 0);
            return;
        }

        float pitch = this.random.nextFloat() * 0.2F + 0.3F;
        this.playSound(EBSounds.ENTITY_FORCE_ORB_HIT_BLOCK.get(), 1.5F, pitch);
        this.playSound(EBSounds.ENTITY_FORCE_ORB_HIT_BLOCK.get(), 1.5F, pitch - 0.01f);

        double blastRadius = Spells.FORCE_ORB.property(DefaultProperties.BLAST_RADIUS) * blastMultiplier;

        List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(blastRadius, this.xo,
                this.yo, this.zo, this.level());

        for (LivingEntity target : targets) {
            if (target != this.getOwner()) {

                double velY = target.getDeltaMovement().y;
                double dx = this.xo - target.xo > 0 ? -0.5 - (this.xo - target.xo) / 8 : 0.5 - (this.xo - target.xo) / 8;
                double dz = this.zo - target.zo > 0 ? -0.5 - (this.zo - target.zo) / 8 : 0.5 - (this.zo - target.zo) / 8;

                float damage = Spells.FORCE_ORB.property(DefaultProperties.DAMAGE) * damageMultiplier;

                target.hurt(EBMagicDamageSource.causeIndirectMagicDamage(this, getOwner(), EBDamageSources.BLAST), damage);
                target.setDeltaMovement(dx, velY + 0.4, dz);
            }
        }

        this.discard();
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ItemStack.EMPTY.getItem();
    }
}
