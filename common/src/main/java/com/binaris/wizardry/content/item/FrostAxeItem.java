package com.binaris.wizardry.content.item;

import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.NotNull;

public class FrostAxeItem extends AxeItem {

    public FrostAxeItem() {
        super(EBItems.MAGICAL, 8, -3, new Properties().durability(12000).rarity(Rarity.UNCOMMON));
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity entity) {
        if (!MagicDamageSource.isEntityImmune(EBDamageSources.FROST, target) && !entity.level().isClientSide)
            target.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(),
                    Spells.FROST_AXE.property(DefaultProperties.EFFECT_DURATION),
                    Spells.FROST_AXE.property(DefaultProperties.EFFECT_STRENGTH)));
        return false;
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
