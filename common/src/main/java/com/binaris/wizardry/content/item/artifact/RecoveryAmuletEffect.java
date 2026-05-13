package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.util.InventoryUtil;
import com.binaris.wizardry.content.item.armor.WizardArmorItem;
import com.binaris.wizardry.core.IArtifactEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Arrays;

public class RecoveryAmuletEffect implements IArtifactEffect {
    private static final float HEAL_AMOUNT = 1.0f;
    private static final int MANA_COST = 5;

    @Override
    public void onTick(LivingEntity entity, Level level, ItemStack artifact) {
        if (!(entity instanceof Player player)) return;
        if (player.tickCount % 50 != 0) return;
        if (player.getHealth() >= player.getMaxHealth()) return;

        Arrays.stream(InventoryUtil.ARMOR_SLOTS)
                .map(player::getItemBySlot)
                .filter(stack -> stack.getItem() instanceof WizardArmorItem wizardArmor && wizardArmor.getMana(stack) > MANA_COST)
                .forEach(stack -> healPlayer(player, stack));
    }

    private void healPlayer(Player player, ItemStack stack) {
        WizardArmorItem wizardArmor = (WizardArmorItem) stack.getItem();
        wizardArmor.consumeMana(stack, MANA_COST, player);
        player.heal(HEAL_AMOUNT);
    }
}
