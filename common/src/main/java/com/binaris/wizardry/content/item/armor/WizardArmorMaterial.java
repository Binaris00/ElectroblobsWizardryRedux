package com.binaris.wizardry.content.item.armor;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;

import java.util.Map;

/// Interface for wizard armor materials, extends the standard ArmorMaterial interface with additional properties to allow
/// custom elemental and cooldown reductions.
public interface WizardArmorMaterial extends ArmorMaterial {
    /// Cast cost reduction when matching the spell element with the armor element
    float getElementalCostReduction();
    /// Cooldown reduction when using this armor
    float getCooldownReduction();
    // Different names for each piece of armor
    Map<EquipmentSlot, String> getArmorPieceNames();
}
