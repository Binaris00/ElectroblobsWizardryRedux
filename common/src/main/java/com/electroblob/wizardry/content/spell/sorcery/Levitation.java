package com.electroblob.wizardry.content.spell.sorcery;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class Levitation extends Spell {
    public Levitation(){
        soundValues(0.5f, 1, 0);
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        // TODO BIN CONFIG HERE
        //if(!Wizardry.settings.replaceVanillaFallDamage) caster.fallDistance = 0;

        ctx.caster().setDeltaMovement(ctx.caster().getDeltaMovement().x, ctx.caster().getDeltaMovement().y < property(DefaultProperties.SPEED) ?
                ctx.caster().getDeltaMovement().y
                        + property(DefaultProperties.ACCELERATION) : ctx.caster().getDeltaMovement().y, ctx.caster().getDeltaMovement().z);

        if(ctx.world().isClientSide){
            double x = ctx.caster().getX() - 0.25 + ctx.world().random.nextDouble() * 0.5;
            double y = ctx.caster().getEyePosition(1).y;
            double z = ctx.caster().getZ() - 0.25 + ctx.world().random.nextDouble() * 0.5;
            ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, -0.1, 0).time(15)
                    .color(0.5f, 1, 0.7f).spawn(ctx.world());
        }


        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected void playSound(Level world, LivingEntity entity, int castTicks, int duration) {
        this.playSoundLoop(world, entity, castTicks);
    }

    @Override
    protected void playSound(Level world, double x, double y, double z, int ticksInUse, int duration) {
        this.playSoundLoop(world, x, y, z, ticksInUse, duration);
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.SORCERY, SpellType.UTILITY, SpellAction.NONE, 10, 0, 0)
                .add(DefaultProperties.SPEED, 0.5F)
                .add(DefaultProperties.ACCELERATION, 0.1F)
                .build();
    }
}
