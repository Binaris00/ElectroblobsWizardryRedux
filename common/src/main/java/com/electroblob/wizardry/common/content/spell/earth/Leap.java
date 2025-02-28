package com.electroblob.wizardry.common.content.spell.earth;

import com.electroblob.wizardry.api.common.spell.Caster;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.api.common.spell.SpellProperty;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class Leap extends Spell {
    public static final SpellProperty<Float> HORIZONTAL_SPEED = SpellProperty.floatProperty("horizontal_speed");
    public static final SpellProperty<Float> VERTICAL_SPEED = SpellProperty.floatProperty("vertical_speed");

    @Override
    protected void perform(Caster caster) {
        if(caster instanceof Player player && player.onGround()) {
            player.setDeltaMovement(player.getDeltaMovement().x, 0.65, player.getDeltaMovement().z);
            double horizontalSpeed = 0.3;
            player.addDeltaMovement(new Vec3(player.getLookAngle().x * horizontalSpeed, 0, player.getLookAngle().z * horizontalSpeed));

            for (int i = 0; i < 10; i++) {
                double x = player.getX() + player.level().random.nextFloat() - 0.5F;
                double y = player.getY();
                double z = player.getZ() + player.level().random.nextFloat() - 0.5F;
                player.level().addParticle(ParticleTypes.CLOUD, x, y, z, 0, 0, 0);
            }
        }
    }

    @Override
    protected SpellProperties properties() {
        return null;
    }
}
