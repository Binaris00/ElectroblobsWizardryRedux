package com.electroblob.wizardry.common.content.spell.lightning;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.spell.Caster;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Random;

public class InvokeWeather extends Spell {
    @Override
    protected void perform(Caster caster) {
        if(!(caster instanceof Player player)) return;

        if (player.level().dimension().equals(Level.OVERWORLD)) {
            if (!player.level().isClientSide) {
                int standardWeatherTime = (300 + (new Random()).nextInt(600)) * 20;

                // TODO Bin: Spell translation names
                if (player.level().isRaining()) {
                    player.displayClientMessage(Component.translatable("spell." + this.getLocation() + ".sun"), true);
                    ((ServerLevel) player.level()).setWeatherParameters(standardWeatherTime, 0, false, false);
                } else {
                    player.displayClientMessage(Component.translatable("spell." + this.getLocation() + ".rain"), true);
                    ((ServerLevel) player.level()).setWeatherParameters(0, standardWeatherTime, true,  player.level().random.nextFloat() < 0.2D);
                }
            }

            if (player.level().isClientSide) {
                for (int i = 0; i < 10; i++) {
                    double x = player.getX() + player.level().random.nextDouble() * 2 - 1;
                    double y = player.getY() + player.getEyeHeight() - 0.5 + player.level().random.nextDouble();
                    double z = player.getZ() + player.level().random.nextDouble() * 2 - 1;
                    ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, 0.1, 0)
                            .color(0.5f, 0.7f, 1).spawn(player.level());
                }
            }
            this.playSound(caster.getCastLevel(), player, 0, -1);
        }
    }

    @Override
    protected SpellProperties properties() {
        return null;
    }
}
