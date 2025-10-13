package com.electroblob.wizardry.datagen.provider;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.setup.datagen.EBDataGenProcessor;
import com.electroblob.wizardry.setup.registries.EBBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public final class EBBlockStateProvider extends BlockStateProvider {

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

        runestone(EBBlocks.FIRE_RUNESTONE.get(), "fire_runestone", "runestone_fire");
        runestone(EBBlocks.EARTH_RUNESTONE.get(), "earth_runestone", "runestone_earth");
        runestone(EBBlocks.HEALING_RUNESTONE.get(), "healing_runestone", "runestone_healing");
        runestone(EBBlocks.ICE_RUNESTONE.get(), "ice_runestone", "runestone_ice");
        runestone(EBBlocks.LIGHTNING_RUNESTONE.get(), "lightning_runestone", "runestone_lightning");
        runestone(EBBlocks.NECROMANCY_RUNESTONE.get(), "necromancy_runestone", "runestone_necromancy");
        runestone(EBBlocks.SORCERY_RUNESTONE.get(), "sorcery_runestone", "runestone_sorcery");


    }

    private void blockWithItem(DeferredObject<Block> block) {
        simpleBlockWithItem(block.get(), cubeAll(block.get()));
    }

    /**
     * Make the block state and the models for a runestone. Each runestone has 4 variants, each of which can be placed
     * in any orientation (i.e. 4 Y rotations and 2 X rotations).
     */
    private void runestone(Block block, String name, String textureName) {
        for (int i = 1; i <= 4; i++) {
            String modelName = "%s_%d".formatted(name, i);
            itemModels().getBuilder(name).parent(this.itemModels().getExistingFile(new ResourceLocation(WizardryMainMod.MOD_ID, "item/runestone_item")))
                    .texture("side", "ebwizardry:block/%s_0".formatted(textureName))
                    .texture("rune", "ebwizardry:block/%s_%d".formatted(textureName, i))
                    .texture("overlay", "ebwizardry:block/%s_%d_overlay".formatted(textureName, i));

            models().withExistingParent(modelName, "ebwizardry:block/runestone")
                    .texture("side", "ebwizardry:block/%s_0".formatted(textureName))
                    .texture("rune", "ebwizardry:block/%s_%d".formatted(textureName, i))
                    .texture("overlay", "ebwizardry:block/%s_%d_overlay".formatted(textureName, i));

            // Y rotations
            for (int y = 0; y < 360; y += 90) {
                ConfiguredModel.Builder<?> builder = ConfiguredModel.builder()
                        .modelFile(models().getExistingFile(modLoc(modelName)))
                        .uvLock(true);
                if (y != 0) builder.rotationY(y);
                getVariantBuilder(block)
                        .partialState()
                        .addModels(builder.build());
            }
            // X rotations
            for (int x : new int[]{90, 270}) {
                getVariantBuilder(block)
                        .partialState()
                        .addModels(ConfiguredModel.builder()
                                .modelFile(models().getExistingFile(modLoc(modelName)))
                                .uvLock(true)
                                .rotationX(x)
                                .build());
            }
        }
    }

}
