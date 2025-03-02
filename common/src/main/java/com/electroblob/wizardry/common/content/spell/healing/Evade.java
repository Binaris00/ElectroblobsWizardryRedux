package com.electroblob.wizardry.common.content.spell.healing;

import com.electroblob.wizardry.api.common.spell.Caster;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class Evade extends Spell {
    @Override
    protected void perform(Caster caster) {
        if(!(caster instanceof Player player)) return;
        if (!player.onGround()) return;

        Vec3 look = player.getLookAngle();

        look = look.subtract(0, look.y, 0).normalize();

        Vec3 evadeDirection;
        if (player.xxa == 0) {
            evadeDirection = look.yRot(player.level().random.nextBoolean() ? (float) Math.PI / 2f : (float) -Math.PI / 2f);
        } else {
            evadeDirection = look.yRot(Math.signum(player.xxa) * (float) Math.PI / 2f);
        }

        evadeDirection = evadeDirection.scale(1);
        player.addDeltaMovement(new Vec3(evadeDirection.x, 0.25f, evadeDirection.z));
    }

    @Override
    protected SpellProperties properties() {
        return null;
    }
}
