package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.api.content.data.ConjureData;
import com.electroblob.wizardry.content.entity.projectile.FlamecatcherArrow;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.EBSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FlameCatcherItem extends BowItem {
    public static final String SHOTS_REMAINING_NBT_KEY = "shotsRemaining";

    public FlameCatcherItem() {
        super(new Properties().durability(1200).rarity(Rarity.EPIC));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, Level level, @NotNull LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;

        int charge = this.getUseDuration(stack) - timeLeft;
        if (charge < 0) return;
        int shotsLeft;

        // there could be a future use for the flamecatcher without conjure data, so we made it to be able to shot arrows infinitely
        if (!stack.getOrCreateTag().contains(SHOTS_REMAINING_NBT_KEY)) {
            shotsLeft = -1;
        } else {
            shotsLeft = stack.getTag().getInt(SHOTS_REMAINING_NBT_KEY);
        }

        if (shotsLeft == 0) return;
        if (!level.isClientSide) stack.setDamageValue(stack.getDamageValue() + (this.getUseDuration(stack) - timeLeft));

        if (shotsLeft != -1) {
            shotsLeft--;
            stack.getOrCreateTag().putInt(SHOTS_REMAINING_NBT_KEY, shotsLeft);
        }

        float velocity = getPowerForTime(charge);
        if (!((double) velocity < 0.1D)) {
            if (!level.isClientSide) {
                FlamecatcherArrow arrow = new FlamecatcherArrow(level);
                arrow.aim(player, FlamecatcherArrow.SPEED * velocity);
                level.addFreshEntity(arrow);
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(), EBSounds.ITEM_FLAMECATCHER_SHOOT.get(), SoundSource.PLAYERS, 1, 1);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), EBSounds.ITEM_FLAMECATCHER_FLAME.get(), SoundSource.PLAYERS, 1, 1);
        }
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
