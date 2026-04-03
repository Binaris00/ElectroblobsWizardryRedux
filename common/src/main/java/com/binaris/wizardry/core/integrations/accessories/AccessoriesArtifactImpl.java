package com.binaris.wizardry.core.integrations.accessories;

import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.core.integrations.ArtifactIntegration;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AccessoriesArtifactImpl implements ArtifactIntegration {
    public static final ArtifactIntegration INSTANCE = new AccessoriesArtifactImpl();

    // We don't really use this :p
    @Override
    public boolean isLoaded() {
        return false;
    }

    @Override
    public Item createArtifact(Rarity rarity, IArtifactEffect effect) {
        return new AccessoriesArtifactItem(rarity, effect);
    }

    @Override
    public List<ItemStack> getEquippedArtifacts(Player player) {
        if (AccessoriesCapability.get(player) != null) {
            return AccessoriesCapability.get(player).getAllEquipped().stream()
                    .map(SlotEntryReference::stack)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isEquipped(Player player, Item item) {
        if (AccessoriesCapability.get(player) != null) {
            return AccessoriesCapability.get(player).isEquipped(item);
        }
        return false;
    }
}
