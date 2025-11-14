package com.electroblob.wizardry.api.content.enchantment;

import com.electroblob.wizardry.api.content.data.SpellManagerData;
import com.electroblob.wizardry.api.content.event.EBEntityJoinLevelEvent;
import com.electroblob.wizardry.api.content.event.EBItemTossEvent;
import com.electroblob.wizardry.api.content.event.EBLivingDeathEvent;
import com.electroblob.wizardry.api.content.util.InventoryUtil;
import com.electroblob.wizardry.content.spell.sorcery.ImbueWeapon;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.EBEnchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Iterator;
import java.util.Map;

public interface Imbuement {

    static void onLivingDeath(EBLivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        InventoryUtil.getAllItems(player).forEach(stack -> removeImbuements(player, stack));
    }

    static void onItemTossEvent(EBItemTossEvent event) {
        removeImbuements(event.getPlayer(), event.getStack());
    }

    static boolean removeImbuements(Player player, ItemStack stack) {
        if (stack.isEnchanted()) {
            Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
            Iterator<Enchantment> iterator = enchants.keySet().iterator();
            while (iterator.hasNext()) {
                Enchantment enchantment = iterator.next();
                if (enchantment instanceof Imbuement imbuement) {
                    imbuement.onImbuementRemoval(stack);
                    iterator.remove();
                    SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(player);
                    data.removeImbuement(stack, enchantment);
                }
            }
            EnchantmentHelper.setEnchantments(enchants, stack);
        }
        return false;
    }

    static void onEntityJoinLevel(EBEntityJoinLevelEvent event) {
        if (event.getEntity().level().isClientSide() || !(event.getEntity() instanceof AbstractArrow arrow)) return;
        if (!(arrow.getOwner() instanceof LivingEntity archer)) return;

        ItemStack bow = archer.getMainHandItem();

        if (!ImbueWeapon.isBow(bow)) {
            bow = archer.getOffhandItem();
            if (!ImbueWeapon.isBow(bow)) return;
        }

        // TODO: Imbuement Make this work again
//        int level = EnchantmentHelper.getItemEnchantmentLevel(EBEnchantments.MAGIC_BOW.get(), bow);
//
//        if (level > 0) {
//            arrow.setBaseDamage(arrow.getBaseDamage() + (double) level * 0.5D + 0.5D);
//        }


        if (EnchantmentHelper.getItemEnchantmentLevel(EBEnchantments.FLAMING_WEAPON.get(), bow) > 0) {
            arrow.setSecondsOnFire(100);
        }

        // TODO: Make this work again
//
//        level = EnchantmentHelper.getTagEnchantmentLevel(WizardryEnchantments.FREEZING_WEAPON.get(), bow);
//
//        if (level > 0) {
//            if (arrow.getPersistentData() != null) {
//                arrow.getPersistentData().putInt(FreezingWeapon.FREEZING_ARROW_NBT_KEY, level);
//            }
//        }
    }

    default void onImbuementRemoval(ItemStack stack) {
    }
}
