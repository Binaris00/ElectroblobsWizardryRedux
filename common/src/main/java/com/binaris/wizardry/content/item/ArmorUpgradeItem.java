package com.binaris.wizardry.content.item;

import com.binaris.wizardry.content.item.armor.IArmorUpgrade;
import com.binaris.wizardry.content.item.armor.WizardArmorMaterial;
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

public class ArmorUpgradeItem extends Item implements IArmorUpgrade {
    private final WizardArmorMaterial material;

    public ArmorUpgradeItem(Properties properties, WizardArmorMaterial material) {
        super(properties);
        this.material = material;
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
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag tooltipFlag) {
        String desc = tooltipFlag.isAdvanced() ? ".desc_extended" : ".desc";

        tooltip.add(Component.translatable(getOrCreateDescriptionId() + desc).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public WizardArmorMaterial getWizardArmorMaterial() {
        return material;
    }
}
