package com.binaris.wizardry.core.integrations;

import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.core.integrations.accessories.AccessoriesIntegration;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.List;

/// Static facade that selects and delegates to the appropriate {@code ArtifactIntegration} at runtime.
///
/// The resolution order is: Accessories (cross-loader), then Curios (Forge) or Trinkets (Fabric)
/// via {@code Services.PLATFORM.getArtifactIntegration()}, falling back to {@code VanillaArtifactIntegration}
/// which scans only the hotbar (in case no mod is loaded). All artifact item creation, equipment queries, and equip-checks
/// in the mod flow through this class.
public final class ArtifactChannel {

    /// Creates an artifact item using the currently active integration.
    ///
    /// Delegates to the {@code ArtifactIntegration} selected by {@code getIntegration()}, which
    /// may wrap the item in a mod-specific item type.
    ///
    /// @param rarity the rarity tier for the artifact.
    /// @param effect the effect callback attached to the artifact.
    ///
    /// @return the created artifact item, ready for registration.
    public static Item createArtifact(Rarity rarity, IArtifactEffect effect) {
        ArtifactIntegration integration = getIntegration();
        return integration.createArtifact(rarity, effect);
    }

    /// Returns all currently equipped artifact stacks for the given player from the active integration.
    ///
    /// Used by {@code ArtifactItem} to iterate equipped artifacts and fire their tick, hurt, kill,
    /// and spell-cast callbacks. The integration determines which slots are considered "equipped"
    /// (accessory slots, curios slots, trinket slots, or the hotbar).
    ///
    /// @param player the player to query.
    ///
    /// @return a list of equipped artifact item stacks; may be empty.
    public static List<ItemStack> getEquippedArtifacts(Player player) {
        ArtifactIntegration integration = getIntegration();
        return integration.getEquippedArtifacts(player);

    }

    /// Checks whether the given item type is currently equipped by the player in the active integration.
    ///
    /// Used by spells, effects, artifact effects, and mixins to gate bonus behavior (e.g. ring effects,
    /// charm bonuses, immunity amulets). The check is an item-type match, not a stack-specific match.
    ///
    /// @param player the player to check.
    /// @param item   the item type to look for.
    ///
    /// @return {@code true} if at least one stack of the item is equipped.
    public static boolean isEquipped(Player player, Item item){
        ArtifactIntegration integration = getIntegration();
        return integration.isEquipped(player, item);
    }

    /// Selects the active {@code ArtifactIntegration} based on which mods are loaded.
    ///
    /// Priority: Accessories > platform-specific (Curios on Forge, Trinkets on Fabric) >
    /// {@code VanillaArtifactIntegration}. Called internally by the three public methods;
    /// external code should use those methods rather than calling this directly.
    ///
    /// @return the integration to use for artifact operations; never null.
    public static ArtifactIntegration getIntegration(){
        if (AccessoriesIntegration.INSTANCE.isLoaded()) {
            return AccessoriesIntegration.INSTANCE;
        }

        if (Services.PLATFORM.getPlatformName().equals("Forge") && Services.PLATFORM.isModLoaded("curios")) {
            return Services.PLATFORM.getArtifactIntegration();
        }

        if (Services.PLATFORM.getPlatformName().equals("Fabric") && Services.PLATFORM.isModLoaded("trinkets")) {
            return Services.PLATFORM.getArtifactIntegration();
        }

        return VanillaArtifactIntegration.INSTANCE;
    }

    private ArtifactChannel(){
    }
}
