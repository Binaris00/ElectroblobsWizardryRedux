package com.electroblob.wizardry.datagen.provider;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.setup.datagen.EBDataGenProcessor;
import net.minecraft.data.PackOutput;
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

    }

    private void blockWithItem(DeferredObject<Block> block) {
        simpleBlockWithItem(block.get(), cubeAll(block.get()));
    }

}
