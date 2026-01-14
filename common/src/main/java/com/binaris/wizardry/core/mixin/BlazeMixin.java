package com.binaris.wizardry.core.mixin;

import com.binaris.wizardry.content.entity.living.IceWraith;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Blaze.class)
public abstract class BlazeMixin {

    @Unique
    Blaze electro$blaze = (Blaze) (Object) this;

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"))
    public void EBWIZARDRY$blazeAiStepSound(Level instance, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch, boolean distanceDelay) {
        if (!(electro$blaze instanceof IceWraith)) {
            instance.playLocalSound(electro$blaze.getX() + (double) 0.5F, electro$blaze.getY() + (double) 0.5F, electro$blaze.getZ() + (double) 0.5F,
                    SoundEvents.BLAZE_BURN, electro$blaze.getSoundSource(), 1.0F + electro$blaze.getRandom().nextFloat(), electro$blaze.getRandom().nextFloat() * 0.7F + 0.3F, false);
        }
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    public void EBWIZARDRY$blazeAiStepParticle(Level instance, ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        if (!(electro$blaze instanceof IceWraith)) {
            instance.addParticle(ParticleTypes.LARGE_SMOKE, electro$blaze.getRandomX(0.5F), electro$blaze.getRandomY(),
                    electro$blaze.getRandomZ(0.5F), 0.0F, 0.0F, 0.0F);

        }
    }
}
