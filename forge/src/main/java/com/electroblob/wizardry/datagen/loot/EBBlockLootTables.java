package com.electroblob.wizardry.datagen.loot;

import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.setup.datagen.DataGenProcessor;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class EBBlockLootTables extends BlockLootSubProvider {

    public EBBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        DataGenProcessor.get().defaultBlockDrops().forEach((name, block) -> this.dropSelf(block.get()));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        //TODO: This needs to be all ebwizardry blocks not just default datagen ones
        return DataGenProcessor.get().defaultBlockDrops().values().stream().map(DeferredObject::get)::iterator;
    }
}
