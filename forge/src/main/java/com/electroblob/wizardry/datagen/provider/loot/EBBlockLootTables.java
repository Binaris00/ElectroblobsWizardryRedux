package com.electroblob.wizardry.datagen.provider.loot;

import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.setup.datagen.EBDataGenProcessor;
import com.electroblob.wizardry.setup.registries.EBBlocks;
import com.electroblob.wizardry.setup.registries.EBItems;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class EBBlockLootTables extends BlockLootSubProvider {

    public EBBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        // Default block drops
        EBDataGenProcessor.defaultBlockDrops().forEach((name, block) -> this.dropSelf(block.get()));

        this.add(EBBlocks.CRYSTAL_ORE.get(), createOreDrop(EBBlocks.CRYSTAL_ORE.get(), EBItems.MAGIC_CRYSTAL.get()));
        this.add(EBBlocks.DEEPSLATE_CRYSTAL_ORE.get(), createOreDrop(EBBlocks.DEEPSLATE_CRYSTAL_ORE.get(), EBItems.MAGIC_CRYSTAL.get()));
        this.dropSelf(EBBlocks.CRYSTAL_FLOWER.get());
        this.add(EBBlocks.POTTED_CRYSTAL_FLOWER.get(), createPotFlowerItemTable(EBBlocks.CRYSTAL_FLOWER.get()));

        // No drop ones
        this.add(EBBlocks.PERMAFROST.get(), noDrop());
        this.add(EBBlocks.VANISHING_COBWEB.get(), noDrop());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        List<DeferredObject<Block>> allDeferredBlocks = new ArrayList<>();
        allDeferredBlocks.addAll(EBBlocks.BLOCKS.values());
        allDeferredBlocks.addAll(EBBlocks.BLOCK_ITEMS.values());
        return allDeferredBlocks.stream().map(DeferredObject::get).collect(Collectors.toList());
    }
}
