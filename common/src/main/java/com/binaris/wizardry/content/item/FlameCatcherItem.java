package com.binaris.wizardry.content.item;

import com.binaris.wizardry.api.content.data.ConjureData;
import com.binaris.wizardry.content.entity.projectile.FlamecatcherArrow;
import com.binaris.wizardry.content.spell.fire.Flamecatcher;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.Spells;
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
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;
        if (level.isClientSide) return;

        int charge = this.getUseDuration(stack) - timeLeft;
        if (charge < 0) return;

        // Check shots (in case the flamecatcher is from the spell)
        ConjureData data = Services.OBJECT_DATA.getConjureData(stack);
        if (data != null && data.isSummoned()) {
            int count = Spells.FLAMECATCHER.property(Flamecatcher.SHOT_COUNT);
            if (!stack.hasTag()) stack.getOrCreateTag();
            if (!stack.getTag().contains("ShotsLeft")) stack.getTag().putInt("ShotsLeft", count);

            int shotsLeft = stack.getOrCreateTag().getInt("ShotsLeft");

            if (shotsLeft <= 0) {
                data.setSummoned(false);
                stack.shrink(1);
                return; // Don't continue processing an expired item
            }
            stack.getOrCreateTag().putInt("ShotsLeft", shotsLeft - 1);
        }

        // Arrow!!
        float velocity = getPowerForTime(charge);
        if ((double) velocity < 0.1D) return;

        FlamecatcherArrow arrow = new FlamecatcherArrow(level);
        arrow.aim(player, FlamecatcherArrow.SPEED * velocity);
        level.addFreshEntity(arrow);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), EBSounds.ITEM_FLAMECATCHER_SHOOT.get(), SoundSource.PLAYERS, 1, 1);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), EBSounds.ITEM_FLAMECATCHER_FLAME.get(), SoundSource.PLAYERS, 1, 1);

        stack.hurtAndBreak(this.getUseDuration(stack) - timeLeft, player, (p) ->
                p.broadcastBreakEvent(p.getUsedItemHand()));
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
