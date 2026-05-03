package com.binaris.wizardry.content.item.armor;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BattleMageArmorItem extends WizardArmorItem {
    public BattleMageArmorItem(Type type, Element element) {
        super(WizardArmorType.BATTLEMAGE, type, element);
    }

    @Override
    public void effectTick(ItemStack stack, LivingEntity entity, Level level) {
        if (level.getGameTime() % 40 != 0) return;
        if (getEquipmentSlot() != EquipmentSlot.HEAD) return;
        entity.addEffect(new MobEffectInstance(EBMobEffects.WARD.get(), 80, 0, false, false, false));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag advanced) {
        super.appendHoverText(stack, world, tooltip, advanced);
        tooltip.add(Component.translatable("item.%s.wizard_armor.full_set".formatted(WizardryMainMod.MOD_ID)).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("item.%s.battlemage_armor.full_set_bonus".formatted(WizardryMainMod.MOD_ID)).withStyle(ChatFormatting.AQUA));
    }
}