package com.electroblob.wizardry.core.integrations;

import com.electroblob.wizardry.content.item.ArtifactItem;
import com.electroblob.wizardry.core.platform.Services;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("ConstantConditions")
public final class EBAccessoriesIntegration {
    private EBAccessoriesIntegration() {
    }

    public static boolean isAccessoriesLoaded() {
        return Services.PLATFORM.isModLoaded("accessories");
    }

    public static Item artifact(Rarity rarity) {
        return !isAccessoriesLoaded() ? new ArtifactItem(rarity) : new AccessoriesArtifactItem(rarity);
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
