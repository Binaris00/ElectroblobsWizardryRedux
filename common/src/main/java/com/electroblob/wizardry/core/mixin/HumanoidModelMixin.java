package com.electroblob.wizardry.core.mixin;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.api.content.util.WandHelper;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T> {
    @Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
    public void EBWIZARDRYinjectPoseRightArm(LivingEntity livingEntity, CallbackInfo ci){
        HumanoidModel<?> model = ((HumanoidModel<?>) (Object) this);
        ItemStack stack = livingEntity.getItemInHand(InteractionHand.MAIN_HAND);
        Spell spell;
        if(SpellUtil.getSpell(stack) != Spells.NONE && SpellUtil.getSpell(stack) != null){
            spell = SpellUtil.getSpell(stack);
        } else {
            spell = WandHelper.getCurrentSpell(stack);
        }

        if((spell != null && spell != Spells.NONE) && spell.getAction().shouldRender(livingEntity, spell, stack, InteractionHand.MAIN_HAND)){
            spell.getAction().renderArms(livingEntity, model, InteractionHand.MAIN_HAND);
            ci.cancel();
        }
    }

    @Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
    public void EBWIZARDRYinjectPoseLeftArm(LivingEntity livingEntity, CallbackInfo ci){
        HumanoidModel<?> model = ((HumanoidModel<?>) (Object) this);
        ItemStack stack = livingEntity.getItemInHand(InteractionHand.OFF_HAND);
        Spell spell;
        if(SpellUtil.getSpell(stack) != Spells.NONE && SpellUtil.getSpell(stack) != null){
            spell = SpellUtil.getSpell(stack);
        } else {
            spell = WandHelper.getCurrentSpell(stack);
        }

        if((spell != null && spell != Spells.NONE) && spell.getAction().shouldRender(livingEntity, spell, stack, InteractionHand.OFF_HAND)){
            spell.getAction().renderArms(livingEntity, model, InteractionHand.OFF_HAND);
            ci.cancel();
        }
    }
}
