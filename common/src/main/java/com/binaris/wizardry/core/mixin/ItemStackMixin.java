package com.binaris.wizardry.core.mixin;

import com.binaris.wizardry.api.content.item.IManaStoringItem;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.SpellUtil;
import com.binaris.wizardry.client.NotImplementedItems;
import com.binaris.wizardry.content.item.ScrollItem;
import com.binaris.wizardry.content.item.SpellBookItem;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.Spells;
import com.llamalad7.mixinextras.sugar.Local;
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

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Unique
    ItemStack stack = (ItemStack) (Object) this;

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
            ordinal = 15, shift = At.Shift.AFTER))
    public void EBWIZARDRY$getTooltipLinesMana(Player player, TooltipFlag isAdvanced, CallbackInfoReturnable<List<Component>> cir, @Local(name = "list") List<Component> list) {
        if (stack.getItem() instanceof IManaStoringItem) {
            list.remove(list.size() - 1); // Removing "Durability %s/%s"
            list.add(Component.translatable("item.ebwizardry.wand.damage_desc", stack.getMaxDamage() - stack.getDamageValue(), stack.getMaxDamage()).withStyle(ChatFormatting.BLUE));
        }
    }

    @Inject(method = "getTooltipLines", at = @At("RETURN"))
    public void EBWIZARDRY$getTooltipLinesEvent(Player player, TooltipFlag isAdvanced, CallbackInfoReturnable<List<Component>> cir){
        if (NotImplementedItems.notImplemented(stack.getItem())) {
            cir.getReturnValue().add(Component.literal("Not Implemented").withStyle(ChatFormatting.RED));
        }

        if (stack.getItem() instanceof SpellBookItem || stack.getItem() instanceof ScrollItem) {
            Spell spell = SpellUtil.getSpell(stack);
            if (spell != Spells.NONE && spell.property(DefaultProperties.SENSIBLE)) {
                cir.getReturnValue().add(Component.literal("Sensible Spell (Only for testing)").withStyle(ChatFormatting.RED));
            }
        }
    }
}
