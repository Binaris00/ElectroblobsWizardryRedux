package com.electroblob.wizardry.content.spell_tier;

import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.api.content.util.WandHelper;
import com.electroblob.wizardry.setup.registries.Elements;
import net.minecraft.ChatFormatting;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

// Probabilities: 41.67% spell book, 8.33% wand, 16.67% arcane tome, 33.33% special upgrades
public class AdvancedTier extends SpellTier {
    public AdvancedTier() {
        super(3500, 7, 2, 2, ChatFormatting.DARK_BLUE, 300);
    }

    @Override
    public ItemStack getTradeItem(Element element, RandomSource random, ArrayList<Spell> spells, ArrayList<Spell> specializedSpells) {
        super.getTradeItem(element, random, spells, specializedSpells);
        boolean prob = element != Elements.MAGIC && random.nextInt(4) > 0;

        int randomizer = random.nextInt(12);

        // 41.67% chance for spell book (31.25% specialized, 10.42% general)
        if (randomizer < 5 && !spells.isEmpty()) {
            ArrayList<Spell> spellList = prob && !specializedSpells.isEmpty() ? specializedSpells : spells;
            return SpellUtil.spellBookItem(spellList.get(random.nextInt(spellList.size())));
        }

        // 8.33% chance for wand (6.25% specialized, 2.08% random element)
        if (randomizer < 6) {
            Element wandElement = prob ? element : SpellUtil.getRandomElement(random);
            return SpellUtil.wandItem(this, wandElement);
        }

        // 16.67% chance for arcane tome (tier 2)
        if (randomizer < 8) return SpellUtil.arcaneTomeItem(this);


        // 33.33% chance for special upgrades (random)
        List<DeferredObject<Item>> upgrades = new ArrayList<>(WandHelper.getSpecialUpgrades());
        return new ItemStack(upgrades.get(random.nextInt(upgrades.size())).get());

    }
}
