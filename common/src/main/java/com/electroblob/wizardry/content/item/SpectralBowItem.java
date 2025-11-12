package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.content.entity.projectile.ConjuredArrowEntity;
import com.electroblob.wizardry.setup.registries.EBEntities;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SpectralBowItem extends BowItem {
    public SpectralBowItem() {
        super(new Properties().rarity(Rarity.UNCOMMON).durability(1200));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, Level world, @NotNull LivingEntity entity, int timeLeft) {
        if (!world.isClientSide) stack.setDamageValue(stack.getDamageValue() + (this.getUseDuration(stack) - timeLeft));
        if (!(entity instanceof Player player)) return;

        int i = this.getUseDuration(stack) - timeLeft;
        if (i < 0) return;
        double f = getPowerForTime(i);
        if(f < 0.1D) return;

        if (!world.isClientSide) {
            ConjuredArrowEntity arrow = new ConjuredArrowEntity(EBEntities.CONJURED_ARROW.get(), player, world);
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, (float) (f * 3.0F), 1.0F);

            if (f == 1.0F) arrow.setCritArrow(true);


            int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
            if (j > 0) arrow.setBaseDamage(arrow.getBaseDamage() + (double) j * 0.5D + 0.5D);


            int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);

            if (k > 0) arrow.setKnockback(k);
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) arrow.setSecondsOnFire(100);


            arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
            world.addFreshEntity(arrow);
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 1.0F, (float) (1.0F / (world.random.nextFloat() * 0.4F + 1.2F) + f * 0.5F));
        player.awardStat(Stats.ITEM_USED.get(this));
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
