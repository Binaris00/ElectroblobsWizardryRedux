package com.binaris.wizardry.content.item;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

/// A singleton {@code Tier} used by magic axe items (Frost Axe, Flaming Axe), providing
/// diamond-level mining, high efficiency, and no repair ingredient.
public class MagicItemTier implements Tier {
    public static final Tier TIER = new MagicItemTier();

    @Override
    public int getUses() {
        return 100;
    }

    @Override
    public float getSpeed() {
        return 8.0F;
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
    }

    @Override
    public int getLevel() {
        return 3;
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public float getAttackDamageBonus() {
        return 4.0F;
    }
}
