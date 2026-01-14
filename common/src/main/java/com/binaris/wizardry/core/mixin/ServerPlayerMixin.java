package com.binaris.wizardry.core.mixin;

import com.binaris.wizardry.api.content.event.EBItemTossEvent;
import com.binaris.wizardry.core.event.WizardryEventBus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(method = "drop*", at = @At("HEAD"), cancellable = true)
    public void EBWIZARDRY$drop(ItemStack itemStack, boolean bl, boolean bl2, CallbackInfoReturnable<ItemEntity> cir) {
        boolean result = WizardryEventBus.getInstance().fire(new EBItemTossEvent((Player) (Object) this, itemStack));
        if (result) cir.cancel();
    }
}
