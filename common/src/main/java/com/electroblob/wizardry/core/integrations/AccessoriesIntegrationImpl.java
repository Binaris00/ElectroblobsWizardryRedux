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
    
    static Item createAccessoryItem(Rarity rarity, IArtefactEffect effect) {
        return new AccessoriesArtefactItem(rarity, effect);
    }
    
    static List<ItemStack> getEquippedItemsFromAccessories(Player player) {
        if (AccessoriesCapability.get(player) != null) {
            return AccessoriesCapability.get(player).getAllEquipped().stream()
                    .map(SlotEntryReference::stack)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
    
    static boolean isEquippedInAccessories(Player player, Item item) {
        if (AccessoriesCapability.get(player) != null) {
            return AccessoriesCapability.get(player).isEquipped(item);
        }
        return false;
    }
    
    static @Nullable ItemStack getEquippedFromAccessories(Player player, Item item) {
        if (AccessoriesCapability.get(player) != null) {
            return AccessoriesCapability.get(player).getFirstEquipped(item).stack();
        }
        return null;
    }
}

