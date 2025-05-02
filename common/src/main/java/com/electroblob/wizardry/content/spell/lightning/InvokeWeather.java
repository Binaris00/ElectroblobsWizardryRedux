package com.electroblob.wizardry.content.spell.lightning;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class InvokeWeather extends Spell {
    public static final SpellProperty<Float> THUNDER_CHANCE = SpellProperty.floatProperty("thunderstorm_chance", 0.2F);

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if(!ctx.world().dimension().equals(Level.OVERWORLD)) return false;

        if (!ctx.world().isClientSide) {
            int standardWeatherTime = (300 + (new Random()).nextInt(600)) * 20;

            // TODO Bin: Spell translation names
            if (ctx.world().isRaining()) {
                ctx.caster().displayClientMessage(Component.translatable("spell." + this.getDescriptionId() + ".sun"), true);
                ((ServerLevel) ctx.world()).setWeatherParameters(standardWeatherTime, 0, false, false);
            } else {
                ctx.caster().displayClientMessage(Component.translatable("spell." + this.getDescriptionId() + ".rain"), true);
                ((ServerLevel) ctx.world()).setWeatherParameters(0, standardWeatherTime, true,  ctx.world().random.nextFloat() < property(THUNDER_CHANCE));
            }
        } else {
            for (int i = 0; i < 10; i++) {
                double x = ctx.caster().getX() + ctx.world().random.nextDouble() * 2 - 1;
                double y = ctx.caster().getY() + ctx.caster().getEyeHeight() - 0.5 + ctx.world().random.nextDouble();
                double z = ctx.caster().getZ() + ctx.world().random.nextDouble() * 2 - 1;
                ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, 0.1, 0)
                        .color(0.5f, 0.7f, 1).spawn(ctx.world());
            }
        }
        this.playSound(ctx.world(), ctx.caster(), 0, -1);
        return true;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.LIGHTNING, SpellType.UTILITY, SpellAction.POINT_UP, 30, 15, 100)
                .add(THUNDER_CHANCE)
                .build();
    }
}
