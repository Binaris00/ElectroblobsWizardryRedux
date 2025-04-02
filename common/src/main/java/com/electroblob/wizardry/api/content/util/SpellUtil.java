package com.electroblob.wizardry.api.content.util;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class SpellUtil {
    public static String SPELL_KEY = "Spell";

    private SpellUtil() {}

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

    public static ItemStack setSpell(ItemStack stack, ResourceLocation location) {
        stack.getOrCreateTag().putString(SPELL_KEY, location.toString());
        return stack;
    }

    /**
     * Retrieves the spell from the given ItemStack.
     *
     * @param stack The ItemStack from which the spell is to be retrieved.
     * @return The spell retrieved from the ItemStack.
     */
    public static Spell getSpell(ItemStack stack) {
        return getSpellFromNbt(stack.getTag());
    }

    /**
     * Retrieves the spell from the given NbtCompound.
     *
     * @param tag The NbtCompound from which the spell is to be retrieved.
     * @return The spell retrieved from the NbtCompound. If the tag is null, returns Spells.NONE.
     */
    private static Spell getSpellFromNbt(@Nullable CompoundTag tag) {
        return tag == null ? null : byId(tag.getString(SPELL_KEY)); // Needs to be none spell
    }

    public static String getSpellNameTranslationComponent(@Nullable ItemStack stack) {
        return (stack == null || stack.getTag() == null) ? "spell.ebwizardry.none" : "spell." + stack.getTag().getString(SPELL_KEY).replace(":", ".");
    }


    // -------------------------------- NBT --------------------------------
    // All the methods below are used to store the spell in the item's NBT.
    // ---------------------------------------------------------------------

    /**
     * Gets a spell from the given id.
     * @param id The id of the spell.
     * @return The spell with the given id.
     * */
    private static Spell byId(String id) {
        // TODO Bin: Temp
        return Spells.Register.SPELLS.get(new ResourceLocation(id).getPath());
    }
}
