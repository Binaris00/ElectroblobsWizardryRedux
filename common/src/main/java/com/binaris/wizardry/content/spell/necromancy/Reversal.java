package com.binaris.wizardry.content.spell.necromancy;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.core.EBConstants;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Reversal extends RaySpell {
    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (ctx.caster() == null || !(entityHit.getEntity() instanceof LivingEntity target) || ctx.world().isClientSide) {
            return true;
        }

        List<MobEffectInstance> harmfulEffects = new ArrayList<>(ctx.caster().getActiveEffects().stream()
                .filter(effect -> effect.getEffect().getCategory() == MobEffectCategory.HARMFUL)
                .toList());

        int bonusEffects = (int) (ctx.modifiers().get(SpellModifiers.POTENCY) - 1 / EBConstants.POTENCY_INCREASE_PER_TIER + 0.5F) - 1;
        int n = property(DefaultProperties.EFFECT_STRENGTH) + bonusEffects;

        for (int i = 0; i < n; i++) {
            if (harmfulEffects.isEmpty()) break;
            MobEffectInstance effectToTransfer = harmfulEffects.get(ctx.world().random.nextInt(harmfulEffects.size()));
            ctx.caster().removeEffect(effectToTransfer.getEffect());
            target.addEffect(effectToTransfer);
            harmfulEffects.remove(effectToTransfer);
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
    public boolean canCastByLocation() {
        return false; // :c
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.1f, 0, 0).spawn(ctx.world());
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).time(12 + ctx.world().random.nextInt(8)).color(0.1f, 0, 0.05f).spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.NECROMANCY, SpellType.ALTERATION, SpellAction.POINT, 40, 0, 80)
                .add(DefaultProperties.RANGE, 8F)
                .add(DefaultProperties.EFFECT_STRENGTH, 1)
                .build();
    }
}
