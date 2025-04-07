package com.electroblob.wizardry.api.content.item;

import com.electroblob.wizardry.api.content.spell.Spell;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public interface ISpellCastingItem {
    @Nonnull
    Spell getCurrentSpell(ItemStack stack);
}
