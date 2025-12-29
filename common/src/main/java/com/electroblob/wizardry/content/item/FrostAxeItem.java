package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.api.content.util.MagicDamageSource;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.EBMobEffects;
import com.electroblob.wizardry.setup.registries.Spells;
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
        if (!MagicDamageSource.isEntityImmune(EBDamageSources.FROST, target))
            target.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(), Spells.FROST_AXE.property(DefaultProperties.EFFECT_DURATION), Spells.FROST_AXE.property(DefaultProperties.EFFECT_STRENGTH)));
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
