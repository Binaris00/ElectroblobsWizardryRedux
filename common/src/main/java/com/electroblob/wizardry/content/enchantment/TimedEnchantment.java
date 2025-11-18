package com.electroblob.wizardry.content.enchantment;

import com.electroblob.wizardry.api.content.enchantment.Imbuement;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * @deprecated Use the new temporary enchantment system instead.
 * See {@link com.electroblob.wizardry.api.content.data.SpellManagerData#setTemporaryEnchantment}
 * and {@link com.electroblob.wizardry.api.content.util.TemporaryEnchantmentLoader}.
 * <p>
 * The old imbuement system required custom enchantment classes. The new system allows
 * using ANY enchantment (vanilla or modded) as a temporary effect.
 * <p>
 * This class is kept for backwards compatibility.
 */
@Deprecated(since = "1.0.0-dev11", forRemoval = false)
public class TimedEnchantment extends Enchantment implements Imbuement {
    public TimedEnchantment() {
        super(Rarity.COMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return false;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }
}
