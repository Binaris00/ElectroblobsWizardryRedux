package com.binaris.wizardry.content.spell.healing;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ArcaneJammer extends RaySpell {
    public ArcaneJammer() {
        this.soundValues(0.7f, 1, 0.4f);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (entityHit.getEntity() instanceof LivingEntity target) {
            if (!ctx.world().isClientSide) {
                target.addEffect(new MobEffectInstance(EBMobEffects.ARCANE_JAMMER.get(),
                        (int) (ctx.modifiers().get(SpellModifiers.DURATION, property(DefaultProperties.EFFECT_DURATION))),
                        (int) (ctx.modifiers().get(SpellModifiers.POTENCY, property(DefaultProperties.EFFECT_STRENGTH)) - 1)));
            }
        }
        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return true;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).time(12 + ctx.world().random.nextInt(8))
                .color(0.9f, 0.3f, 0.7f)
                .spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.HEALING, SpellTypes.ALTERATION, SpellAction.POINT, 30, 5, 50)
                .add(DefaultProperties.RANGE, 10F)
                .add(DefaultProperties.EFFECT_DURATION, 300)
                .add(DefaultProperties.EFFECT_STRENGTH, 0)
                .build();
    }
}
