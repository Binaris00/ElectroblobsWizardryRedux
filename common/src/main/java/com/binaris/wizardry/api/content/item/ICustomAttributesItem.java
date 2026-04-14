package com.binaris.wizardry.api.content.item;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

/**
 * Interface to mirror allow items to add/remove entity attributes dynamically based on the item stack
 * <p>
 * This is only implemented for Wizard armor items with mixins
 */
public interface ICustomAttributesItem {
    Multimap<Attribute, AttributeModifier> getCustomAttributes(ItemStack stack, EquipmentSlot slot);
}