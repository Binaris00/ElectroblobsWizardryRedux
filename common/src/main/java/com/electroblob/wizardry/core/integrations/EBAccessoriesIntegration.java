package com.electroblob.wizardry.core.integrations;

import com.electroblob.wizardry.api.content.item.ArtefactItem;
import com.electroblob.wizardry.api.content.util.InventoryUtil;
import com.electroblob.wizardry.core.IArtefactEffect;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("ConstantConditions")
public final class EBAccessoriesIntegration {
    /**
     * Checks if Accessories mod is loaded.
     *
     * @return true if Accessories is loaded, false otherwise
     */
    public static boolean isAccessoriesLoaded() {
        return Services.PLATFORM.isModLoaded("accessories");
    }

    /**
     * Creates an artefact item, using Accessories integration if available, otherwise defaults to a standard artefact item.
     *
     * @param rarity the rarity of the artefact
     * @param effect the artefact effect
     * @return the created artefact item
     */
    public static Item getArtifact(Rarity rarity, IArtefactEffect effect) {
        if (!isAccessoriesLoaded()) {
            return new ArtefactItem(rarity, effect);
        }
        // Isolate the class loading by calling a separate method
        try {
            return AccessoriesIntegrationImpl.createAccessoryItem(rarity, effect);
        } catch (NoClassDefFoundError e) {
            // Fallback if Accessories classes are not available
            return new ArtefactItem(rarity, effect);
        }
    }

    /**
     * Retrieves a list of all equipped items from Accessories mod if available, otherwise returns the player's hotbar
     * and offhand items.
     *
     * @param player the player whose equipped items are to be retrieved
     * @return a list of equipped item stacks
     */
    public static List<ItemStack> getEquippedItems(Player player) {
        if (isAccessoriesLoaded()) {
            try {
                return AccessoriesIntegrationImpl.getEquippedItemsFromAccessories(player);
            } catch (NoClassDefFoundError e) {
                // Fallback if Accessories classes are not available
            }
        }

        return InventoryUtil.getHotBarAndOffhand(player).stream().distinct().toList();
    }

    /**
     * Checks if a specific item is equipped in Accessories mod if available.
     *
     * @param player the player to check
     * @param item   the item to check for
     * @return true if the item is equipped, false otherwise
     */
    public static boolean isEquipped(Player player, Item item) {
        if (isAccessoriesLoaded()) {
            try {
                return AccessoriesIntegrationImpl.isEquippedInAccessories(player, item);
            } catch (NoClassDefFoundError e) {
                // Fallback if Accessories classes are not available
            }
        }
        return InventoryUtil.getHotBarAndOffhand(player).stream().anyMatch(stack -> stack.getItem() == item);
    }

    /**
     * Retrieves the equipped item stack for a specific item from Accessories mod if available.
     *
     * @param player the player whose equipped item is to be retrieved
     * @param item   the item to retrieve
     * @return the equipped item stack, or null if not found
     */
    public static @Nullable ItemStack getEquipped(Player player, Item item) {
        if (isAccessoriesLoaded()) {
            try {
                return AccessoriesIntegrationImpl.getEquippedFromAccessories(player, item);
            } catch (NoClassDefFoundError e) {
                // Fallback if Accessories classes are not available
            }
        }
        return InventoryUtil.getHotBarAndOffhand(player).stream()
                .filter(stack -> stack.getItem() == item)
                .findFirst()
                .orElse(null);
    }

    private EBAccessoriesIntegration() {
    }
}
