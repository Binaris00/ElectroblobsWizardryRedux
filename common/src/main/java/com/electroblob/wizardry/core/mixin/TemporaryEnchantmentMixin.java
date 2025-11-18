package com.electroblob.wizardry.core.mixin;

import com.electroblob.wizardry.api.content.data.TemporaryEnchantmentData;
import com.electroblob.wizardry.api.content.util.DrawingUtils;
import com.electroblob.wizardry.core.platform.Services;
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

/**
 * Mixin to add visual feedback for items with temporary enchantments.
 * Similar to {@link ConjureMixin} but for temporary enchantments.
 * <p>
 * This mixin modifies ItemStack to:
 * <ul>
 *   <li>Show a durability bar for items with temporary enchantments</li>
 *   <li>Change the bar color to a blue gradient</li>
 *   <li>Add a tooltip showing remaining time</li>
 * </ul>
 */
@Mixin(ItemStack.class)
public class TemporaryEnchantmentMixin {
    @Unique
    ItemStack stack = (ItemStack) (Object) this;

    // Show the durability bar for all items with temporary enchantments
    @Inject(method = "isBarVisible", at = @At("RETURN"), cancellable = true)
    public void EBWIZARDRY$tempEnchantIsBarVisible(CallbackInfoReturnable<Boolean> cir) {
        TemporaryEnchantmentData data = Services.OBJECT_DATA.getTemporaryEnchantmentData(stack);
        if (data == null || !data.hasTemporaryEnchantment()) return;
        cir.setReturnValue(true);
    }

    // Changes the color of the durability bar to a blue gradient for temporarily enchanted items.
    @Inject(method = "getBarColor", at = @At("RETURN"), cancellable = true)
    public void EBWIZARDRY$tempEnchantGetBarColor(CallbackInfoReturnable<Integer> cir) {
        TemporaryEnchantmentData data = Services.OBJECT_DATA.getTemporaryEnchantmentData(stack);
        if (data == null || !data.hasTemporaryEnchantment()) return;
        // Use a blue/cyan gradient to distinguish from purple conjured items
        cir.setReturnValue(DrawingUtils.mix(0x00d4ff, 0x0066cc, (float) stack.getBarWidth()));
    }

    @Inject(method = "getTooltipLines", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void EBWIZARDDRY$tempEnchantGetTooltipLines(Player player, TooltipFlag isAdvanced, CallbackInfoReturnable<List<Component>> cir, List<Component> list) {
        TemporaryEnchantmentData data = Services.OBJECT_DATA.getTemporaryEnchantmentData(stack);
        if (data == null || !data.hasTemporaryEnchantment()) return;

        long currentGameTime = player.level().getGameTime();
        int remaining = data.getRemainingLifetime(currentGameTime);

        list.add(Component.translatable("tooltip.ebwizardry.temporary_enchantment",
                remaining / 20).withStyle(ChatFormatting.AQUA));
    }
}
