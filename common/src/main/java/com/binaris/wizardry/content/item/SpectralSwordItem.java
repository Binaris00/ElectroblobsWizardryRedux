package com.binaris.wizardry.content.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import org.jetbrains.annotations.NotNull;

public class SpectralSwordItem extends SwordItem {
    private static final float ATTACK_SPEED = -1.6F;

    public SpectralSwordItem() {
        super(Tiers.IRON, 3, ATTACK_SPEED, new Properties().durability(1200).rarity(Rarity.UNCOMMON));
    }

    /// Writes the complete main-hand attribute set, plus the potency bonus, into the stack's NBT.
    ///
    /// In 1.20.1 {@code ItemStack.getAttributeModifiers} uses the NBT list *instead of* the item's default
    /// modifiers when that list exists. Adding only the bonus would therefore remove the sword's base damage
    /// and attack speed (the "no stats" bug), so both base modifiers must be written explicitly.
    public void applyConjuredAttributes(ItemStack stack, double bonusDamage) {
        stack.addAttributeModifier(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                        getDamage() + bonusDamage, AttributeModifier.Operation.ADDITION),
                EquipmentSlot.MAINHAND);
        stack.addAttributeModifier(Attributes.ATTACK_SPEED,
                new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                        ATTACK_SPEED, AttributeModifier.Operation.ADDITION),
                EquipmentSlot.MAINHAND);
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack stack, @NotNull ItemStack stack1) {
        return false;
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }
}