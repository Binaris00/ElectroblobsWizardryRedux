package com.electroblob.wizardry.core.mixin.client;

import com.electroblob.wizardry.content.item.WandItem;
import com.electroblob.wizardry.core.integrations.accessories.EBAccessoriesIntegration;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.EBMobEffects;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Shadow
    public Input input;
    @Unique
    LocalPlayer player = (LocalPlayer)(Object)this;

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/Tutorial;onInput(Lnet/minecraft/client/player/Input;)V"))
    public void ebwizardry$localAIStep(CallbackInfo ci) {
        if (player.hasEffect(EBMobEffects.PARALYSIS.get())) {
            input.forwardImpulse = 0;
            input.leftImpulse = 0;
            input.jumping = false;
            input.shiftKeyDown = false;
        }

        if (EBAccessoriesIntegration.isEquipped(player, EBItems.CHARM_MOVE_SPEED.get()) && player.isUsingItem() && player.getUseItem().getItem() instanceof WandItem) {
            input.leftImpulse *= 4;
            input.forwardImpulse *= 4;
        }
    }

}
