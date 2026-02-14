package com.binaris.wizardry.content.item;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.item.IManaStoringItem;
import com.binaris.wizardry.api.content.util.InventoryUtil;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class ManaFlaskItem extends Item {
    public final Size size;

    public ManaFlaskItem(Size size) {
        super(new Properties().stacksTo(16));
        this.size = size;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack flask = player.getItemInHand(usedHand);

        List<ItemStack> stacks = InventoryUtil.getHotBarAndOffhand(player);
        stacks.addAll(player.getInventory().armor);

        if (stacks.stream().anyMatch(s -> s.getItem() instanceof IManaStoringItem manaItem && !manaItem.isManaFull(s))) {
            player.startUsingItem(usedHand);
            return InteractionResultHolder.consume(flask);
        }

        return InteractionResultHolder.fail(flask);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int remainingUseDuration) {
        if (livingEntity.level().isClientSide) {
            float f = remainingUseDuration / (float) getUseDuration(stack);
            Vec3 pos = livingEntity.getEyePosition(0).subtract(0, 0.2, 0).add(livingEntity.getLookAngle().scale(0.6));
            Vec3 delta = new Vec3(0, 0.2 * f, 0).xRot(remainingUseDuration * 0.5f).yRot((float) Math.toRadians(90 - livingEntity.yHeadRot));
            ParticleBuilder.create(EBParticles.DUST).pos(pos.add(delta))
                    .velocity(delta.scale(0.2)).time(12 + livingEntity.level().random.nextInt(6))
                    .color(1, 1, 0.65f).fade(0.7f, 0, 1).spawn(livingEntity.level());
        }
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, Level level, @NotNull LivingEntity livingEntity) {
        if (!level.isClientSide && livingEntity instanceof Player player) {
            findAndChargeItem(stack, player);
        }
        return stack;
    }

    private void findAndChargeItem(ItemStack stack, Player player) {

        List<ItemStack> stacks = InventoryUtil.getHotBarAndOffhand(player);
        stacks.addAll(player.getInventory().armor);

        // Find the chargeable item with the least mana
        ItemStack toCharge = stacks.stream()
                .filter(s -> s.getItem() instanceof IManaStoringItem && !((IManaStoringItem) s.getItem()).isManaFull(s))
                .min(Comparator.comparingDouble(s -> ((IManaStoringItem) s.getItem()).getFullness(s))).orElse(null);

        if (toCharge != null) {

            ((IManaStoringItem) toCharge.getItem()).rechargeMana(toCharge, size.capacity);

            player.playSound(EBSounds.ITEM_MANA_FLASK_USE.get(), 1, 1);
            player.playSound(EBSounds.ITEM_MANA_FLASK_RECHARGE.get(), 0.7f, 1.1f);

            if (!player.isCreative()) stack.shrink(1);
            player.getCooldowns().addCooldown(this, 20);
        }
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack stack) {
        return size.rarity;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return size.useDuration;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltip, isAdvanced);
        tooltip.add(Component.translatable("item.ebwizardry.mana_flask.desc", size.capacity).withStyle(ChatFormatting.GRAY));
    }

    public enum Size {
        SMALL(75, 25, Rarity.COMMON),
        MEDIUM(350, 40, Rarity.COMMON),
        LARGE(1400, 60, Rarity.RARE);

        public final int capacity;
        public final int useDuration;
        public final Rarity rarity;

        Size(int capacity, int useDuration, Rarity rarity) {
            this.capacity = capacity;
            this.useDuration = useDuration;
            this.rarity = rarity;
        }
    }
}
