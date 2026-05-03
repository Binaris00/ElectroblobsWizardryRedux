package com.binaris.wizardry.api.content.util;

import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.content.item.armor.WizardArmorType;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * General utility methods used for Spell related functionality, normally being used for item creation, retrieval, etc.
 */
public final class RegistryUtils {
    /** The NBT key used to store spells on items. */
    public static String SPELL_KEY = "Spell";

    /**
     * Returns the wand Item corresponding to the given tier and element.
     *
     * @param tier    The tier of the wand.
     * @param element The element of the wand. If null, defaults to {@link Elements#MAGIC}.
     * @return The wand Item.
     */
    public static Item getWand(@NotNull SpellTier tier, @Nullable Element element) {
        if (element == null) element = Elements.MAGIC;
        String registryName = tier == SpellTiers.NOVICE && element == Elements.MAGIC ? "novice" : tier.getOrCreateLocation().getPath();
        if (element != Elements.MAGIC) registryName = registryName + "_" + element.getLocation().getPath();
        registryName = "wand_" + registryName;
        return BuiltInRegistries.ITEM.get(new ResourceLocation(element.getLocation().getNamespace(), registryName));
    }

    public static Item getCrystal(Element element) {
        String registryName = "magic_crystal";
        if (element != null && element != Elements.MAGIC) {
            registryName += "_" + element.getLocation().getPath();
        }
        return BuiltInRegistries.ITEM.get(new ResourceLocation(element.getLocation().getNamespace(), registryName));
    }

    /**
     * Gets a random wizard armor item of the given type and element.
     *
     * @param type    The type of wizard armor.
     * @param element The element of the armor. If null, defaults to magic.
     * @return The corresponding wizard armor item.
     */
    public static Item getArmor(WizardArmorType type, Element element, RandomSource randomSource) {
        EquipmentSlot randomArmorSlot = InventoryUtil.ARMOR_SLOTS[randomSource.nextInt(InventoryUtil.ARMOR_SLOTS.length)];
        return getArmor(type, element, randomArmorSlot);
    }

    /**
     * Gets a wizard armor item based on the given parameters, searching for its implementation in the item registry
     * by constructing its registry name accordingly.
     *
     * @param wizardArmorType The type of wizard armor.
     * @param element         The element of the armor. If null, defaults to magic.
     * @param slot            The equipment slot for the armor piece.
     * @return The corresponding wizard armor item.
     * @throws IllegalArgumentException if the slot is null or not an armor slot. (this should never happen if used correctly)
     */
    public static Item getArmor(WizardArmorType wizardArmorType, Element element, EquipmentSlot slot) {
        if (slot == null || slot.getType() != EquipmentSlot.Type.ARMOR)
            throw new IllegalArgumentException("Must be a valid armour slot");
        if (element == null) element = Elements.MAGIC;

        String registryName = wizardArmorType.getName() + "_" + wizardArmorType.getArmourPieceNames().get(slot);
        if (element != Elements.MAGIC)
            registryName = registryName + "_" + element.getLocation().getPath();

        // Each mod should be responsible for ensuring their items are registered with the correct names
        return BuiltInRegistries.ITEM.get(new ResourceLocation(element.getLocation().getNamespace(), registryName));
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
     * Gets the spell from the given ItemStack.
     *
     * @param stack The ItemStack to get the spell from.
     * @return The spell from the ItemStack, or {@link Spells#NONE} if the stack has no tag or the spell is not found.
     */
    public static @NotNull Spell getSpell(ItemStack stack) {
        if (!stack.hasTag()) return Spells.NONE;
        return getSpellFromNbt(stack.getTag());
    }

    /**
     * Gets the spell from the given CompoundTag.
     *
     * @param tag The CompoundTag to get the spell from.
     * @return The spell from the CompoundTag, or {@link Spells#NONE} if the tag is null or the spell is not found.
     */
    public static @NotNull Spell getSpellFromNbt(CompoundTag tag) {
        if (tag == null) return Spells.NONE;
        Spell byId = Services.REGISTRY_UTIL.getSpell(ResourceLocation.tryParse(tag.getString(SPELL_KEY)));
        return byId == null ? Spells.NONE : byId;
    }

    private RegistryUtils() {
    }
}