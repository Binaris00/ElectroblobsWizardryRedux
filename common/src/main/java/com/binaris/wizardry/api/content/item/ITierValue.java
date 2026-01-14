package com.binaris.wizardry.api.content.item;

import com.binaris.wizardry.api.content.spell.SpellTier;
import net.minecraft.world.item.ItemStack;

/**
 * Interface for items that have an associated tier value. This is typically used for items that can store or
 * manipulate tiered things, like spells, wands, and other magical items.
 */
public interface ITierValue {
    /**
     * Gets the tier associated with this item.
     *
     * @return The tier of the item.
     */
    SpellTier getTier(ItemStack stack);
}
