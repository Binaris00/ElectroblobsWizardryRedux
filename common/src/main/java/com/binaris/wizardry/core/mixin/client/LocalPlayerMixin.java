package com.binaris.wizardry.core.mixin.client;

import com.binaris.wizardry.api.content.item.ICastItem;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.CastItemUtils;
import com.binaris.wizardry.core.integrations.ArtifactChannel;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.EBMobEffects;
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
    LocalPlayer player = (LocalPlayer) (Object) this;

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/Tutorial;onInput(Lnet/minecraft/client/player/Input;)V"))
    public void ebwizardry$localAIStep(CallbackInfo ci) {
        if (player.hasEffect(EBMobEffects.PARALYSIS.get())) {
            input.forwardImpulse = 0;
            input.leftImpulse = 0;
            input.jumping = false;
            input.shiftKeyDown = false;
        }

        if (ArtifactChannel.isEquipped(player, EBItems.CHARM_MOVE_SPEED.get()) && player.isUsingItem()
                && player.getUseItem().getItem() instanceof ICastItem castItem) {
            // Instant spells keep the use state alive to show the spell action pose, so only apply the charm
            // while the spell is actually charging; otherwise just holding the pose would grant the speed bonus.
            Spell spell = castItem.getCurrentSpell(player.getUseItem());
            boolean charging = !spell.isInstantCast()
                    || player.getTicksUsingItem() < CastItemUtils.calcCharge(spell, Services.OBJECT_DATA.getWizardData(player).getSpellModifiers());

            if (charging) {
                input.leftImpulse *= 4;
                input.forwardImpulse *= 4;
            }
        }
    }

}
