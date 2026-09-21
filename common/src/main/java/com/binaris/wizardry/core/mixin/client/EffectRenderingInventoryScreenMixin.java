package com.binaris.wizardry.core.mixin.client;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.effect.CurseMobEffect;
import com.binaris.wizardry.api.content.effect.MagicMobEffectInstance;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EffectRenderingInventoryScreen.class)
public class EffectRenderingInventoryScreenMixin {
    @ModifyArg(method = "renderBackgrounds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V", ordinal = 0))
    private ResourceLocation EBWIZARDRY$customRenderEffects(ResourceLocation atlasLocation, @Local MobEffectInstance mobeffectinstance) {
        if (mobeffectinstance instanceof MagicMobEffectInstance) {
            return WizardryMainMod.location("textures/gui/effect/mobeffect_curse_blue.png");
        }
        if (mobeffectinstance.getEffect() instanceof CurseMobEffect) {
            return WizardryMainMod.location("textures/gui/effect/mobeffect_curse_red.png");
        }
        return atlasLocation;
    }

    @ModifyArg(method = "renderBackgrounds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V", ordinal = 1))
    private ResourceLocation EBWIZARDRY$customRenderEffects2(ResourceLocation atlasLocation, @Local MobEffectInstance mobeffectinstance) {
        if (mobeffectinstance instanceof MagicMobEffectInstance) {
            return WizardryMainMod.location("textures/gui/effect/mobeffect_curse_blue.png");
        }
        if (mobeffectinstance.getEffect() instanceof CurseMobEffect) {
            return WizardryMainMod.location("textures/gui/effect/mobeffect_curse_red.png");
        }
        return atlasLocation;
    }
}
