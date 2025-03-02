package com.electroblob.wizardry.api.common.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class InventoryUtil {
    /**
     * Returns a list of the itemstacks in the given player's hotbar and offhand, sorted into the following order: main
     * hand, offhand, rest of hotbar left-to-right. The returned list is a modifiable shallow copy of part of the player's
     * inventory stack list; as such, changes to the list are <b>not</b> written through to the player's inventory.
     * However, the ItemStack instances themselves are not copied, so changes to any of their fields (size, metadata...)
     * will change those in the player's inventory.
     */
    public static List<ItemStack> getPrioritisedHotBarAndOffhand(Player player) {
        List<ItemStack> hotbar = getHotbar(player);
        hotbar.add(0, player.getOffhandItem());
        hotbar.remove(player.getMainHandItem());
        hotbar.add(0, player.getMainHandItem());
        return hotbar;
    }

    public static boolean doesPlayerHaveItem(Player player, Item item) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() == item) {
                return true;
            }
        }

        for (ItemStack stack : player.getInventory().armor) {
            if (stack.getItem() == item) {
                return true;
            }
        }

        for (ItemStack stack : player.getInventory().offhand) {
            if (stack.getItem() == item) {
                return true;
            }
        }

        return false;
    }

    public static List<ItemStack> getHotbar(Player player) {
        NonNullList<ItemStack> hotBar = NonNullList.create();
        hotBar.addAll(player.getInventory().items.subList(0, 9));
        return hotBar;
    }
}
