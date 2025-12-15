package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.client.util.ClientUtils;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpellBookItem extends Item {
    public SpellBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        if (SpellUtil.getSpell(stack) == Spells.NONE) {
            SpellUtil.setSpell(stack, Spells.MAGIC_MISSILE);
            EBLogger.warn("SpellBookItem has no spell assigned to it! Defaulting to Magic Missile, where did this item come from?");
        }
        if (level.isClientSide) ClientUtils.openSpellBook(stack);
        return super.use(level, player, interactionHand);
    }

    @Override
    public void onCraftedBy(@NotNull ItemStack stack, @NotNull Level level, @NotNull Player player) {
        super.onCraftedBy(stack, level, player);
        Spell spell = SpellUtil.getSpell(stack);
        if (spell == Spells.NONE) SpellUtil.setSpell(stack, Spells.MAGIC_MISSILE);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return ClientUtils.getBookDisplayName(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipFlag) {
        if (level == null) return;
        Spell spell = SpellUtil.getSpell(stack);
        if (spell == Spells.NONE) return;
        boolean discovered = ClientUtils.shouldDisplayDiscovered(spell, stack);
        list.add(spell.getTier().getDescriptionFormatted());

        if (discovered && tooltipFlag.isAdvanced()) {
            list.add(Component.translatable(spell.getElement().getDescriptionId()).withStyle(ChatFormatting.GRAY));
            list.add(Component.translatable(spell.getType().getDisplayName()).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    public ResourceLocation getGuiTexture(Spell spell) {
        ResourceLocation l = spell.getTier().getOrCreateLocation();
        return new ResourceLocation(l.getNamespace(), "textures/gui/spell_book_" + l.getPath() + ".png");
    }
}
