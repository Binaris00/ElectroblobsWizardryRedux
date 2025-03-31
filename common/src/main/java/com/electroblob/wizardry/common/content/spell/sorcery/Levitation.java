package com.electroblob.wizardry.common.content.spell.sorcery;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.spell.internal.Caster;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.spell.properties.SpellProperties;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.entity.player.Player;

public class Levitation extends Spell {
    public Levitation(){

        // TODO BIN Sound loop
        soundValues(0.5f, 1, 0);
    }
//
//    @Override
//    protected SoundEvent[] createSounds(){
//        return this.createContinuousSpellSounds();
//    }
//
//    @Override
//    protected void playSound(Level world, LivingEntity entity, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds){
//        this.playSoundLoop(world, entity, ticksInUse);
//    }
//
//    @Override
//    protected void playSound(Level world, double x, double y, double z, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds){
//        this.playSoundLoop(world, x, y, z, ticksInUse, duration);
//    }

    @Override
    protected void perform(Caster caster) {
        if(!(caster instanceof Player player)) return;
        // TODO BIN CONFIG HERE
        //if(!Wizardry.settings.replaceVanillaFallDamage) caster.fallDistance = 0;

        player.setDeltaMovement(player.getDeltaMovement().x, player.getDeltaMovement().y < property(DefaultProperties.SPEED) ?
                player.getDeltaMovement().y
                + property(DefaultProperties.ACCCELERATION) : player.getDeltaMovement().y, player.getDeltaMovement().z);

        if(player.level().isClientSide){
            double x = player.getX() - 0.25 + player.level().random.nextDouble() * 0.5;
            double y = player.getEyePosition(1).y;
            double z = player.getZ() - 0.25 + player.level().random.nextDouble() * 0.5;
            ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, -0.1, 0).time(15)
                    .color(0.5f, 1, 0.7f).spawn(player.level());
        }


        this.playSound(caster.getCastLevel(), player, 0, -1);
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }


    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(DefaultProperties.SPEED, 0.5F)
                .add(DefaultProperties.ACCCELERATION, 0.1F)
                .build();
    }
}
