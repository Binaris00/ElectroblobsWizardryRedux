package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.MagicArrowEntity;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.Spells;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class LightningArrow extends MagicArrowEntity {

    public LightningArrow(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    public LightningArrow(Level world) {
        super(EBEntities.LIGHTNING_ARROW.get(), world);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        for (int i = 0; i < 8; i++) {
            if (this.level().isClientSide()) {
                ParticleBuilder.create(EBParticles.SPARK, level().getRandom(), this.xo, this.yo + this.getBbHeight() / 2, this.zo, 1, false)
                        .spawn(this.level());
            }
        }
    }

    @Override
    public void ticksInAir() {
        if (!this.level().isClientSide) return;
        ParticleBuilder.create(EBParticles.SPARK).pos(this.xo, this.yo, this.zo).spawn(this.level());
    }

    @Override
    public @NotNull SoundEvent getSoundEvent(HitResult result) {
        return EBSounds.ENTITY_LIGHTNING_ARROW_HIT.get();
    }

    @Override
    public double getDamage(@NotNull EntityHitResult hitResult) {
        return Spells.LIGHTNING_ARROW.property(DefaultProperties.DAMAGE);
    }

    @Override
    public int getLifetime() {
        return 20;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public ResourceKey<DamageType> getDamageType(@NotNull EntityHitResult hitResult) {
        return EBDamageSources.SHOCK;
    }
}
