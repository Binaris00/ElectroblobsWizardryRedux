package com.binaris.wizardry.content.item;

import com.binaris.wizardry.api.content.event.EBLivingDeathEvent;
import com.binaris.wizardry.api.content.item.IManaStoringItem;
import com.binaris.wizardry.api.content.util.InventoryUtil;
import com.binaris.wizardry.api.content.util.WandHelper;
import com.binaris.wizardry.core.EBConstants;
import com.binaris.wizardry.core.integrations.accessories.EBAccessoriesIntegration;
import com.binaris.wizardry.setup.registries.EBItems;
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
    public static final float RING_SIPHONING_BONUS = 1.3f;

    public WandUpgradeItem(Properties properties) {
        super(properties);
    }

    public static void onPlayerKillMob(EBLivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        // Need to be a ManaStoringItem and without full mana
        // only can recharge 1 item for death
        for (ItemStack itemStack : InventoryUtil.getHotBarAndOffhand(player)) {
            if (itemStack.getItem() instanceof IManaStoringItem manaItem && !manaItem.isManaFull(itemStack)) {
                if (WandHelper.getUpgradeLevel(itemStack, EBItems.SIPHON_UPGRADE.get()) <= 0) continue;

                float mana = EBConstants.SIPHON_MANA_PER_LEVEL
                        * WandHelper.getUpgradeLevel(itemStack, EBItems.SIPHON_UPGRADE.get())
                        + player.level().random.nextInt(EBConstants.SIPHON_MANA_PER_LEVEL);

                if (EBAccessoriesIntegration.isEquipped(player, EBItems.RING_SIPHONING.get())) mana *= RING_SIPHONING_BONUS;
                manaItem.rechargeMana(itemStack, (int) mana);
                break;

            }
        }
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
