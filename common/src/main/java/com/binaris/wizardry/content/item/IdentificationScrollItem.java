package com.binaris.wizardry.content.item;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.data.SpellManagerData;
import com.binaris.wizardry.api.content.event.EBDiscoverSpellEvent;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.InventoryUtil;
import com.binaris.wizardry.api.content.util.SpellUtil;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IdentificationScrollItem extends Item {
    public IdentificationScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(player);

        for (ItemStack stack1 : InventoryUtil.getHotBarAndOffhand(player)) {
            if (stack1.isEmpty()) continue;
            Spell spell = SpellUtil.getSpell(stack1);
            if (stack1.getItem() instanceof IdentificationScrollItem || spell == Spells.NONE) continue;

            if ((stack1.getItem() instanceof SpellBookItem || stack1.getItem() instanceof ScrollItem) && !data.hasSpellBeenDiscovered(spell)) {
                if (WizardryEventBus.getInstance().fire(new EBDiscoverSpellEvent(player, spell, EBDiscoverSpellEvent.Source.IDENTIFICATION_SCROLL)))
                    return InteractionResultHolder.fail(stack);

                if (!level.isClientSide) {
                    data.discoverSpell(spell);
                    player.sendSystemMessage(Component.translatable("spell.discover", spell.getDescriptionFormatted()));
                }

                player.playSound(EBSounds.MISC_DISCOVER_SPELL.get(), 1.25f, 1);
                if (!player.isCreative()) stack.shrink(1);
                player.getCooldowns().addCooldown(stack.getItem(), 60);
                return InteractionResultHolder.success(stack);
            }
        }

        player.getCooldowns().addCooldown(stack.getItem(), 60);
        if (!level.isClientSide)
            player.sendSystemMessage(Component.translatable("item." + WizardryMainMod.MOD_ID + ".identification_scroll.nothing_to_identify"));

        return InteractionResultHolder.fail(stack);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack stack) {
        return Rarity.UNCOMMON;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        String desc = flag.isAdvanced() ? ".desc_extended" : ".desc";
        tooltip.add(Component.translatable(getOrCreateDescriptionId() + desc).withStyle(ChatFormatting.GRAY));
    }
}
