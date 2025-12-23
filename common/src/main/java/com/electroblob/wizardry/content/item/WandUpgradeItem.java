package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.api.content.event.EBLivingDeathEvent;
import com.electroblob.wizardry.api.content.item.IManaStoringItem;
import com.electroblob.wizardry.api.content.util.InventoryUtil;
import com.electroblob.wizardry.api.content.util.WandHelper;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.core.integrations.EBAccessoriesIntegration;
import com.electroblob.wizardry.setup.registries.EBItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WandUpgradeItem extends Item {
    public WandUpgradeItem(Properties properties) {
        super(properties);
    }

    public static void onLivingDeath(EBLivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        // Need to be a ManaStoringItem and without full mana
        InventoryUtil.getHotBarAndOffhand(player).stream()
                .filter(stack -> stack.getItem() instanceof IManaStoringItem manaItem && !manaItem.isManaFull(stack))
                .filter(stack -> WandHelper.getUpgradeLevel(stack, EBItems.SIPHON_UPGRADE) > 0)
                .findFirst() // only can recharge 1 item for death
                .ifPresent(stack -> {
                    float mana = EBConfig.SIPHON_MANA_PER_LEVEL
                            * WandHelper.getUpgradeLevel(stack, EBItems.SIPHON_UPGRADE)
                            + player.level().random.nextInt(EBConfig.SIPHON_MANA_PER_LEVEL);
                    if (EBAccessoriesIntegration.isEquipped(player, EBItems.RING_SIPHONING.get())) mana *= 1.3f;
                    ((IManaStoringItem) stack.getItem()).rechargeMana(stack, (int) mana);
                });
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack stack) {
        return Rarity.UNCOMMON;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable(getOrCreateDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
    }
}
