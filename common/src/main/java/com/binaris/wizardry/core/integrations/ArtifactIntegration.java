package com.binaris.wizardry.core.integrations;

import com.binaris.wizardry.core.IArtifactEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.List;

public interface ArtifactIntegration {

    /**
     * Check if the Artifact dependency is loaded.
     *
     * @return true if it's loaded, false otherwise
     */
    boolean isLoaded();

    /**
     * Creates an artifact item base on the artifact dependency if loaded.
     *
     * @param rarity the rarity of the artifact item
     * @param effect the artifact effect (event quick access)
     * @return the created artifact item
     */
    Item createArtifact(Rarity rarity, IArtifactEffect effect);

    /**
     * Retrieves a list of all equipped items from the artifact dependency if loaded.
     *
     * @param player the player whose equipped items are to be retrieved
     * @return a list of equipped item stacks
     */
    List<ItemStack> getEquippedArtifacts(Player player);

    /**
     * Checks if a specific item is equipped in the artifact dependency if loaded.
     *
     * @param player the player to check
     * @param item   the item to check for
     * @return true if the item is equipped, false otherwise
     */
    boolean isEquipped(Player player, Item item);
}
