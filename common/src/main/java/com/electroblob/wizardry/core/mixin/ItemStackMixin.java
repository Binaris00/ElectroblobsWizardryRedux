package com.electroblob.wizardry.core.mixin;

import com.electroblob.wizardry.api.content.item.IManaStoringItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Unique
    ItemStack stack = (ItemStack) (Object) this;

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
            ordinal = 15, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void EBWIZARDRY$getTooltipLines(Player player, TooltipFlag isAdvanced, CallbackInfoReturnable<List<Component>> cir, List<Component> list) {
        if (stack.getItem() instanceof IManaStoringItem) {
            list.remove(list.size() - 1); // Removing "Durability %s/%s"
            list.add(Component.translatable("item.ebwizardry.wand.damage_desc", stack.getMaxDamage() - stack.getDamageValue(), stack.getMaxDamage()).withStyle(ChatFormatting.BLUE));
        }
    }
}
