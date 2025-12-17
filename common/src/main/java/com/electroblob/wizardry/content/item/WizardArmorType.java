package com.electroblob.wizardry.content.item;


import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.EBSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Enum defining the different classes of wizard armor. Each class has its own armor material, upgrade item, and
 * armor piece names. The armor piece names are used to construct the registry names for the armor items.
 * Implementation note: This enum implements ArmorMaterial so that the armor material can be accessed directly from
 * the enum value.
 *
 */
public enum WizardArmorType implements ArmorMaterial {
    WIZARD("wizard", () -> null, 15, 0.1F, 0, SoundEvents.ARMOR_EQUIP_DIAMOND, new int[]{2, 4, 5, 2}, 15, "hat", "robe", "leggings", "boots"),
    SAGE("sage", EBItems.RESPLENDENT_THREAD, 15, 0.2f, 0, EBSounds.ITEM_ARMOUR_EQUIP_SAGE.get(), new int[]{2, 5, 6, 3}, 15, "hat", "robe", "leggings", "boots"),
    BATTLEMAGE("battlemage", EBItems.CRYSTAL_SILVER_PLATING, 15, 0.05f, 0.05f, EBSounds.ITEM_ARMOUR_EQUIP_BATTLEMAGE.get(), new int[]{3, 6, 8, 3}, 15, "helmet", "chestplate", "leggings", "boots"),
    WARLOCK("warlock", EBItems.ETHEREAL_CRYSTAL_WEAVE, 20, 0.1f, 0.1f, EBSounds.ITEM_ARMOUR_EQUIP_WARLOCK.get(), new int[]{2, 4, 5, 2}, 15, "hood", "robe", "leggings", "boots");


    final int[] protectionValues;
    final int durabilityMultiplier;
    final String name;
    final Supplier<Item> upgradeItem;
    final float elementalCostReduction;
    final float cooldownReduction;
    @Deprecated
    final Map<EquipmentSlot, String> armourPieceNames;
    final SoundEvent equipSound;
    final int enchantability;
    private final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
    Element element = null;

    WizardArmorType(String name, Supplier<Item> upgradeItem, int durabilityMultiplier, float elementalCostReduction, float cooldownReduction, SoundEvent equipSound, int[] protectionValues, int enchantability, String... armourPieceNames) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.upgradeItem = upgradeItem;
        this.elementalCostReduction = elementalCostReduction;
        this.cooldownReduction = cooldownReduction;
        this.equipSound = equipSound;
        this.protectionValues = protectionValues;
        this.enchantability = enchantability;


        if (armourPieceNames.length != 4) {
            throw new IllegalArgumentException("Armour class " + name + " must have exactly 4 armour piece names. Try again!!!!!");
        }
        this.armourPieceNames = new EnumMap<>(EquipmentSlot.class);
        this.armourPieceNames.put(EquipmentSlot.HEAD, armourPieceNames[0]);
        this.armourPieceNames.put(EquipmentSlot.CHEST, armourPieceNames[1]);
        this.armourPieceNames.put(EquipmentSlot.LEGS, armourPieceNames[2]);
        this.armourPieceNames.put(EquipmentSlot.FEET, armourPieceNames[3]);

    }
    // --------------------------------- ArmorMaterial methods --------------------------------- //

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
        if (element != null) {
            return name + "_" + element.getDescriptionFormatted();
        } else {
            return name;
        }
    }

    public Map<EquipmentSlot, String> getArmourPieceNames() {
        return armourPieceNames;
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
