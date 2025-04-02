package com.electroblob.wizardry.content.spell.earth;

import com.electroblob.wizardry.api.content.spell.internal.Caster;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class Leap extends Spell {
    public static final SpellProperty<Float> HORIZONTAL_SPEED = SpellProperty.floatProperty("horizontal_speed", 0.3F);
    public static final SpellProperty<Float> VERTICAL_SPEED = SpellProperty.floatProperty("vertical_speed", 0.65F);

    @Override
    protected void perform(Caster caster) {
        if(caster instanceof Player player && player.onGround()) {
            player.setDeltaMovement(player.getDeltaMovement().x, property(VERTICAL_SPEED), player.getDeltaMovement().z);
            double horizontalSpeed = property(HORIZONTAL_SPEED);
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
        return SpellProperties.builder()
                .add(HORIZONTAL_SPEED)
                .add(VERTICAL_SPEED)
                .build();
    }
}
