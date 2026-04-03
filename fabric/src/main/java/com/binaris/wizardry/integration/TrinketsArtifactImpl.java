package com.binaris.wizardry.integration;

import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.core.integrations.ArtifactIntegration;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TrinketsArtifactImpl implements ArtifactIntegration {
    public static final TrinketsArtifactImpl INSTANCE = new TrinketsArtifactImpl();

    // Not used
    @Override
    public boolean isLoaded() {
        return false;
    }

    @Override
    public Item createArtifact(Rarity rarity, IArtifactEffect effect) {
        return new TrinketsArtifactItem(rarity, effect);
    }

    @Override
    public List<ItemStack> getEquippedArtifacts(Player player) {
        ArrayList<ItemStack> list = new ArrayList<>();

        TrinketsApi.getTrinketComponent(player).ifPresent(c -> {
            for (Tuple<SlotReference, ItemStack> tuple : c.getAllEquipped()) {
                list.add(tuple.getB());
            }
        });

        return list;
    }

    @Override
    public boolean isEquipped(Player player, Item item) {
        AtomicBoolean result = new AtomicBoolean(false);
        TrinketsApi.getTrinketComponent(player).ifPresent(c -> {
            result.set(c.isEquipped(item));
        });

        return result.get();
    }
}
