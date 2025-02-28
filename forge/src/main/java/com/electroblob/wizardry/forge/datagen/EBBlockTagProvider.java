package com.electroblob.wizardry.forge.datagen;

import com.electroblob.wizardry.Wizardry;
import com.electroblob.wizardry.setup.registries.EBBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EBBlockTagProvider extends BlockTagsProvider {

    public EBBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Wizardry.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(EBBlocks.MAGIC_CRYSTAL_BLOCK.get())
                .add(EBBlocks.FIRE_CRYSTAL_BLOCK.get())
                .add(EBBlocks.ICE_CRYSTAL_BLOCK.get())
                .add(EBBlocks.LIGHTNING_CRYSTAL_BLOCK.get())
                .add(EBBlocks.NECROMANCY_CRYSTAL_BLOCK.get())
                .add(EBBlocks.EARTH_CRYSTAL_BLOCK.get())
                .add(EBBlocks.SORCERY_CRYSTAL_BLOCK.get())
                .add(EBBlocks.HEALING_CRYSTAL_BLOCK.get())
        ;

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(EBBlocks.MAGIC_CRYSTAL_BLOCK.get())
                .add(EBBlocks.FIRE_CRYSTAL_BLOCK.get())
                .add(EBBlocks.ICE_CRYSTAL_BLOCK.get())
                .add(EBBlocks.LIGHTNING_CRYSTAL_BLOCK.get())
                .add(EBBlocks.NECROMANCY_CRYSTAL_BLOCK.get())
                .add(EBBlocks.EARTH_CRYSTAL_BLOCK.get())
                .add(EBBlocks.SORCERY_CRYSTAL_BLOCK.get())
                .add(EBBlocks.HEALING_CRYSTAL_BLOCK.get())
        ;
    }
}
