package com.binaris.wizardry.content.entity.construct;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.construct.ScaledConstructEntity;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.Spells;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;

import java.util.List;

public class HealAuraConstruct extends ScaledConstructEntity {
    public HealAuraConstruct(EntityType<?> type, Level world) {
        super(type, world);
    }

    public HealAuraConstruct(Level world) {
        super(EBEntities.HEAL_AURA.get(), world);
        setSize(Spells.HEALING_AURA.property(DefaultProperties.EFFECT_RADIUS) * 2, 1);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % 25 == 1) this.playSound(EBSounds.ENTITY_HEAL_AURA_AMBIENT.get(), 0.1f, 1.0f);

        if (this.level().isClientSide) {
            for (int i = 1; i < 3; i++) {
                float brightness = 0.5f + (random.nextFloat() * 0.5f);
                double radius = random.nextDouble() * (getBbWidth() / 2);
                float angle = random.nextFloat() * (float) Math.PI * 2;

                ParticleBuilder.create(EBParticles.SPARKLE)
                        .pos(this.xo + radius * Mth.cos(angle), this.yo, this.zo + radius * Mth.sin(angle))
                        .velocity(0, 0.05, 0)
                        .time(48 + this.random.nextInt(12))
                        .color(1.0f, 1.0f, brightness)
                        .spawn(level());
            }
            return;
        }

        List<LivingEntity> targets = EntityUtil.getLivingWithinCylinder(getBbWidth() / 2, xo, yo, zo, getBbHeight(), level());
        for (LivingEntity target : targets) {
            if (!this.isValidTarget(target)) {
                if (target.getHealth() < target.getMaxHealth() && target.tickCount % 5 == 0) {
                    target.heal(Spells.HEALING_AURA.property(DefaultProperties.HEALTH) * damageMultiplier);
                }
                continue;
            }

            if (target.getMobType() == MobType.UNDEAD && this.tickCount % 10 == 1) {
                EntityUtil.attackEntityWithoutKnockback(target, this.getCaster() != null ?
                                MagicDamageSource.causeIndirectMagicDamage(this, getCaster(), EBDamageSources.RADIANT) :
                                MagicDamageSource.causeDirectMagicDamage(this, EBDamageSources.SORCERY),
                        Spells.HEALING_AURA.property(DefaultProperties.DAMAGE) * damageMultiplier);
            }
        }
    }
}