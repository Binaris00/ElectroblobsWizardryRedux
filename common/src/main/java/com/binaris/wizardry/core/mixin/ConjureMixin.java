package com.binaris.wizardry.core.mixin;

import com.binaris.wizardry.api.content.data.ConjureData;
import com.binaris.wizardry.api.content.util.DrawingUtils;
import com.binaris.wizardry.core.platform.Services;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * We need to modify the way that the items load the durability bar, with this we check for the ConjureData
 * if the item isn't a conjure item it just continues with the normal behavior, if the item is a conjure item and is
 * summoned the mod will load the remaining lifetime based on gameTime and expireTime.
 */
@Mixin(ItemStack.class)
public class ConjureMixin {
    @Unique
    ItemStack stack = (ItemStack) (Object) this;

    // All the conjure items that are summoned will always show the durability bar regardless of their durability.
    @Inject(method = "isBarVisible", at = @At("RETURN"), cancellable = true)
    public void EBWIZARDRY$conjureIsBarVisible(CallbackInfoReturnable<Boolean> cir) {
        ConjureData data = Services.OBJECT_DATA.getConjureData(stack);
        if (data == null || !data.isSummoned()) return;
        cir.setReturnValue(true);
    }

    // Changes the color of the durability bar to a purple gradient for conjured items.
    @Inject(method = "getBarColor", at = @At("RETURN"), cancellable = true)
    public void EBWIZARDRY$conjureGetBarColor(CallbackInfoReturnable<Integer> cir) {
        ConjureData data = Services.OBJECT_DATA.getConjureData(stack);
        if (data == null || !data.isSummoned()) return;
        cir.setReturnValue(DrawingUtils.mix(0xff8bfe, 0x8e2ee4, (float) stack.getBarWidth()));
    }

    @Inject(method = "getTooltipLines", at = @At("RETURN"))
    public void EBWIZARDRY$conjureGetTooltipLines(Player player, TooltipFlag isAdvanced, CallbackInfoReturnable<List<Component>> cir, @Local(name = "list") List<Component> list) {
        ConjureData data = Services.OBJECT_DATA.getConjureData(stack);
        if (data == null || !data.isSummoned()) return;

        long currentGameTime = player.level().getGameTime();
        int remaining = data.getRemainingLifetime(currentGameTime);

        list.add(Component.translatable("tooltip.ebwizardry.conjured_item",
                remaining / 20).withStyle(ChatFormatting.DARK_PURPLE));
    }

    // This is the important one - prevents damage being set on conjured items, avoiding items to continue using the
    // durability tags and making inconsistent behaviour with the conjure system.
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void EBWIZARDRY$preventDurabilityLoss(int amount, RandomSource random, ServerPlayer user, CallbackInfoReturnable<Boolean> cir) {
        ConjureData data = Services.OBJECT_DATA.getConjureData(stack);
        if (data != null && data.isSummoned()) {
            cir.setReturnValue(false);
        }
    }

    // Prevents conjured items from being considered "damaged" (i.e. broken) by the vanilla system.
    @Inject(method = "isDamaged", at = @At("RETURN"), cancellable = true)
    public void EBWIZARDRY$preventBroken(CallbackInfoReturnable<Boolean> cir) {
        ConjureData data = Services.OBJECT_DATA.getConjureData(stack);
        if (data != null && data.isSummoned()) {
            cir.setReturnValue(false);
        }
    }
}
