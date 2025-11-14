package com.electroblob.wizardry.core.mixin;

import com.electroblob.wizardry.content.entity.living.IceWraith;
import net.minecraft.world.entity.monster.Blaze;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Blaze.class)
public abstract class BlazeMixin {

    @Unique
    Blaze ebwizardry$blaze = (Blaze)(Object)this;

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"), cancellable = true)
    public void EBWIZARDRY$blazeAiStepSound(CallbackInfo ci){
        if(ebwizardry$blaze instanceof IceWraith) ci.cancel();
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"), cancellable = true)
    public void EBWIZARDRY$blazeAiStepParticle(CallbackInfo ci){
        if(ebwizardry$blaze instanceof IceWraith) ci.cancel();
    }
}
