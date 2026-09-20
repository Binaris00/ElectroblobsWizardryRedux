package com.binaris.wizardry.core.mixin.client;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.effect.CurseMobEffect;
import com.binaris.wizardry.api.content.effect.MagicMobEffectInstance;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Redirect(method = "renderEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V", ordinal = 0))
    public void EBWIZARDRY$customRenderEffects(GuiGraphics instance, ResourceLocation atlasLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight, @Local MobEffectInstance mobeffectinstance){
        if (mobeffectinstance instanceof MagicMobEffectInstance) {
            instance.blit(WizardryMainMod.location("textures/gui/effect/mobeffect_curse_blue.png"), x, y, uOffset, vOffset, uWidth, vHeight);
            return;
        }
        if (mobeffectinstance.getEffect() instanceof CurseMobEffect) {
            instance.blit(WizardryMainMod.location("textures/gui/effect/mobeffect_curse_red.png"), x, y, uOffset, vOffset, uWidth, vHeight);
            return;
        }
        instance.blit(atlasLocation, x, y, uOffset, vOffset, uWidth, vHeight);
    }

    @Redirect(method = "renderEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V", ordinal = 1))
    public void EBWIZARDRY$customRenderEffects2(GuiGraphics instance, ResourceLocation atlasLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight, @Local MobEffectInstance mobeffectinstance){
        if (mobeffectinstance instanceof MagicMobEffectInstance) {
            instance.blit(WizardryMainMod.location("textures/gui/effect/mobeffect_curse_blue.png"), x, y, uOffset, vOffset, uWidth, vHeight);
            return;
        }
        if (mobeffectinstance.getEffect() instanceof CurseMobEffect) {
            instance.blit(WizardryMainMod.location("textures/gui/effect/mobeffect_curse_red.png"), x, y, uOffset, vOffset, uWidth, vHeight);
            return;
        }
        instance.blit(atlasLocation, x, y, uOffset, vOffset, uWidth, vHeight);
    }
}