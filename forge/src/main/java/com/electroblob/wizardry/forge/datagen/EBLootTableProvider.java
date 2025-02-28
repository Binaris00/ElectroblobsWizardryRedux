package com.electroblob.wizardry.forge.datagen;

import com.electroblob.wizardry.forge.datagen.loot.EBBlockLootTables;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

public final class EBLootTableProvider {

    public static LootTableProvider create(PackOutput output) {
        return new LootTableProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(EBBlockLootTables::new, LootContextParamSets.BLOCK)
        ));
    }

    private EBLootTableProvider() {}
}
