package com.binaris.wizardry.core.integrations;

import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.core.integrations.accessories.AccessoriesIntegration;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.List;

public final class ArtifactChannel {

    public static Item createArtifact(Rarity rarity, IArtifactEffect effect) {
        ArtifactIntegration integration = getIntegration();
        return integration.createArtifact(rarity, effect);
    }

    public static List<ItemStack> getEquippedArtifacts(Player player) {
        ArtifactIntegration integration = getIntegration();
        return integration.getEquippedArtifacts(player);

    }

    public static boolean isEquipped(Player player, Item item){
        ArtifactIntegration integration = getIntegration();
        return integration.isEquipped(player, item);
    }

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
