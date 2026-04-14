package com.binaris.wizardry.mixin;

import com.binaris.wizardry.content.item.WizardArmorItem;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WizardArmorItem.class)
public abstract class WizardArmorItemMixin extends Item {
    @Unique WizardArmorItem item = (WizardArmorItem) (Object) this;

    public WizardArmorItemMixin(Properties properties) {
        super(properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        return item.getCustomAttributes(stack, slot);
    }
}
