package com.binaris.wizardry.core.mixin;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.RegistryUtils;
import com.binaris.wizardry.api.content.util.CastItemDataHelper;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin is used to inject code into the HumanoidModel class to render {@link  com.binaris.wizardry.api.content.spell.SpellAction}.
 * With this logic addons or any addition to spell actions doesn't need to interact or register anything with the game.
 */
@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T> {
    @Unique
    HumanoidModel<?> model = ((HumanoidModel<?>) (Object) this);

    @Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
    public void EBWIZARDRYinjectPoseRightArm(LivingEntity livingEntity, CallbackInfo ci) {
        ItemStack stack = livingEntity.getItemInHand(InteractionHand.MAIN_HAND);
        Spell spell;
        if (!(livingEntity instanceof Player)) return;
        if (RegistryUtils.getSpell(stack) != Spells.NONE) {
            spell = RegistryUtils.getSpell(stack);
        } else {
            spell = CastItemDataHelper.getCurrentSpell(stack);
        }

        if (spell != Spells.NONE && spell.getAction().shouldRender(livingEntity, spell, stack, InteractionHand.MAIN_HAND)) {
            spell.getAction().renderArms(livingEntity, model, InteractionHand.MAIN_HAND);
            ci.cancel();
        }
    }

    @Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
    public void EBWIZARDRYinjectPoseLeftArm(LivingEntity livingEntity, CallbackInfo ci) {
        ItemStack stack = livingEntity.getItemInHand(InteractionHand.OFF_HAND);
        Spell spell;
        if (!(livingEntity instanceof Player)) return;

        if (RegistryUtils.getSpell(stack) != Spells.NONE) {
            spell = RegistryUtils.getSpell(stack);
        } else {
            spell = CastItemDataHelper.getCurrentSpell(stack);
        }

        if (spell != Spells.NONE && spell.getAction().shouldRender(livingEntity, spell, stack, InteractionHand.OFF_HAND)) {
            spell.getAction().renderArms(livingEntity, model, InteractionHand.OFF_HAND);
            ci.cancel();
        }
    }
}
