package com.binaris.wizardry.content.item.armor;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SageArmorItem extends WizardArmorItem {
    private static final float SAGE_OTHER_COST_REDUCTION = 0.2f;

    public SageArmorItem(Type type, Element element) {
        super(WizardArmorType.SAGE, type, element);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag advanced) {
        super.appendHoverText(stack, world, tooltip, advanced);
        tooltip.add(Component.translatable("item.%s.wizard_armor.enchantability".formatted(WizardryMainMod.MOD_ID)).withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("item.%s.wizard_armor.full_set".formatted(WizardryMainMod.MOD_ID)).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("item.%s.sage_armor.full_set_bonus".formatted(WizardryMainMod.MOD_ID), (int) (SAGE_OTHER_COST_REDUCTION * 100)).withStyle(ChatFormatting.AQUA));
    }

    @Override
    public void applyModifiers(LivingEntity entity, SpellModifiers modifiers, WizardArmorItem armor, Spell spell) {
        if (armor.getElement() != spell.getElement() && getEquipmentSlot() == EquipmentSlot.HEAD) {
            modifiers.set(SpellModifiers.COST, 1 - SAGE_OTHER_COST_REDUCTION);
        }
    }
}
