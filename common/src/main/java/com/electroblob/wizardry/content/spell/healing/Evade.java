package com.electroblob.wizardry.content.spell.healing;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import net.minecraft.world.phys.Vec3;

public class Evade extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (!ctx.caster().onGround()) return false;

        Vec3 look = ctx.caster().getLookAngle();

        look = look.subtract(0, look.y, 0).normalize();

        Vec3 evadeDirection;
        if (ctx.caster().xxa == 0) {
            evadeDirection = look.yRot(ctx.world().random.nextBoolean() ? (float) Math.PI / 2f : (float) -Math.PI / 2f);
        } else {
            evadeDirection = look.yRot(Math.signum(ctx.caster().xxa) * (float) Math.PI / 2f);
        }

        evadeDirection = evadeDirection.scale(this.property(DefaultProperties.SPEED));
        ctx.caster().addDeltaMovement(new Vec3(evadeDirection.x, 0.25f, evadeDirection.z));
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder().add(DefaultProperties.SPEED, 1F).build();
    }
}
