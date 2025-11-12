package com.electroblob.wizardry.content.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import org.jetbrains.annotations.NotNull;

public class SpectralPickaxeItem extends PickaxeItem {
    public SpectralPickaxeItem() {
        super(Tiers.IRON, 1, -2.8F, new Properties().durability(1200).rarity(Rarity.UNCOMMON));
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack stack, @NotNull ItemStack stack1) {
        return false;
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }
}