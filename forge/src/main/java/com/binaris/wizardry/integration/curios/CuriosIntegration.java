package com.binaris.wizardry.integration.curios;

import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.core.integrations.ArtifactIntegration;
import com.binaris.wizardry.core.integrations.VanillaArtifactIntegration;
import com.binaris.wizardry.setup.registries.EBItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.fml.ModList;

import java.util.List;

public final class CuriosIntegration implements ArtifactIntegration {
    public static final ArtifactIntegration INSTANCE = new CuriosIntegration();

    @Override
    public boolean isLoaded() {
        return ModList.get().isLoaded("curios");
    }

    public static void load() {
        try {
            EBItems.getArtifacts().keySet().forEach(item ->
                    CuriosArtifactImpl.INSTANCE.load(item.get()));
        } catch (NoClassDefFoundError ignored) {
        }
    }

    @Override
    public Item createArtifact(Rarity rarity, IArtifactEffect effect) {
        if (!isLoaded()) {
            return VanillaArtifactIntegration.INSTANCE.createArtifact(rarity, effect);
        }

        try {
            return CuriosArtifactImpl.INSTANCE.createArtifact(rarity, effect);
        } catch (NoClassDefFoundError e) {
            return VanillaArtifactIntegration.INSTANCE.createArtifact(rarity, effect);
        }
    }

    @Override
    public List<ItemStack> getEquippedArtifacts(Player player) {
        if (isLoaded()) {
            try {
                return CuriosArtifactImpl.INSTANCE.getEquippedArtifacts(player);
            } catch (NoClassDefFoundError ignored) {

            }
        }

        return VanillaArtifactIntegration.INSTANCE.getEquippedArtifacts(player);
    }

    @Override
    public boolean isEquipped(Player player, Item item) {
        if (isLoaded()) {
            try {
                return CuriosArtifactImpl.INSTANCE.isEquipped(player, item);
            } catch (NoClassDefFoundError ignored) {

            }
        }
        return VanillaArtifactIntegration.INSTANCE.isEquipped(player, item);
    }


    private CuriosIntegration() {
    }
}

