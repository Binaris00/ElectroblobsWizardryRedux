package com.electroblob.wizardry.core.mixin;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.PlayerWizardData;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public final class ClientPlayerMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    public void EBWIZARDRY$tick(CallbackInfo ci){
//        if(!WizardryMainMod.isFabric()) return;
//
//        Player player = (Player) (Object) this;
//
//        PlayerWizardData wizardData = Services.WIZARD_DATA.getWizardData(player, player.level());
//        wizardData.updateContinuousSpellCasting(player);
//        wizardData.updateImbuedItems(player);
//
//        wizardData.getSpellData().forEach((k, v) -> wizardData.getSpellData().put(k, k.update(player, v)));
//        wizardData.getSpellData().keySet().removeIf(k -> k.canPurge(player, wizardData.getSpellData().get(k)));
//
//        Services.WIZARD_DATA.onUpdate(wizardData, player);
    }
}
