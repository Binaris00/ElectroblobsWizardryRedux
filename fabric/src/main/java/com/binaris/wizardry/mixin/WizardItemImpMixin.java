package com.binaris.wizardry.mixin;

import com.binaris.wizardry.api.content.item.IWizardryItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class WizardItemImpMixin {
    @Unique
    ItemStack stack = (ItemStack) (Object) this;

    @Inject(method = "getMaxDamage", at = @At("RETURN"), cancellable = true)
    public void EBWIZARDRY$wandGetMaxDamage(CallbackInfoReturnable<Integer> cir) {
        if (!(stack.getItem() instanceof IWizardryItem wizardryItem)) return;
        int maxDamage = wizardryItem.getMaxDamage(stack);
        cir.setReturnValue(maxDamage);
    }

    @Inject(method = "setDamageValue", at = @At("TAIL"), cancellable = true)
    public void EBWIZARDRY$wandGetDamage(int damage, CallbackInfo ci) {
        if (!(stack.getItem() instanceof IWizardryItem wizardryItem)) return;
        wizardryItem.setDamage(stack, damage);
        ci.cancel();
    }
}
