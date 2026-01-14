package com.binaris.wizardry.content.item;

import com.binaris.wizardry.api.content.data.SpellManagerData;
import com.binaris.wizardry.api.content.item.IWorkbenchItem;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellContext;
import com.binaris.wizardry.api.content.util.SpellUtil;
import com.binaris.wizardry.core.EBConfig;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BlankScrollItem extends Item implements IWorkbenchItem {
    public BlankScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onApplyButtonPressed(Player player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
        if (spellBooks[0].getItem().isEmpty() || crystals.getItem().isEmpty()) return false;
        Spell spell = SpellUtil.getSpell(spellBooks[0].getItem());

        if (!canPlayerUseSpell(player, spell)) return false;

        int scrollCost = calculateScrollCost(spell, centre.getItem().getCount());
        int manaPerCrystal = getManaPerCrystal(crystals.getItem());

        if (hasEnoughMana(crystals.getItem().getCount(), manaPerCrystal, scrollCost)) {
            consumeCrystalsAndCreateScroll(crystals, centre, spell, scrollCost, manaPerCrystal);
            return true;
        }

        return false;
    }

    private boolean canPlayerUseSpell(Player player, Spell spell) {
        if (spell == Spells.NONE) return false;
        if (!spell.isEnabled(SpellContext.SCROLL)) return false;
        if (player.isCreative()) return true;

        SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(player);
        return data != null && data.hasSpellBeenDiscovered(spell);
    }

    private int calculateScrollCost(Spell spell, int scrollCount) {
        int cost = spell.getProperties().getCost() * scrollCount;
        if (!spell.isInstantCast()) cost *= ScrollItem.CASTING_TIME / 20;
        return cost;
    }

    private int getManaPerCrystal(ItemStack crystalStack) {
        Item crystal = crystalStack.getItem();
        if (crystal == EBItems.MAGIC_CRYSTAL_SHARD.get()) return EBConfig.MANA_PER_SHARD;
        if (crystal == EBItems.MAGIC_CRYSTAL_GRAND.get()) return EBConfig.GRAND_CRYSTAL_MANA;
        return EBConfig.MANA_PER_CRYSTAL;
    }

    private boolean hasEnoughMana(int crystalCount, int manaPerCrystal, int requiredMana) {
        return crystalCount * manaPerCrystal > requiredMana;
    }

    private void consumeCrystalsAndCreateScroll(Slot crystals, Slot centre, Spell spell, int cost, int manaPerCrystal) {
        crystals.remove(Mth.ceil((float) cost / manaPerCrystal));
        ItemStack scroll = SpellUtil.setSpell(EBItems.SCROLL.get().getDefaultInstance(), spell);
        scroll.setCount(centre.getItem().getCount());
        centre.set(scroll);
    }

    @Override
    public int getSpellSlotCount(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean showTooltip(ItemStack stack) {
        return false;
    }
}
