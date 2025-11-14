package com.electroblob.wizardry.content.spell.earth;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.AreaEffectSpell;
import com.electroblob.wizardry.content.spell.abstr.BuffSpell;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ForestsCurse extends AreaEffectSpell {
    public ForestsCurse() {
        this.alwaysSucceed(true);
        this.soundValues(1, 1.1f, 0.2f);
    }

    @Override
    protected boolean affectEntity(CastContext ctx, Vec3 origin, LivingEntity target, int targetCount) {
        if (!EBMagicDamageSource.isEntityImmune(EBDamageSources.POISON, target)) {
            DamageSource source = ctx.caster() != null ? EBMagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.POISON)
                    : target.damageSources().magic();
            target.hurt(source, property(DefaultProperties.DAMAGE) * ctx.modifiers().get(SpellModifiers.POTENCY));

            int bonusAmplifier = BuffSpell.getStandardBonusAmplifier(ctx.modifiers().get(SpellModifiers.POTENCY));
            int duration = (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get()));
            int amplifier = property(DefaultProperties.EFFECT_STRENGTH) + bonusAmplifier;

            target.addEffect(new MobEffectInstance(MobEffects.POISON, duration, amplifier));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, amplifier));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, amplifier));
        }

        return true;
    }

    @Override
    protected void spawnParticle(Level world, double x, double y, double z) {
        y += 2; // Moves the particles up to the caster's head level

        float brightness = world.random.nextFloat() / 4;
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).velocity(0, -0.2, 0)
                .color(0.05f + brightness, 0.2f + brightness, 0).spawn(world);

        brightness = world.random.nextFloat() / 4;
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, -0.05, 0).time(50)
                .color(0.1f + brightness, 0.2f + brightness, 0).spawn(world);

        ParticleBuilder.create(EBParticles.LEAF).pos(x, y, z).velocity(0, -0.01, 0).time(40 + world.random.nextInt(12)).spawn(world);
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.EARTH, SpellType.ATTACK, SpellAction.POINT_UP, 75, 15, 200)
                .add(DefaultProperties.EFFECT_RADIUS, 5)
                .add(DefaultProperties.DAMAGE, 4F)
                .add(DefaultProperties.EFFECT_DURATION, 140)
                .add(DefaultProperties.EFFECT_STRENGTH, 2)
                .build();
    }
}
