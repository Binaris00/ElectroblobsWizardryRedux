package com.binaris.wizardry.core.integrations.accessories;

import com.binaris.wizardry.api.content.item.ArtifactItem;
import com.binaris.wizardry.api.content.util.InventoryUtil;
import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.core.integrations.ArtifactIntegration;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.List;

public class AccessoriesIntegration implements ArtifactIntegration {
    public static final AccessoriesIntegration INSTANCE = new AccessoriesIntegration();

    @Override
    public boolean isLoaded() {
        return Services.PLATFORM.isModLoaded("accessories");
    }

    @Override
    public Item createArtifact(Rarity rarity, IArtifactEffect effect) {
        if (!isLoaded()) {
            return new ArtifactItem(rarity, effect);
        }
        try {
            return AccessoriesArtifactImpl.INSTANCE.createArtifact(rarity, effect);
        } catch (NoClassDefFoundError e) {
            return new ArtifactItem(rarity, effect);
        }
    }

    @Override
    public List<ItemStack> getEquippedArtifacts(Player player) {
        if (isLoaded()) {
            try {
                return AccessoriesArtifactImpl.INSTANCE.getEquippedArtifacts(player);
            } catch (NoClassDefFoundError e) {
                // Fallback if Accessories classes are not available
            }
        }

        return InventoryUtil.getHotBarAndOffhand(player).stream().distinct().toList();
    }

    @Override
    public boolean isEquipped(Player player, Item item) {
        if (isLoaded()) {
            try {
                return AccessoriesArtifactImpl.INSTANCE.isEquipped(player, item);
            } catch (NoClassDefFoundError e) {
                // Fallback if Accessories classes are not available
            }
        }
        return InventoryUtil.getHotBarAndOffhand(player).stream().anyMatch(stack -> stack.getItem() == item);
    }

    private AccessoriesIntegration() {
    }
}
