package com.binaris.wizardry.content.spell.earth;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.OLDEBCONFIG;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
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

        float potencyModifier = ctx.modifiers().get(SpellModifiers.POTENCY);
        float speed = property(DefaultProperties.SPEED) * potencyModifier;
        float acceleration = property(DefaultProperties.ACCELERATION) * potencyModifier;

        // setting limits
        speed = Math.min(speed, 0.7f);
        acceleration = Math.min(acceleration, 0.1f);

        Vec3 currentVelocity = ctx.caster().getDeltaMovement();
        Vec3 lookAngle = ctx.caster().getLookAngle();

        double newVelX = currentVelocity.x;
        double newVelY = currentVelocity.y;
        double newVelZ = currentVelocity.z;

        if (Math.abs(lookAngle.x) > 0.01) {
            double targetVelX = lookAngle.x * speed;
            if (Math.abs(currentVelocity.x) < Math.abs(targetVelX)) {
                newVelX = Math.min(Math.max(currentVelocity.x + lookAngle.x * acceleration, -speed), speed);
            }
        }

        if (Math.abs(lookAngle.z) > 0.01) {
            double targetVelZ = lookAngle.z * speed;
            if (Math.abs(currentVelocity.z) < Math.abs(targetVelZ)) {
                newVelZ = Math.min(Math.max(currentVelocity.z + lookAngle.z * acceleration, -speed), speed);
            }
        }

        if (Math.abs(lookAngle.y) > 0.01) {
            double targetVelY = lookAngle.y * speed;
            if (Math.abs(currentVelocity.y) < Math.abs(targetVelY)) {
                double yAcceleration = lookAngle.y * acceleration;

                if (lookAngle.y < 0) {
                    yAcceleration += 0.10;
                } else {
                    yAcceleration += Y_NUDGE_ACCELERATION;
                }

                newVelY = currentVelocity.y + yAcceleration;
                newVelY = Math.min(Math.max(newVelY, -speed), speed);
            }
        } else {
            newVelY = Math.min(currentVelocity.y + Y_NUDGE_ACCELERATION, speed * 0.5);
        }

        ctx.caster().setDeltaMovement(newVelX, newVelY, newVelZ);

        if (!OLDEBCONFIG.replaceVanillaFallDamage) ctx.caster().fallDistance = 0.0f;

        return true;
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
