package com.electroblob.wizardry.content.spell_tier;

import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.util.RegistryUtils;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.setup.registries.Elements;
import net.minecraft.ChatFormatting;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class NoviceTier extends SpellTier {
    public NoviceTier() {
        super(700, 3, 12, 0, ChatFormatting.WHITE, 100);
    }

    @Override
    public ItemStack getTradeItem(Element element, RandomSource random, ArrayList<Spell> spells, ArrayList<Spell> specializedSpells) {
        super.getTradeItem(element, random, spells, specializedSpells);
        boolean prob = element != Elements.MAGIC && random.nextInt(4) > 0;

        // 80% chance to return a spell book if possible (60% for specialized, 20% for general), 20% chance to return a wand
        if (random.nextInt(5) < 4 && !spells.isEmpty()) {
            ArrayList<Spell> spellList = prob && !specializedSpells.isEmpty()
                    ? specializedSpells : spells;
            return SpellUtil.spellBookItem(spellList.get(random.nextInt(spellList.size())));
        } else {
            Element wandElement = prob ? element : SpellUtil.getRandomElement(random);
            return new ItemStack(RegistryUtils.getWand(this, wandElement));
        }
    }
}
