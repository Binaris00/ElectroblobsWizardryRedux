package com.electroblob.wizardry.api.content.util;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.content.item.WandItem;
import com.electroblob.wizardry.content.item.WizardArmorType;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class SpellUtil {
    public static String SPELL_KEY = "Spell";

    private SpellUtil() {
    }

    public static List<Spell> getSpells(Predicate<Spell> filter) {
        return Services.REGISTRY_UTIL.getSpells().stream().filter(filter.and(s -> s != Spells.NONE)).collect(Collectors.toList());
    }

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
        return new ItemStack(WandItem.getWand(tier, element));
    }

    /**
     * Creates an arcane tome ItemStack of the given tier.
     *
     * @param tier The tier of the arcane tome.
     * @return The arcane tome ItemStack.
     */
    public static ItemStack arcaneTomeItem(SpellTier tier) {
        ItemStack stack = new ItemStack(EBItems.ARCANE_TOME.get());
        stack.getOrCreateTag().putString("Tier", tier.getOrCreateLocation().toString());
        return stack;
    }


    /**
     * Retrieves the spell from the given ItemStack.
     *
     * @param stack The ItemStack from which the spell is to be retrieved.
     * @return The spell retrieved from the ItemStack.
     */
    public static @NotNull Spell getSpell(ItemStack stack) {
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

    public static String getSpellNameTranslationComponent(@Nullable ItemStack stack) {
        return (stack == null || stack.getTag() == null) ? "spell.ebwizardry.none" : "spell." + stack.getTag().getString(SPELL_KEY).replace(":", ".");
    }

    public static Item getArmor(WizardArmorType wizardArmorType, Element element, EquipmentSlot slot) {
        if (slot == null || slot.getType() != EquipmentSlot.Type.ARMOR)
            throw new IllegalArgumentException("Must be a valid armour slot");
        if (element == null) element = Elements.MAGIC;
        String registryName = wizardArmorType.getName() + "_" + wizardArmorType.getArmourPieceNames().get(slot);
        if (element != Elements.MAGIC) registryName = registryName + "_" + element.getLocation().getPath();
        return BuiltInRegistries.ITEM.get(new ResourceLocation(WizardryMainMod.MOD_ID, registryName));
    }

    public static Collection<String> getSpellNames() {
        return Services.REGISTRY_UTIL.getSpells().stream().map(Spell::getLocation).map(ResourceLocation::toString).toList();
    }


    // -------------------------------- NBT --------------------------------
    // All the methods below are used to store the spell in the item's NBT.
    // ---------------------------------------------------------------------

    /**
     * Gets a spell from the given id.
     *
     * @param id The id of the spell.
     * @return The spell with the given id.
     *
     */
    private static Spell byId(String id) {
        return Services.REGISTRY_UTIL.getSpell(ResourceLocation.tryParse(id));
    }
}
