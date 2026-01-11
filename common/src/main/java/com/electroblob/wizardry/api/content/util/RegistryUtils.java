package com.electroblob.wizardry.api.content.util;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.content.item.WizardArmorType;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RegistryUtils {
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

    private RegistryUtils() {
    }
}
