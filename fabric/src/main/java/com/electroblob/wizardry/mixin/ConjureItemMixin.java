package com.electroblob.wizardry.mixin;

import com.electroblob.wizardry.api.ConjureItemData;
import com.electroblob.wizardry.api.content.item.IWizardryItem;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ConjureItemMixin {
    @Unique
    ItemStack stack = (ItemStack) (Object) this;

    @Inject(method = "getMaxDamage", at = @At("RETURN"), cancellable = true)
    public void EBWIZARDRY$wandGetMaxDamage(CallbackInfoReturnable<Integer> cir){
        ConjureItemData data = Services.WIZARD_DATA.getConjureItemData(stack);
        if(data == null || !data.isSummoned()) return;
        cir.setReturnValue(data.getMaxLifetime());
    }

    @Inject(method = "getBarWidth", at = @At("RETURN"), cancellable = true)
    public void EBWIZARDRY$getBarWidth(CallbackInfoReturnable<Integer> cir) {
        ConjureItemData data = Services.WIZARD_DATA.getConjureItemData(stack);
        if(data == null || !data.isSummoned()) return;
        cir.setReturnValue(Math.round(13.0f - (float)stack.getDamageValue() * 13.0f / data.getMaxLifetime()));
    }

    @Inject(method = "isBarVisible", at = @At("RETURN"), cancellable = true)
    public void EBWIZARDRY$isBarVisible(CallbackInfoReturnable<Boolean> cir) {
        ConjureItemData data = Services.WIZARD_DATA.getConjureItemData(stack);
        if(data == null || !data.isSummoned()) return;
        cir.setReturnValue(true);
    }

    @Inject(method = "setDamageValue", at = @At("TAIL"), cancellable = true)
    public void EBWIZARDRY$wandGetMaxDamage(int damage, CallbackInfo ci){
        if(!(stack.getItem() instanceof IWizardryItem wizardryItem)) return;
        wizardryItem.setDamage(stack, damage);
        ci.cancel();
    }
}
