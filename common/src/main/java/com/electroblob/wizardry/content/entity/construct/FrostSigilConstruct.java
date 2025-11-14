package com.electroblob.wizardry.content.entity.construct;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.entity.construct.ScaledConstructEntity;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.*;
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

public class FrostSigilConstruct extends ScaledConstructEntity {
    public FrostSigilConstruct(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public FrostSigilConstruct(Level world) {
        super(EBEntities.FROST_SIGIL.get(), world);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && this.random.nextInt(15) == 0) {
            double radius = (0.5 + random.nextDouble() * 0.3) * getBbWidth() / 2;
            float angle = random.nextFloat() * (float) Math.PI * 2;
            ParticleBuilder.create(EBParticles.SNOW).pos(this.getX() + radius * Mth.cos(angle), this.getY() + 0.1, this.getZ() + radius * Mth.sin(angle)).velocity(0, 0, 0).spawn(level());
        }

        List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(getBbWidth() / 2, this.getX(), this.getY(), this.getZ(), this.level());

        for (LivingEntity target : targets) {
            if (!this.isValidTarget(target)) continue;
            EntityUtil.attackEntityWithoutKnockback(target, this.getCaster() != null ?
                    EBMagicDamageSource.causeIndirectMagicDamage(this, getCaster(), EBDamageSources.FROST) :
                    EBMagicDamageSource.causeDirectMagicDamage(this, EBDamageSources.SORCERY), Spells.FROST_SIGIL.property(DefaultProperties.DAMAGE) * damageMultiplier);

            if (!EBMagicDamageSource.isEntityImmune(EBDamageSources.FROST, target))
                target.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(), Spells.FROST_SIGIL.property(DefaultProperties.EFFECT_DURATION),
                        Spells.FROST_SIGIL.property(DefaultProperties.EFFECT_STRENGTH)));

            this.playSound(EBSounds.ENTITY_FROST_SIGIL_TRIGGER.get(), 1.0f, 1.0f);
            this.discard();
        }
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return EntityDimensions.scalable(Spells.FROST_SIGIL.property(DefaultProperties.EFFECT_RADIUS) * 2, 0.2f);
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }
}
