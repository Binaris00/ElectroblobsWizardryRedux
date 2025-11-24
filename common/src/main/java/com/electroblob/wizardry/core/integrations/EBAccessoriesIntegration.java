package com.electroblob.wizardry.core.integrations;

import com.electroblob.wizardry.api.content.util.ArtefactItem;
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
    private EBAccessoriesIntegration() {
    }

    public static boolean isAccessoriesLoaded() {
        return Services.PLATFORM.isModLoaded("accessories");
    }

    public static Item getSafeArtifact(Rarity rarity, IArtefactEffect effect) {
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

    public static List<ItemStack> getEquippedItems(Player player) {
        if (isAccessoriesLoaded()) {
            try {
                return AccessoriesIntegrationImpl.getEquippedItemsFromAccessories(player);
            } catch (NoClassDefFoundError e) {
                // Fallback if Accessories classes are not available
            }
        }

        return InventoryUtil.getPrioritisedHotBarAndOffhand(player); // TODO Important, delete repeated artefacts
    }

    public static boolean isEquipped(Player player, Item item) {
        if (isAccessoriesLoaded()) {
            try {
                return AccessoriesIntegrationImpl.isEquippedInAccessories(player, item);
            } catch (NoClassDefFoundError e) {
                // Fallback if Accessories classes are not available
            }
        }
        return false;
    }

    public static @Nullable ItemStack getEquipped(Player player, Item item) {
        if (isAccessoriesLoaded()) {
            try {
                return AccessoriesIntegrationImpl.getEquippedFromAccessories(player, item);
            } catch (NoClassDefFoundError e) {
                // Fallback if Accessories classes are not available
            }
        }
        return null;
    }
}
