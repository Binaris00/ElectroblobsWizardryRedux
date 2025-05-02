package com.electroblob.wizardry.content.spell.earth;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Leap extends Spell {
    public static final SpellProperty<Float> HORIZONTAL_SPEED = SpellProperty.floatProperty("horizontal_speed", 0.3F);
    public static final SpellProperty<Float> VERTICAL_SPEED = SpellProperty.floatProperty("vertical_speed", 0.65F);

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if(!ctx.caster().onGround()) return false;

        ctx.caster().setDeltaMovement(ctx.caster().getDeltaMovement().x, property(VERTICAL_SPEED) * ctx.modifiers().get(SpellModifiers.POTENCY), ctx.caster().getDeltaMovement().z);
        double horizontalSpeed = property(HORIZONTAL_SPEED);
        ctx.caster().addDeltaMovement(new Vec3(ctx.caster().getLookAngle().x * horizontalSpeed, 0, ctx.caster().getLookAngle().z * horizontalSpeed));

        for (int i = 0; i < 10; i++) {
            double x = ctx.caster().getX() + ctx.world().random.nextFloat() - 0.5F;
            double y = ctx.caster().getY();
            double z = ctx.caster().getZ() + ctx.world().random.nextFloat() - 0.5F;
            ctx.world().addParticle(ParticleTypes.CLOUD, x, y, z, 0, 0, 0);
        }

        return true;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.NOVICE, Elements.EARTH, SpellType.UTILITY, SpellAction.POINT, 10, 0, 20)
                .add(HORIZONTAL_SPEED)
                .add(VERTICAL_SPEED)
                .build();
    }
}
