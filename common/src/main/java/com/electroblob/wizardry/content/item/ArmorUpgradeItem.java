package com.electroblob.wizardry.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// TODO AVOID BREAKING
public class ArmorUpgradeItem extends Item {
    public ArmorUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack stack) {
        return Rarity.EPIC;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag isAdvanced) {
        tooltip.add(Component.translatable(getOrCreateDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
    }
}
