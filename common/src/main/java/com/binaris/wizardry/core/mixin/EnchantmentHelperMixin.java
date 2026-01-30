package com.binaris.wizardry.core.mixin;

import com.binaris.wizardry.content.item.FlamingAxeItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(method = "getFireAspect", at = @At("HEAD"), cancellable = true)
    private static void EBWIZARDRY$getFireAspect(LivingEntity player, CallbackInfoReturnable<Integer> cir){
        if (player.getOffhandItem().getItem() instanceof FlamingAxeItem || player.getMainHandItem().getItem() instanceof FlamingAxeItem) {
            cir.setReturnValue(1);
        }
    }
}
