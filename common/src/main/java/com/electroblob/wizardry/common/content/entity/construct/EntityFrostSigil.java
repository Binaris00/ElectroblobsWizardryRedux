package com.electroblob.wizardry.common.content.entity.construct;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.common.content.entity.abstr.ScaledConstructEntity;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBMobEffects;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityFrostSigil extends ScaledConstructEntity {


    public EntityFrostSigil(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public EntityFrostSigil(Level world) {
        super(EBEntities.FROST_SIGIL.get(), world);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return EntityDimensions.scalable(1 * 2, 0.2f);
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(getBbWidth() / 2, this.getX(), this.getY(), this.getZ(), this.level());

            for (LivingEntity target : targets) {
                if (this.isValidTarget(target)) {
                    // TODO Magic Attack
                    EntityUtil.attackEntityWithoutKnockback(target, this.getCaster() != null ? this.damageSources().indirectMagic(this, this.getCaster()) :
                            this.damageSources().magic(), 8 * damageMultiplier);

                    // TODO MAGIC DAMAGE
                    //if (!MagicDamage.isEntityImmune(DamageType.FROST, target))
                    target.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(), 200, 1));

                    // TODO ENTITY SOUND
                    //this.playSound(WizardrySounds.ENTITY_FROST_SIGIL_TRIGGER.get(), 1.0f, 1.0f);
                    this.discard();
                }
            }
        } else if (this.random.nextInt(15) == 0) {
            double radius = (0.5 + random.nextDouble() * 0.3) * getBbWidth() / 2;
            float angle = random.nextFloat() * (float) Math.PI * 2;
            ParticleBuilder.create(EBParticles.SNOW).pos(this.getX() + radius * Mth.cos(angle), this.getY() + 0.1, this.getZ() + radius * Mth.sin(angle)).velocity(0, 0, 0).spawn(level());
        }
    }
}
