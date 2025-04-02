package com.electroblob.wizardry.content.spell.earth;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.internal.Caster;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class Flight extends Spell {
    private static final double Y_NUDGE_ACCELERATION = 0.075;

    @Override
    protected void perform(Caster caster) {
        if(!(caster instanceof Player player)) return;
        if(player.isInWater() || player.isInLava() || player.isFallFlying()) return;

        // Particles
        if(player.level().isClientSide){
            double x = player.xo - 1 + player.level().random.nextDouble() * 2;
            double y = player.yo + player.getEyeHeight() - 0.5 + player.level().random.nextDouble();
            double z = player.zo - 1 + player.level().random.nextDouble() * 2;
            ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, -0.1, 0).time(15)
                    .color(0.8f, 1, 0.5f).spawn(player.level());
            x = player.xo - 1 + player.level().random.nextDouble() * 2;
            y = player.yo + player.getEyeHeight() - 0.5 + player.level().random.nextDouble();
            z = player.zo - 1 + player.level().random.nextDouble() * 2;
            ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, -0.1, 0).time(15)
                    .color(1f, 1f, 1f).spawn(player.level());
        }

        playSound(caster.getCastLevel(), player, 0, -1);

        // Speed
        float speed = property(DefaultProperties.SPEED);
        float acceleration = 0.05f;

        if((Math.abs(player.getDeltaMovement().x) < speed || player.getDeltaMovement().x / player.getLookAngle().x < 0)
                && (Math.abs(player.getDeltaMovement().z) < speed || player.getDeltaMovement().z / player.getLookAngle().z < 0)){
            player.addDeltaMovement(new Vec3(player.getLookAngle().x * acceleration, 0, player.getLookAngle().z * acceleration));
        }

        if(Math.abs(player.getDeltaMovement().y) < speed || player.getDeltaMovement().y / player.getLookAngle().y < 0){
            player.setDeltaMovement(player.getDeltaMovement().x, player.getDeltaMovement().y +
                    player.getLookAngle().y * acceleration + Y_NUDGE_ACCELERATION, player.getDeltaMovement().z);
        }
        if(!EBConfig.replaceVanillaFallDamage) player.fallDistance = 0.0f;
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(DefaultProperties.ACCELERATION, 0.05F)
                .add(DefaultProperties.SPEED, 0.5F)
                .build();
    }
}
