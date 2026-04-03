package com.binaris.wizardry.core.integrations;

import com.binaris.wizardry.api.content.item.ArtifactItem;
import com.binaris.wizardry.api.content.util.InventoryUtil;
import com.binaris.wizardry.core.IArtifactEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.List;

public class VanillaArtifactIntegration implements ArtifactIntegration {
    public static final ArtifactIntegration INSTANCE = new VanillaArtifactIntegration();

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public Item createArtifact(Rarity rarity, IArtifactEffect effect) {
        return new ArtifactItem(rarity, effect);
    }

    @Override
    public List<ItemStack> getEquippedArtifacts(Player player) {
        return InventoryUtil.getHotBarAndOffhand(player).stream().distinct().toList();
    }

    @Override
    public boolean isEquipped(Player player, Item item) {
        return InventoryUtil.getHotBarAndOffhand(player).stream().anyMatch(stack -> stack.getItem() == item);
    }
}
