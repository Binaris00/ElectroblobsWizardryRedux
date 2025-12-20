package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.api.content.item.IManaStoringItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CrystalItem extends Item implements IManaStoringItem {
    private final int manaCapacity;

    public CrystalItem(int manaCapacity) {
        super(new Properties());
        this.manaCapacity = manaCapacity;
    }

    @Override
    public int getMana(ItemStack stack) {
        return manaCapacity;
    }

    @Override
    public void setMana(ItemStack stack, int mana) {
        // You can't set mana on a crystal
    }

    @Override
    public int getManaCapacity(ItemStack stack) {
        return manaCapacity;
    }

    @Override
    public boolean showManaInWorkbench(Player player, ItemStack stack) {
        return false;
    }

    @Override
    public void consumeMana(ItemStack stack, int mana, @Nullable LivingEntity wielder) {
        // Crystals don't store mana, like wands and armors
    }
}
