package com.electroblob.wizardry.forge.datagen;

import com.electroblob.wizardry.Wizardry;
import com.electroblob.wizardry.api.common.DeferredObject;
import com.electroblob.wizardry.setup.datagen.DataGenProcessor;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class EBBlockStateProvider extends BlockStateProvider {

    public EBBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Wizardry.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        DataGenProcessor.get().defaultBlockModels().forEach((name, block) -> blockWithItem(block));
    }

    private void blockWithItem(DeferredObject<Block> block) {
        simpleBlockWithItem(block.get(), cubeAll(block.get()));
    }

}
