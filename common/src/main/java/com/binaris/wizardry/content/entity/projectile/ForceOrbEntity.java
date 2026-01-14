package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.BombEntity;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.Spells;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
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
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);

        if (!this.level().isClientSide) {
            handleEntityHit();
            this.level().broadcastEntityEvent(this, (byte) 3); // Particles (client-side)
            handleServerHit();
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);

        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3); // Particles (client-side)
            handleServerHit();
        }

    }

    @Override
    public void handleEntityEvent(byte b) {
        if (b != 3) return;

        for (int j = 0; j < 20; j++) {
            float brightness = 0.5f + (random.nextFloat() / 2);
            ParticleBuilder.create(EBParticles.SPARKLE, random, xo, yo, zo, 0.25, true)
                    .time(6)
                    .color(brightness, 1.0f, brightness + 0.2f)
                    .spawn(level());
        }
        this.level().addParticle(ParticleTypes.EXPLOSION, this.xo, this.yo, this.zo, 0, 0, 0);
    }


    private void handleEntityHit() {
        this.playSound(EBSounds.ENTITY_FORCE_ORB_HIT.get(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
    }

    private void handleServerHit() {
        float pitch = this.random.nextFloat() * 0.2F + 0.3F;
        this.playSound(EBSounds.ENTITY_FORCE_ORB_HIT_BLOCK.get(), 1.5F, pitch);
        this.playSound(EBSounds.ENTITY_FORCE_ORB_HIT_BLOCK.get(), 1.5F, pitch - 0.01f);

        double blastRadius = Spells.FORCE_ORB.property(DefaultProperties.BLAST_RADIUS) * blastMultiplier;

        List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(blastRadius, this.xo,
                this.yo, this.zo, this.level());

        targets.stream()
                .filter(target -> target != this.getOwner())
                .forEach(target -> {
                    double velY = target.getDeltaMovement().y;
                    double dx = this.xo - target.xo > 0 ? -0.5 - (this.xo - target.xo) / 8 : 0.5 - (this.xo - target.xo) / 8;
                    double dz = this.zo - target.zo > 0 ? -0.5 - (this.zo - target.zo) / 8 : 0.5 - (this.zo - target.zo) / 8;
                    float damage = Spells.FORCE_ORB.property(DefaultProperties.DAMAGE) * damageMultiplier;
                    target.hurt(MagicDamageSource.causeIndirectMagicDamage(this, getOwner(), EBDamageSources.BLAST), damage);
                    target.setDeltaMovement(dx, velY + 0.4, dz);
                });

        this.discard();
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ItemStack.EMPTY.getItem();
    }
}
