package com.electroblob.wizardry.datagen.provider;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.setup.datagen.EBDataGenProcessor;
import com.electroblob.wizardry.setup.registries.EBBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class EBBlockStateProvider extends BlockStateProvider {

    public EBBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, WizardryMainMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        EBDataGenProcessor.defaultBlockModels().forEach((name, block) -> blockWithItem(block)); // Default block models and states

        simpleBlockWithItem(EBBlocks.CRYSTAL_FLOWER.get(), models().cross(blockTexture(EBBlocks.CRYSTAL_FLOWER.get()).getPath(),
                blockTexture(EBBlocks.CRYSTAL_FLOWER.get())).renderType("cutout"));

        simpleBlockWithItem(EBBlocks.POTTED_CRYSTAL_FLOWER.get(),
                models().singleTexture("potted_crystal_flower", new ResourceLocation("flower_pot_cross"), "plant",
                blockTexture(EBBlocks.CRYSTAL_FLOWER.get())).renderType("cutout"));

    }

    private void blockWithItem(DeferredObject<Block> block) {
        simpleBlockWithItem(block.get(), cubeAll(block.get()));
    }

}
