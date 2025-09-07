package com.electroblob.wizardry.core.integrations;

import com.electroblob.wizardry.api.content.util.InventoryUtil;
import com.electroblob.wizardry.api.content.util.ArtefactItem;
import com.electroblob.wizardry.core.IArtefactEffect;
import com.electroblob.wizardry.core.platform.Services;
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

@SuppressWarnings("ConstantConditions")
public final class EBAccessoriesIntegration {
    private EBAccessoriesIntegration() {
    }

    public static boolean isAccessoriesLoaded() {
        return Services.PLATFORM.isModLoaded("accessories");
    }

    public static Item getSafeArtifact(Rarity rarity, IArtefactEffect effect) {
        return !isAccessoriesLoaded() ? new ArtefactItem(rarity, effect) : new AccessoriesArtefactItem(rarity, effect);
    }

    public static List<ItemStack> getEquippedItems(Player player){
        if(isAccessoriesLoaded()){
            if (AccessoriesCapability.get(player) != null) {
                return AccessoriesCapability.get(player).getAllEquipped().stream().map(SlotEntryReference::stack).collect(Collectors.toList());
            }
            return Collections.emptyList();
        }

        return InventoryUtil.getPrioritisedHotBarAndOffhand(player); // TODO Important, delete repeated artefacts
    }

    public static boolean isEquipped(Player player, Item item) {
        if (AccessoriesCapability.get(player) != null) return AccessoriesCapability.get(player).isEquipped(item);
        return false;
    }

    public static @Nullable ItemStack getEquipped(Player player, Item item) {
        if (AccessoriesCapability.get(player) != null)
            return AccessoriesCapability.get(player).getFirstEquipped(item).stack();
        return null;
    }
}
