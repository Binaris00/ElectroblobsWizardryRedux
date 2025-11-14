package com.electroblob.wizardry.content.spell.earth;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Flight extends Spell {
    private static final double Y_NUDGE_ACCELERATION = 0.075;

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (ctx.caster().isInWater() || ctx.caster().isInLava() || ctx.caster().isFallFlying()) return false;

        // Particles
        if (ctx.caster().level().isClientSide) {
            double x = ctx.caster().xo - 1 + ctx.caster().level().random.nextDouble() * 2;
            double y = ctx.caster().yo + ctx.caster().getEyeHeight() - 0.5 + ctx.caster().level().random.nextDouble();
            double z = ctx.caster().zo - 1 + ctx.caster().level().random.nextDouble() * 2;
            ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, -0.1, 0).time(15)
                    .color(0.8f, 1, 0.5f).spawn(ctx.caster().level());
            x = ctx.caster().xo - 1 + ctx.caster().level().random.nextDouble() * 2;
            y = ctx.caster().yo + ctx.caster().getEyeHeight() - 0.5 + ctx.caster().level().random.nextDouble();
            z = ctx.caster().zo - 1 + ctx.caster().level().random.nextDouble() * 2;
            ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, -0.1, 0).time(15)
                    .color(1f, 1f, 1f).spawn(ctx.caster().level());
        }

        if (ctx.castingTicks() % 24 == 0) playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);

        float speed = property(DefaultProperties.SPEED) * ctx.modifiers().get(SpellModifiers.POTENCY);
        float acceleration = property(DefaultProperties.ACCELERATION) * ctx.modifiers().get(SpellModifiers.POTENCY);

        if ((Math.abs(ctx.caster().getDeltaMovement().x) < speed || ctx.caster().getDeltaMovement().x / ctx.caster().getLookAngle().x < 0)
                && (Math.abs(ctx.caster().getDeltaMovement().z) < speed || ctx.caster().getDeltaMovement().z / ctx.caster().getLookAngle().z < 0)) {
            ctx.caster().addDeltaMovement(new Vec3(ctx.caster().getLookAngle().x * acceleration, 0, ctx.caster().getLookAngle().z * acceleration));
        }

        if (Math.abs(ctx.caster().getDeltaMovement().y) < speed || ctx.caster().getDeltaMovement().y / ctx.caster().getLookAngle().y < 0) {
            ctx.caster().setDeltaMovement(ctx.caster().getDeltaMovement().x, ctx.caster().getDeltaMovement().y +
                    ctx.caster().getLookAngle().y * acceleration + Y_NUDGE_ACCELERATION, ctx.caster().getDeltaMovement().z);
        }
        if (!EBConfig.replaceVanillaFallDamage) ctx.caster().fallDistance = 0.0f;

        return true;
    }

    @Override
    public boolean canCastByEntity() {
        return false;
    }

    @Override
    public boolean canCastByLocation() {
        return false;
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.EARTH, SpellType.UTILITY, SpellAction.NONE, 10, 0, 0)
                .add(DefaultProperties.ACCELERATION, 0.05F)
                .add(DefaultProperties.SPEED, 0.5F)
                .build();
    }
}
