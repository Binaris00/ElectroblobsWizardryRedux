package com.binaris.wizardry.content.item.armor;


import com.binaris.wizardry.setup.registries.EBSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

/**
 * Enum defining the different classes of wizard armor. Each class has its own armor material, upgrade item, and
 * armor piece names. The armor piece names are used to construct the registry names for the armor items.
 * Implementation note: This enum implements ArmorMaterial so that the armor material can be accessed directly from
 * the enum value.
 */
public enum WizardArmorTypes implements WizardArmorMaterial {
    WIZARD("wizard", 15, 0.1F, 0, SoundEvents.ARMOR_EQUIP_DIAMOND, new int[]{2, 4, 5, 2}, 15, "hat", "robe", "leggings", "boots"),
    SAGE("sage", 15, 0.2f, 0, EBSounds.ITEM_ARMOR_EQUIP_SAGE.get(), new int[]{2, 5, 6, 3}, 15, "hat", "robe", "leggings", "boots"),
    BATTLEMAGE("battlemage", 15, 0.05f, 0.05f, EBSounds.ITEM_ARMOR_EQUIP_BATTLEMAGE.get(), new int[]{3, 6, 8, 3}, 15, "helmet", "chestplate", "leggings", "boots"),
    WARLOCK("warlock", 20, 0.1f, 0.1f, EBSounds.ITEM_ARMOR_EQUIP_WARLOCK.get(), new int[]{2, 4, 5, 2}, 15, "hood", "robe", "leggings", "boots");

    final String name;
    final int[] protectionValues;
    final int durabilityMultiplier;
    final float elementalCostReduction;
    final float cooldownReduction;
    final Map<EquipmentSlot, String> armorPieceNames;
    final SoundEvent equipSound;
    final int enchantability;
    final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};

    WizardArmorTypes(String name, int durabilityMultiplier, float elementalCostReduction, float cooldownReduction, SoundEvent equipSound, int[] protectionValues, int enchantability, String... armorPieceNames) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.elementalCostReduction = elementalCostReduction;
        this.cooldownReduction = cooldownReduction;
        this.equipSound = equipSound;
        this.protectionValues = protectionValues;
        this.enchantability = enchantability;

        if (armorPieceNames.length != 4) {
            throw new IllegalArgumentException("Armour class " + name + " must have exactly 4 armour piece names. Try again!!!!!");
        }
        this.armorPieceNames = new EnumMap<>(EquipmentSlot.class);
        this.armorPieceNames.put(EquipmentSlot.HEAD, armorPieceNames[0]);
        this.armorPieceNames.put(EquipmentSlot.CHEST, armorPieceNames[1]);
        this.armorPieceNames.put(EquipmentSlot.LEGS, armorPieceNames[2]);
        this.armorPieceNames.put(EquipmentSlot.FEET, armorPieceNames[3]);

    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return BASE_DURABILITY[type.ordinal()] * durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return protectionValues[type.ordinal()];
    }

    @Override
    public int getEnchantmentValue() {
        return enchantability;
    }

    @Override
    public @NotNull SoundEvent getEquipSound() {
        return equipSound;
    }

    // We don't want to allow repairing of wizard armor in an anvil, so we return null here.
    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public float getElementalCostReduction() {
        return elementalCostReduction;
    }

    @Override
    public float getCooldownReduction() {
        return cooldownReduction;
    }

    @Override
    public Map<EquipmentSlot, String> getArmorPieceNames() {
        return armorPieceNames;
    }

    @Override
    public float getToughness() {
        return 0;
    }

    @Override
    public float getKnockbackResistance() {
        return 0;
    }
}
