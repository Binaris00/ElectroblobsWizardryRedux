package com.binaris.wizardry.content.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SpectralPickaxeItem extends PickaxeItem {
    public static final String TIER_LEVEL_TAG = "SpectralTierLevel";
    private static final float GOLD_SPEED = 12.0F;
    private static final float DIAMOND_SPEED = 8.0F;
    private static final int DIAMOND_SPEED_TIER = 2;

    public SpectralPickaxeItem() {
        super(Tiers.DIAMOND, 1, -2.8F, new Properties().durability(1200).rarity(Rarity.UNCOMMON));
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, BlockState state) {
        if (!state.is(BlockTags.MINEABLE_WITH_PICKAXE)) return 1.0F;
        return getTierLevel(stack) >= DIAMOND_SPEED_TIER ? DIAMOND_SPEED : GOLD_SPEED;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean isSelected) {
        if (level.isClientSide || !isSelected || !(entity instanceof LivingEntity living) || getTierLevel(stack) < 3) return;

        MobEffectInstance haste = living.getEffect(MobEffects.DIG_SPEED);
        if (haste == null || haste.getAmplifier() < 1 || haste.getDuration() < 20) {
            living.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 40, 1, false, false, true));
        }
    }

    public static int getTierLevel(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt(TIER_LEVEL_TAG) : 0;
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