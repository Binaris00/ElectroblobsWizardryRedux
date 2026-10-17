package com.binaris.wizardry.core.mixin.client;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.util.CastItemDataHelper;
import com.binaris.wizardry.api.content.util.RegistryUtils;
import com.binaris.wizardry.content.item.WandItem;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Removes the vanilla "use" animations from casting items: the hand raise started by
 * {@link ItemInHandRenderer#itemUsed(InteractionHand)} and the equip animation that plays when the server syncs
 * the wand's NBT (mana, cooldown, spell progression) and the client ends up with a new {@link ItemStack} instance.
 * Wands (and any item whose current spell has a {@link SpellAction}) already have their own casting pose, so the
 * vanilla bumps only get in the way.
 */
@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Shadow
    private ItemStack mainHandItem;

    @Shadow
    private ItemStack offHandItem;

    @Inject(method = "itemUsed", at = @At("HEAD"), cancellable = true)
    private void EBWIZARDRY$itemUsed(InteractionHand hand, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        if (EBWIZARDRY$hidesVanillaUseAnimation(minecraft.player.getItemInHand(hand))) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void EBWIZARDRY$keepCastingItemEquipped(CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        // When the server updates the wand's NBT, the client receives a new ItemStack instance. Vanilla treats that
        // content change as an item switch and plays the equip animation, which looks like a pop while casting.
        // Refresh the cached stacks when it is the same item so that only actual item switches animate.
        ItemStack mainHand = player.getMainHandItem();
        if (EBWIZARDRY$isUpdatedCastingItem(this.mainHandItem, mainHand)) {
            this.mainHandItem = mainHand;
        }

        ItemStack offHand = player.getOffhandItem();
        if (EBWIZARDRY$isUpdatedCastingItem(this.offHandItem, offHand)) {
            this.offHandItem = offHand;
        }
    }

    @Unique
    private static boolean EBWIZARDRY$isUpdatedCastingItem(ItemStack cached, ItemStack current) {
        return cached != null && !cached.isEmpty() && !current.isEmpty()
                && cached.getItem() == current.getItem()
                && EBWIZARDRY$hidesVanillaUseAnimation(current);
    }

    @Unique
    private static boolean EBWIZARDRY$hidesVanillaUseAnimation(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.getItem() instanceof WandItem) return true;

        Spell spell = RegistryUtils.getSpell(stack);
        if (spell == Spells.NONE) spell = CastItemDataHelper.getCurrentSpell(stack);

        if (spell == null || spell == Spells.NONE) return false;
        SpellAction action = spell.getAction();
        return action != null && action != SpellAction.NONE;
    }
}
