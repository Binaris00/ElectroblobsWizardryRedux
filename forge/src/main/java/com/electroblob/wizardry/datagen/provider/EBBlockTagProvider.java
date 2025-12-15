package com.electroblob.wizardry.datagen.provider;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.setup.registries.EBBlocks;
import com.electroblob.wizardry.setup.registries.EBTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public final class EBBlockTagProvider extends BlockTagsProvider {

    public EBBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, WizardryMainMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(EBBlocks.RECEPTACLE.get())
                .add(EBBlocks.WALL_RECEPTACLE.get())
                .add(EBBlocks.EARTH_RUNESTONE_PEDESTAL.get())
                .add(EBBlocks.FIRE_RUNESTONE_PEDESTAL.get())
                .add(EBBlocks.HEALING_RUNESTONE_PEDESTAL.get())
                .add(EBBlocks.ICE_RUNESTONE_PEDESTAL.get())
                .add(EBBlocks.LIGHTNING_RUNESTONE_PEDESTAL.get())
                .add(EBBlocks.NECROMANCY_RUNESTONE_PEDESTAL.get())
                .add(EBBlocks.SORCERY_RUNESTONE_PEDESTAL.get())
                .add(EBBlocks.EARTH_RUNESTONE.get())
                .add(EBBlocks.FIRE_RUNESTONE.get())
                .add(EBBlocks.HEALING_RUNESTONE.get())
                .add(EBBlocks.ICE_RUNESTONE.get())
                .add(EBBlocks.LIGHTNING_RUNESTONE.get())
                .add(EBBlocks.NECROMANCY_RUNESTONE.get())
                .add(EBBlocks.SORCERY_RUNESTONE.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(EBBlocks.CRYSTAL_ORE.get())
                .add(EBBlocks.DEEPSLATE_CRYSTAL_ORE.get())
                .add(EBBlocks.MAGIC_CRYSTAL_BLOCK.get())
                .add(EBBlocks.FIRE_CRYSTAL_BLOCK.get())
                .add(EBBlocks.ICE_CRYSTAL_BLOCK.get())
                .add(EBBlocks.LIGHTNING_CRYSTAL_BLOCK.get())
                .add(EBBlocks.NECROMANCY_CRYSTAL_BLOCK.get())
                .add(EBBlocks.EARTH_CRYSTAL_BLOCK.get())
                .add(EBBlocks.SORCERY_CRYSTAL_BLOCK.get())
                .add(EBBlocks.HEALING_CRYSTAL_BLOCK.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(EBBlocks.RECEPTACLE.get())
                .add(EBBlocks.WALL_RECEPTACLE.get())
                .add(EBBlocks.CRYSTAL_ORE.get())
                .add(EBBlocks.DEEPSLATE_CRYSTAL_ORE.get())
                .add(EBBlocks.MAGIC_CRYSTAL_BLOCK.get())
                .add(EBBlocks.FIRE_CRYSTAL_BLOCK.get())
                .add(EBBlocks.ICE_CRYSTAL_BLOCK.get())
                .add(EBBlocks.LIGHTNING_CRYSTAL_BLOCK.get())
                .add(EBBlocks.NECROMANCY_CRYSTAL_BLOCK.get())
                .add(EBBlocks.EARTH_CRYSTAL_BLOCK.get())
                .add(EBBlocks.SORCERY_CRYSTAL_BLOCK.get())
                .add(EBBlocks.HEALING_CRYSTAL_BLOCK.get())
                .add(EBBlocks.EARTH_RUNESTONE_PEDESTAL.get())
                .add(EBBlocks.FIRE_RUNESTONE_PEDESTAL.get())
                .add(EBBlocks.HEALING_RUNESTONE_PEDESTAL.get())
                .add(EBBlocks.ICE_RUNESTONE_PEDESTAL.get())
                .add(EBBlocks.LIGHTNING_RUNESTONE_PEDESTAL.get())
                .add(EBBlocks.NECROMANCY_RUNESTONE_PEDESTAL.get())
                .add(EBBlocks.SORCERY_RUNESTONE_PEDESTAL.get())
                .add(EBBlocks.EARTH_RUNESTONE.get())
                .add(EBBlocks.FIRE_RUNESTONE.get())
                .add(EBBlocks.HEALING_RUNESTONE.get())
                .add(EBBlocks.ICE_RUNESTONE.get())
                .add(EBBlocks.LIGHTNING_RUNESTONE.get())
                .add(EBBlocks.NECROMANCY_RUNESTONE.get())
                .add(EBBlocks.SORCERY_RUNESTONE.get());

        this.tag(EBTags.GILDED_WOOD_BLOCK)
                .add(EBBlocks.GILDED_ACACIA_WOOD.get())
                .add(EBBlocks.GILDED_BIRCH_WOOD.get())
                .add(EBBlocks.GILDED_DARK_OAK_WOOD.get())
                .add(EBBlocks.GILDED_OAK_WOOD.get())
                .add(EBBlocks.GILDED_JUNGLE_WOOD.get())
                .add(EBBlocks.GILDED_SPRUCE_WOOD.get());
    }
}
