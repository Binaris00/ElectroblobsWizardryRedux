package com.electroblob.wizardry.content.spell_tier;

import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.util.InventoryUtil;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.content.item.WizardArmorType;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.Elements;
import net.minecraft.ChatFormatting;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

// Probabilities: 50% spell book, 10% wand, 20% arcane tome, 20% armor (or 16.67% identification scroll if discovery mode)
public class ApprenticeTier extends SpellTier {
    public ApprenticeTier() {
        super(1500, 5, 5, 1, ChatFormatting.AQUA, 200);
    }

    @Override
    public ItemStack getTradeItem(Element element, RandomSource random, ArrayList<Spell> spells, ArrayList<Spell> specializedSpells) {
        super.getTradeItem(element, random, spells, specializedSpells);
        boolean prob = element != Elements.MAGIC && random.nextInt(4) > 0;

        int randomizer = random.nextInt(EBConfig.discoveryMode ? 12 : 10);

        // 50% chance for spell book (37.5% specialized, 12.5% general)
        if (randomizer < 5 && !spells.isEmpty()) {
            ArrayList<Spell> spellList = prob && !specializedSpells.isEmpty() ? specializedSpells : spells;
            return SpellUtil.spellBookItem(spellList.get(random.nextInt(spellList.size())));
        }

        // 10% chance for wand (7.5% specialized, 2.5% random element)
        if (randomizer < 6) {
            Element wandElement = prob ? element : SpellUtil.getRandomElement(random);
            return SpellUtil.wandItem(this, wandElement);
        }

        // 20% chance for arcane tome (tier 1)
        if (randomizer < 8) {
            return SpellUtil.arcaneTomeItem(this);
        }

        // 20% chance for wizard armor (15% specialized, 5% random element)
        if (randomizer < 10) {
            EquipmentSlot slot = InventoryUtil.ARMOR_SLOTS[random.nextInt(InventoryUtil.ARMOR_SLOTS.length)];
            Element armorElement = prob ? element : SpellUtil.getRandomElement(random);
            return new ItemStack(SpellUtil.getArmor(WizardArmorType.WIZARD, armorElement, slot));
        }

        // 16.67% chance for identification scroll (only in discovery mode)
        return new ItemStack(EBItems.IDENTIFICATION_SCROLL.get());
    }
}
