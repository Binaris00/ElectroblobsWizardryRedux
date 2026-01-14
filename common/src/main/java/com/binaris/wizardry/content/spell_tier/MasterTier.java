package com.binaris.wizardry.content.spell_tier;

import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.api.content.util.SpellUtil;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import net.minecraft.ChatFormatting;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

// For non-magic wizards: 62.5% spell book (specialized only), 12.5% wand, 25% arcane tome
// For magic wizards: 0% spell book, 33.33% master wand, 66.67% arcane tome
public class MasterTier extends SpellTier {
    public MasterTier() {
        super(6000, 9, 1, 3, ChatFormatting.DARK_PURPLE, 400);
    }

    @Override
    public ItemStack getTradeItem(Element element, RandomSource random, ArrayList<Spell> spells, ArrayList<Spell> specializedSpells) {
        super.getTradeItem(element, random, spells, specializedSpells);

        int randomizer = element != Elements.MAGIC ? random.nextInt(8) : 5 + random.nextInt(3);

        // 62.5% chance for specialized spell book (only for non-magic wizards)
        if (randomizer < 5 && element != Elements.MAGIC && !specializedSpells.isEmpty()) {
            return SpellUtil.spellBookItem(specializedSpells.get(random.nextInt(specializedSpells.size())));
        }

        // 12.5% chance for wand (non-magic) or 33.33% (magic)
        // 75% chance for specialized wand, 25% for master wand (non-magic)
        // 100% master wand (magic wizards)
        if (randomizer < 6) {
            if (element != Elements.MAGIC && random.nextInt(4) > 0) return SpellUtil.wandItem(this, element);
            else return new ItemStack(EBItems.MASTER_WAND.get());
        }

        // 25% chance for arcane tome (non-magic) or 66.67% (magic) - tier 3
        return SpellUtil.arcaneTomeItem(this);

    }
}

