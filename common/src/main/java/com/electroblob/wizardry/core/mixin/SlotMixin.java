package com.electroblob.wizardry.core.mixin;

import com.electroblob.wizardry.api.content.event.EBItemPlaceInContainerEvent;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin {
    @Shadow
    @Final
    public Container container;

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    public void EBWIZARDRY$preventConjureItemInContainer(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (WizardryEventBus.getInstance().fire(new EBItemPlaceInContainerEvent(stack, this.container)))
            cir.setReturnValue(false);
    }
}
