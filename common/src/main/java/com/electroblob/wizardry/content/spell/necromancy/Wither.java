package com.electroblob.wizardry.content.spell.necromancy;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.BuffSpell;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Wither extends RaySpell {
    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (entityHit.getEntity() instanceof LivingEntity target && !EBMagicDamageSource.isEntityImmune(EBDamageSources.WITHER, target)) {
            DamageSource source = ctx.caster() != null ? EBMagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.WITHER)
                    : target.damageSources().wither();

            target.hurt(source, property(DefaultProperties.DAMAGE) * ctx.modifiers().get(SpellModifiers.POTENCY));
            target.addEffect(new MobEffectInstance(MobEffects.WITHER,
                    (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get())),
                    property(DefaultProperties.EFFECT_STRENGTH) + BuffSpell.getStandardBonusAmplifier(ctx.modifiers().get(SpellModifiers.POTENCY))));
        }

        return true;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.1f, 0, 0).spawn(ctx.world());
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).time(12 + ctx.world().random.nextInt(8)).color(0.1f, 0, 0.05f).spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.NECROMANCY, SpellType.ATTACK, SpellAction.POINT, 20, 5, 30)
                .add(DefaultProperties.RANGE, 10F)
                .add(DefaultProperties.DAMAGE, 1F)
                .add(DefaultProperties.EFFECT_DURATION, 200)
                .add(DefaultProperties.EFFECT_STRENGTH, 1)
                .build();
    }
}
