package com.electroblob.wizardry.core.integrations;

import com.electroblob.wizardry.core.IArtefactEffect;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Internal implementation class for Accessories integration.
 * This class is only loaded when Accessories is present, preventing ClassNotFoundException.
 */
class AccessoriesIntegrationImpl {

    /**
     * Creates an artefact item that is also an accessory.
     *
     * @param rarity the rarity of the artefact
     * @param effect the artefact effect
     * @return the created accessory artefact item
     */
    static Item createAccessoryItem(Rarity rarity, IArtefactEffect effect) {
        return new AccessoriesArtefactItem(rarity, effect);
    }

    /**
     * Retrieves all equipped items from the Accessories mod for the given player.
     *
     * @param player the player whose equipped items are to be retrieved
     * @return a list of equipped item stacks
     */
    static List<ItemStack> getEquippedItemsFromAccessories(Player player) {
        if (AccessoriesCapability.get(player) != null) {
            return AccessoriesCapability.get(player).getAllEquipped().stream()
                    .map(SlotEntryReference::stack)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Checks if the specified item is equipped in the Accessories mod for the given player.
     *
     * @param player the player to check
     * @param item   the item to check for
     * @return true if the item is equipped, false otherwise
     */
    static boolean isEquippedInAccessories(Player player, Item item) {
        if (AccessoriesCapability.get(player) != null) {
            return AccessoriesCapability.get(player).isEquipped(item);
        }
        return false;
    }

    /**
     * Retrieves the first equipped instance of the specified item from the Accessories mod for the given player.
     *
     * @param player the player whose equipped item is to be retrieved
     * @param item   the item to retrieve
     * @return the equipped item stack, or null if not equipped
     */
    static @Nullable ItemStack getEquippedFromAccessories(Player player, Item item) {
        if (AccessoriesCapability.get(player) != null) {
            return AccessoriesCapability.get(player).getFirstEquipped(item).stack();
        }
        return null;
    }
}

