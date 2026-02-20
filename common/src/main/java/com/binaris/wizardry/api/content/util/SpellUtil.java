package com.binaris.wizardry.api.content.util;

import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * General utility methods used for Spell related functionality, normally being used for item creation, retrieval, etc.
 */
public final class SpellUtil {
    /** The NBT key used to store spells on items. */
    public static String SPELL_KEY = "Spell";

    private SpellUtil() {
    }

    /**
     * Returns a list of all spells that match the given filter.
     *
     * @param filter The filter to apply to the spells.
     * @return A list of spells that match the filter.
     */
    public static List<Spell> getSpells(Predicate<Spell> filter) {
        return Services.REGISTRY_UTIL.getSpells().stream().filter(filter.and(s -> s != Spells.NONE)).collect(Collectors.toList());
    }

    /**
     * Returns a random element from all the elements registered.
     *
     * @param random The RandomSource to use for selecting the element.
     * @return A random Element.
     */
    public static Element getRandomElement(RandomSource random) {
        return Services.REGISTRY_UTIL.getElements().stream().toList().get(random.nextInt(Services.REGISTRY_UTIL.getElements().size()));
    }

    /**
     * Sets a spell to the given ItemStack.
     *
     * @param stack The ItemStack to which the spell is to be set.
     * @param spell The spell to be set to the ItemStack.
     * @return The ItemStack with the spell set.
     */
    public static ItemStack setSpell(ItemStack stack, Spell spell) {
        stack.getOrCreateTag().putString(SPELL_KEY, spell.getLocation().toString());
        return stack;
    }

    /**
     * Creates a spell book ItemStack containing the given spell.
     *
     * @param spell The spell to put in the book.
     * @return The spell book ItemStack.
     */
    public static ItemStack spellBookItem(Spell spell) {
        ItemStack stack = new ItemStack(EBItems.SPELL_BOOK.get(), 1);
        setSpell(stack, spell);
        return stack;
    }

    /**
     * Creates a wand ItemStack of the given tier and element.
     *
     * @param tier    The tier of the wand.
     * @param element The element of the wand.
     * @return The wand ItemStack.
     */
    public static ItemStack wandItem(SpellTier tier, Element element) {
        return new ItemStack(RegistryUtils.getWand(tier, element));
    }

    /**
     * Creates an arcane tome ItemStack of the given tier.
     *
     * @param tier The tier of the arcane tome.
     * @return The arcane tome ItemStack.
     */
    public static ItemStack createArcaneTome(SpellTier tier) {
        ItemStack stack = new ItemStack(EBItems.ARCANE_TOME.get());
        stack.getOrCreateTag().putString("Tier", tier.getOrCreateLocation().toString());
        return stack;
    }

    /**
     * Given a spell tier, returns the corresponding arcane tome ItemStack. If the tier is one of the default upgraded tiers
     * (Apprentice, Advanced, Master), it returns the corresponding predefined arcane tome. Otherwise, it creates a new
     * arcane tome with the given tier stored in its NBT. {@link #createArcaneTome(SpellTier)}
     *
     * @param tier The tier of the arcane tome.
     * @return The arcane tome ItemStack.
     */
    public static ItemStack getArcaneTome(SpellTier tier) {
        if (tier.equals(SpellTiers.APPRENTICE)) return new ItemStack(EBItems.APPRENTICE_ARCANE_TOME.get());
        if (tier.equals(SpellTiers.ADVANCED)) return new ItemStack(EBItems.ADVANCED_ARCANE_TOME.get());
        if (tier.equals(SpellTiers.MASTER)) return new ItemStack(EBItems.MASTER_ARCANE_TOME.get());
        return createArcaneTome(tier);
    }

    /**
     * Retrieves the spell from the given ItemStack.
     *
     * @param stack The ItemStack from which the spell is to be retrieved.
     * @return The spell retrieved from the ItemStack.
     */
    public static @NotNull Spell getSpell(ItemStack stack) {
        if (!stack.hasTag()) return Spells.NONE;
        return getSpellFromNbt(stack.getOrCreateTag());
    }

    /**
     * Retrieves the spell from the given NbtCompound.
     *
     * @param tag The NbtCompound from which the spell is to be retrieved.
     * @return The spell retrieved from the NbtCompound. If the tag is null, returns Spells.NONE.
     */
    private static Spell getSpellFromNbt(CompoundTag tag) {
        Spell byId = byId(tag.getString(SPELL_KEY));
        return byId == null ? Spells.NONE : byId;
    }

    /**
     * Gets a spell from the given id (ResourceLocation string).
     *
     * @param id The id of the spell.
     * @return The spell with the given id.
     */
    private static Spell byId(String id) {
        return Services.REGISTRY_UTIL.getSpell(ResourceLocation.tryParse(id));
    }
}
