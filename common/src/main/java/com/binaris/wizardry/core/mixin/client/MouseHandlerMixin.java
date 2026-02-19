package com.binaris.wizardry.core.mixin.client;

import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.core.OLDEBCONFIG;
import com.binaris.wizardry.setup.registries.client.EBKeyBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

    @Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"), cancellable = true)
    public void EBWIZARDRY$onScroll(long windowPointer, double xOffset, double yOffset, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        ItemStack wand = EntityUtil.getWandInUse(player);
        if (wand == null) return;

        if (Minecraft.getInstance().mouseHandler.isMouseGrabbed() && !wand.isEmpty() && yOffset != 0 && player.isShiftKeyDown()) {
            ci.cancel();
            int d = (int) (OLDEBCONFIG.reverseScrollDirection ? -yOffset : yOffset);

            if (d > 0) {
                EBKeyBinding.selectNextSpell(wand);
            } else if (d < 0) {
                EBKeyBinding.selectPreviousSpell(wand);
            }
        }
    }
}
