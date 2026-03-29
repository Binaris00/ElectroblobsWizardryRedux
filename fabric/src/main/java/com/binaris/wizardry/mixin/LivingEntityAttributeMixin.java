package com.binaris.wizardry.mixin;

import com.binaris.wizardry.setup.registries.EBAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityAttributeMixin {

    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void EBWIZARDRY$livingCreateAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir){
        EBAttributes.getAttributes().forEach(a -> cir.getReturnValue().add(a.get()));
    }
}
