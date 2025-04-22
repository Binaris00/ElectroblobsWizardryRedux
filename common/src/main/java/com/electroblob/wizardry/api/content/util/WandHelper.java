package com.electroblob.wizardry.api.content.util;

import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.content.item.WandItem;
import com.electroblob.wizardry.core.registry.SpellRegistry;
import com.electroblob.wizardry.setup.registries.Spells;
import com.electroblob.wizardry.setup.registries.WandUpgrades;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public final class WandHelper {
    public static final String SPELL_ARRAY_KEY = "spells";
    public static final String SELECTED_SPELL_KEY = "selectedSpell";
    public static final String COOLDOWN_ARRAY_KEY = "cooldown";
    public static final String MAX_COOLDOWN_ARRAY_KEY = "maxCooldown";
    public static final String UPGRADES_KEY = "upgrades";
    public static final String PROGRESSION_KEY = "progression";

    public static List<Spell> getSpells(ItemStack wand) {
        ArrayList<Spell> spells = new ArrayList<>();
        wand.getOrCreateTag();

        if(wand.getOrCreateTag().contains(SPELL_ARRAY_KEY)) {
            ListTag list = wand.getOrCreateTag().getList(SPELL_ARRAY_KEY, Tag.TAG_STRING);
            for(Tag element : list){
                if(!(element instanceof StringTag stringTag)) continue;
                ResourceLocation location = ResourceLocation.tryParse(stringTag.getAsString());
                Spell spell = SpellRegistry.get(location);
                if(location != null) spells.add(spell);
            }
        }

        int maxSlots = wand .getItem() instanceof WandItem wandItem ? wandItem.getSpellSlotCount(wand) : 5;
        while(spells.size() < maxSlots) {
            spells.add(Spells.NONE);
        }

        return spells;
    }

    public static void setSpells(ItemStack wand, Collection<Spell> spells) {
        ListTag list = new ListTag();
        for(Spell spell : spells) {
            list.add(StringTag.valueOf(spell.getLocation().toString()));
        }

        wand.getOrCreateTag().put(SPELL_ARRAY_KEY, list);
    }

    public static Spell getCurrentSpell(ItemStack wand) {
        String tag = wand.getOrCreateTag().getString(SELECTED_SPELL_KEY);
        Spell spell = SpellRegistry.get(ResourceLocation.tryParse(tag));
        return spell != null ? spell : Spells.NONE;
    }

    public static void setCurrentSpell(ItemStack wand, Spell spell) {
        wand.getOrCreateTag().putString(SELECTED_SPELL_KEY, spell.getLocation().toString());
    }

    public static Spell getNextSpell(ItemStack wand) {
        List<Spell> spells = getSpells(wand);
        if (spells.isEmpty()) {
            return Spells.NONE;
        }
        Spell current = getCurrentSpell(wand);
        int currentIndex = spells.indexOf(current);
        if (currentIndex == -1) {
            return spells.get(0);
        }
        int nextIndex = (currentIndex + 1) % spells.size();
        return spells.get(nextIndex);
    }

    public static Spell getPreviousSpell(ItemStack wand) {
        List<Spell> spells = getSpells(wand);
        if (spells.isEmpty()) { return Spells.NONE; }
        Spell current = getCurrentSpell(wand);
        int currentIndex = spells.indexOf(current);

        if (currentIndex == -1) { return spells.get(0); }

        int prevIndex = (currentIndex - 1 + spells.size()) % spells.size();
        return spells.get(prevIndex);
    }

    public static void updateSelectedSpell(ItemStack wand, int selectedIndex) {
        List<Spell> spells = getSpells(wand);
        if(selectedIndex < 0 || selectedIndex >= spells.size()){
            wand.getOrCreateTag().putString(SELECTED_SPELL_KEY, Spells.NONE.getLocation().toString());
        } else {
            wand.getOrCreateTag().putString(SELECTED_SPELL_KEY, spells.get(selectedIndex).getLocation().toString());
        }
    }

    public static void selectNextSpell(ItemStack wand) {
        List<Spell> spells = getSpells(wand);
        int currentSpellIndex = spells.indexOf(getCurrentSpell(wand));
        if(currentSpellIndex >= 0 && currentSpellIndex < spells.size() - 1) {
            wand.getOrCreateTag().putString(SELECTED_SPELL_KEY, getNextSpell(wand).getLocation().toString());
        }
    }

    public static void selectPreviousSpell(ItemStack wand) {
        List<Spell> spells = getSpells(wand);
        int currentSpellIndex = spells.indexOf(getCurrentSpell(wand));
        if(currentSpellIndex > 0) {
            wand.getOrCreateTag().putString(SELECTED_SPELL_KEY, getPreviousSpell(wand).getLocation().toString());
        }
    }

    public static boolean selectSpell(ItemStack wand, int index) {
        List<Spell> spells = getSpells(wand);
        if (spells.isEmpty()) return false;

        wand.getOrCreateTag().putString(SELECTED_SPELL_KEY, spells.get(index).getLocation().toString());
        return true;
    }

    public static int[] getCooldowns(ItemStack wand) {
        int[] cooldowns = new int[0];
        return wand.getOrCreateTag().contains(COOLDOWN_ARRAY_KEY) ? wand.getOrCreateTag().getIntArray(COOLDOWN_ARRAY_KEY) : cooldowns;
    }

    public static void setCooldowns(ItemStack wand, int[] cooldowns) {
        wand.getOrCreateTag().putIntArray(COOLDOWN_ARRAY_KEY, cooldowns);
    }

    public static void decrementCooldowns(ItemStack wand) {
        int[] cooldowns = getCooldowns(wand);
        if (cooldowns.length == 0) return;

        for (int i = 0; i < cooldowns.length; i++) {
            if (cooldowns[i] > 0) cooldowns[i]--;
            if (cooldowns[i] < 0) cooldowns[i] = 0;
        }
        setCooldowns(wand, cooldowns);
    }

    public static int getCurrentCooldown(ItemStack wand) {
        int[] cooldowns = getCooldowns(wand);
        int selectedSpellIndex = getSpells(wand).indexOf(getCurrentSpell(wand));

        if (selectedSpellIndex < 0 || cooldowns.length <= selectedSpellIndex) return 0;
        return cooldowns[selectedSpellIndex];
    }

    public static int getNextCooldown(ItemStack wand) {
        int[] cooldowns = getCooldowns(wand);
        int nextSpell = getSpells(wand).indexOf(getNextSpell(wand));

        if (nextSpell < 0 || cooldowns.length <= nextSpell) return 0;

        return cooldowns[nextSpell];
    }

    public static int getPreviousCooldown(ItemStack wand) {
        int[] cooldowns = getCooldowns(wand);
        int previousSpell = getSpells(wand).indexOf(getPreviousSpell(wand));

        if (previousSpell < 0 || cooldowns.length <= previousSpell) return 0;
        return cooldowns[previousSpell];
    }

    public static void setCurrentCooldown(ItemStack wand, int cooldown) {
        int[] cooldowns = getCooldowns(wand);

        int selectedSpell = getSpells(wand).indexOf(getCurrentSpell(wand));
        int spellCount = getSpells(wand).size();

        if (spellCount <= selectedSpell) return;
        if (cooldowns.length <= selectedSpell) cooldowns = new int[spellCount];
        if (cooldown <= 0) cooldown = 1;

        cooldowns[selectedSpell] = cooldown;
        setCooldowns(wand, cooldowns);
        int[] maxCooldowns = getMaxCooldowns(wand);

        if (maxCooldowns.length <= selectedSpell) maxCooldowns = new int[spellCount];
        maxCooldowns[selectedSpell] = cooldown;
        setMaxCooldowns(wand, maxCooldowns);
    }

    public static int[] getMaxCooldowns(ItemStack wand) {
        int[] cooldowns = new int[0];
        return wand.getOrCreateTag().contains(MAX_COOLDOWN_ARRAY_KEY) ? wand.getOrCreateTag().getIntArray(MAX_COOLDOWN_ARRAY_KEY) : cooldowns;
    }

    public static void setMaxCooldowns(ItemStack wand, int[] cooldowns) {
        wand.getOrCreateTag().putIntArray(MAX_COOLDOWN_ARRAY_KEY, cooldowns);
    }

    public static int getCurrentMaxCooldown(ItemStack wand) {
        int[] cooldowns = getMaxCooldowns(wand);
        int selectedSpell = getSpells(wand).indexOf(getCurrentSpell(wand));

        if (selectedSpell < 0 || cooldowns.length <= selectedSpell) return 0;
        return cooldowns[selectedSpell];
    }

    public static int getUpgradeLevel(ItemStack wand, DeferredObject<Item> upgrade) {
        String key = WandUpgrades.getWandUpgrades().get(upgrade);
        return wand.getOrCreateTag().contains(UPGRADES_KEY) && key != null ? wand.getOrCreateTag().getCompound(UPGRADES_KEY).getInt(key)
                : 0;
    }

    public static int getTotalUpgrades(ItemStack wand) {
        int totalUpgrades = 0;
        for (DeferredObject<Item> item : WandUpgrades.getWandUpgrades().keySet())
            totalUpgrades += getUpgradeLevel(wand, item);

        return totalUpgrades;
    }

    public static void applyUpgrade(ItemStack wand, DeferredObject<Item> upgrade) {
        if (!wand.getOrCreateTag().contains(UPGRADES_KEY)) NBTExtras.storeTagSafely(wand.getOrCreateTag(), UPGRADES_KEY, new CompoundTag());

        CompoundTag upgrades = wand.getOrCreateTag().getCompound(UPGRADES_KEY);
        String key = WandUpgrades.getWandUpgrades().get(upgrade);

        if (key != null) upgrades.putInt(key, upgrades.getInt(key) + 1);
        NBTExtras.storeTagSafely(wand.getOrCreateTag(), UPGRADES_KEY, upgrades);
    }

    public static boolean isWandUpgrade(DeferredObject<Item> upgrade) {
        return WandUpgrades.getWandUpgrades().containsKey(upgrade);
    }

    public static Set<DeferredObject<Item>> getSpecialUpgrades() {
        return Collections.unmodifiableSet(WandUpgrades.getWandUpgrades().keySet());
    }

    static String getIdentifier(DeferredObject<Item> upgrade) {
        if (!isWandUpgrade(upgrade))
            throw new IllegalArgumentException("Tried to get a wand upgrade key for an item" + "that is not a registered special wand upgrade.");
        return WandUpgrades.getWandUpgrades().get(upgrade);
    }

    public static void registerSpecialUpgrade(DeferredObject<Item> upgrade, String identifier) {
        if (WandUpgrades.getWandUpgrades().containsValue(identifier))
            throw new IllegalArgumentException("Duplicate wand upgrade identifier: " + identifier);
        WandUpgrades.getWandUpgrades().put(upgrade, identifier);
    }

    public static void setProgression(ItemStack wand, int progression) {
        wand.getOrCreateTag().putInt(PROGRESSION_KEY, progression);
    }

    public static int getProgression(ItemStack wand) {
        return wand.getOrCreateTag().getInt(PROGRESSION_KEY);
    }

    public static void addProgression(ItemStack wand, int progression) {
        setProgression(wand, getProgression(wand) + progression);
    }
}