package com.electroblob.wizardry.content.entity.construct;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.api.content.entity.construct.ScaledConstructEntity;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.Spells;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LightningSigilConstruct extends ScaledConstructEntity {
    public static final SpellProperty<Integer> SECOND_RANGE = SpellProperty.intProperty("second_range");

    public LightningSigilConstruct(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public LightningSigilConstruct(Level world) {
        super(EBEntities.LIGHTNING_SIGIL.get(), world);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > 600 && this.getCaster() == null && !this.level().isClientSide) this.discard();

        List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(getBbWidth() / 2, this.getX(), this.getY(), this.getZ(), this.level());
        for (LivingEntity target : targets) {
            if(!this.isValidTarget(target)) continue;

            Vec3 originalVec = target.getDeltaMovement();

            boolean damageResult = EBMagicDamageSource.causeMagicDamage(this, target,
                    Spells.LIGHTNING_SIGIL.property(DefaultProperties.DAMAGE) * this.damageMultiplier,
                    EBDamageSources.SHOCK, false);
            if(!damageResult) continue;


            this.playSound(EBSounds.ENTITY_LIGHTNING_SIGIL_TRIGGER.get(), 1.0f, 1.0f);
            target.setDeltaMovement(originalVec);

            double seekerRange = Spells.LIGHTNING_SIGIL.property(DefaultProperties.RANGE);
            List<LivingEntity> secondaryTargets = EntityUtil.getLivingWithinRadius(seekerRange, target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(), level());
            for (int j = 0; j < Math.min(secondaryTargets.size(), Spells.LIGHTNING_SIGIL.property(DefaultProperties.MAX_TARGETS)); j++) {
                LivingEntity secondaryTarget = secondaryTargets.get(j);
                if (secondaryTarget != target && this.isValidTarget(secondaryTarget)) {
                    if (level().isClientSide) {
                        ParticleBuilder.create(EBParticles.LIGHTNING).entity(target)
                                .pos(0, target.getBbHeight() / 2, 0).target(secondaryTarget).spawn(level());

                        ParticleBuilder.spawnShockParticles(level(), secondaryTarget.getX(), secondaryTarget.getY() + secondaryTarget.getBbHeight() / 2, secondaryTarget.getZ());
                    }

                    secondaryTarget.playSound(EBSounds.ENTITY_LIGHTNING_SIGIL_TRIGGER.get(), 1.0F, level().random.nextFloat() * 0.4F + 1.5F);
                    secondaryTarget.hurt(EBMagicDamageSource.causeIndirectMagicDamage(this, getCaster(), EBDamageSources.SHOCK),
                            Spells.LIGHTNING_SIGIL.property(DefaultProperties.DAMAGE) * damageMultiplier);
                }
            }
            this.discard();
        }

        if (this.level().isClientSide && this.random.nextInt(15) == 0) {
            double radius = (0.5 + random.nextDouble() * 0.3) * getBbWidth() / 2;
            float angle = random.nextFloat() * (float) Math.PI * 2;
            ParticleBuilder.create(EBParticles.SPARK)
                    .pos(this.getX() + radius * Mth.cos(angle), this.getY() + 0.1, this.getZ() + radius * Mth.sin(angle))
                    .spawn(level());
        }
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return EntityDimensions.scalable(Spells.LIGHTNING_SIGIL.property(DefaultProperties.EFFECT_RADIUS) * 2, 0.2f);
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }
}
