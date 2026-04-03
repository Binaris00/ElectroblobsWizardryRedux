package com.binaris.wizardry.integration;

import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.core.integrations.ArtifactIntegration;
import com.binaris.wizardry.core.integrations.VanillaArtifactIntegration;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.List;

public class TrinketsIntegration implements ArtifactIntegration {
    public static final TrinketsIntegration INSTANCE = new TrinketsIntegration();

    @Override
    public boolean isLoaded() {
        return Services.PLATFORM.isModLoaded("trinkets");
    }

    @Override
    public Item createArtifact(Rarity rarity, IArtifactEffect effect) {
        if (!isLoaded()) {
            return VanillaArtifactIntegration.INSTANCE.createArtifact(rarity, effect);
        }

        try {
            return TrinketsArtifactImpl.INSTANCE.createArtifact(rarity, effect);
        } catch (NoClassDefFoundError e) {
            return VanillaArtifactIntegration.INSTANCE.createArtifact(rarity, effect);
        }
    }

    @Override
    public List<ItemStack> getEquippedArtifacts(Player player) {
        if (isLoaded()) {
            try {
                return TrinketsArtifactImpl.INSTANCE.getEquippedArtifacts(player);
            } catch (NoClassDefFoundError ignored) {

            }
        }

        return VanillaArtifactIntegration.INSTANCE.getEquippedArtifacts(player);
    }

    @Override
    public boolean isEquipped(Player player, Item item) {
        if (isLoaded()) {
            try {
                return TrinketsArtifactImpl.INSTANCE.isEquipped(player, item);
            } catch (NoClassDefFoundError ignored) {

            }
        }
        return VanillaArtifactIntegration.INSTANCE.isEquipped(player, item);
    }
}
