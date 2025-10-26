package com.electroblob.wizardry.core.mixin;

import com.electroblob.wizardry.api.content.data.ConjureData;
import com.electroblob.wizardry.api.content.util.DrawingUtils;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * We need to modify the way that the items load the durability bar, with this we check for the ConjureData
 * if the item isn't a conjure item it just continues with the normal behavior, if the item is a conjure item and is
 * summoned the mod will load the lifetime and the max lifetime.
 * */
@Mixin(ItemStack.class)
public class ConjureMixin {
    @Unique
    ItemStack stack = (ItemStack) (Object) this;

    @Inject(method = "getMaxDamage", at = @At("RETURN"), cancellable = true)
    public void EBWIZARDRY$wandGetMaxDamage(CallbackInfoReturnable<Integer> cir){
        ConjureData data = Services.OBJECT_DATA.getConjureData(stack);
        if(data == null || !data.isSummoned()) return;
        cir.setReturnValue(data.getMaxLifetime());
    }

    @Inject(method = "getBarWidth", at = @At("RETURN"), cancellable = true)
    public void EBWIZARDRY$getBarWidth(CallbackInfoReturnable<Integer> cir) {
        ConjureData data = Services.OBJECT_DATA.getConjureData(stack);
        if(data == null || !data.isSummoned()) return;
        cir.setReturnValue(Math.round(13.0f - (float)(data.getMaxLifetime() - data.getLifetime()) * 13.0f / data.getMaxLifetime()));
    }

    @Inject(method = "isBarVisible", at = @At("RETURN"), cancellable = true)
    public void EBWIZARDRY$isBarVisible(CallbackInfoReturnable<Boolean> cir) {
        ConjureData data = Services.OBJECT_DATA.getConjureData(stack);
        if(data == null || !data.isSummoned()) return;
        cir.setReturnValue(true);
    }

    @Inject(method = "getBarColor", at = @At("RETURN"), cancellable = true)
    public void getBarColor(CallbackInfoReturnable<Integer> cir) {
        ConjureData data = Services.OBJECT_DATA.getConjureData(stack);
        if(data == null || !data.isSummoned()) return;
        cir.setReturnValue(DrawingUtils.mix(0xff8bfe, 0x8e2ee4, (float) stack.getBarWidth()));
    }
}
