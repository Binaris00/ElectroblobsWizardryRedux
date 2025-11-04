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

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

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

        runestonePedestal(EBBlocks.FIRE_RUNESTONE_PEDESTAL.get(), "fire_runestone_pedestal", "runestone_pedestal_fire", "runestone_fire");
        runestonePedestal(EBBlocks.EARTH_RUNESTONE_PEDESTAL.get(), "earth_runestone_pedestal", "runestone_pedestal_earth", "runestone_earth");
        runestonePedestal(EBBlocks.HEALING_RUNESTONE_PEDESTAL.get(), "healing_runestone_pedestal", "runestone_pedestal_healing", "runestone_healing");
        runestonePedestal(EBBlocks.ICE_RUNESTONE_PEDESTAL.get(), "ice_runestone_pedestal", "runestone_pedestal_ice", "runestone_ice");
        runestonePedestal(EBBlocks.LIGHTNING_RUNESTONE_PEDESTAL.get(), "lightning_runestone_pedestal", "runestone_pedestal_lightning", "runestone_lightning");
        runestonePedestal(EBBlocks.NECROMANCY_RUNESTONE_PEDESTAL.get(), "necromancy_runestone_pedestal", "runestone_pedestal_necromancy", "runestone_necromancy");
        runestonePedestal(EBBlocks.SORCERY_RUNESTONE_PEDESTAL.get(), "sorcery_runestone_pedestal", "runestone_pedestal_sorcery", "runestone_sorcery");

        bookShelf(EBBlocks.BIRCH_BOOKSHELF.get(), "birch_bookshelf");
        bookShelf(EBBlocks.SPRUCE_BOOKSHELF.get(), "spruce_bookshelf");
        bookShelf(EBBlocks.JUNGLE_BOOKSHELF.get(), "jungle_bookshelf");
        bookShelf(EBBlocks.ACACIA_BOOKSHELF.get(), "acacia_bookshelf");
        bookShelf(EBBlocks.DARK_OAK_BOOKSHELF.get(), "dark_oak_bookshelf");
        bookShelf(EBBlocks.OAK_BOOKSHELF.get(), "oak_bookshelf");

        lectern(EBBlocks.OAK_LECTERN.get(), "oak_lectern");
        lectern(EBBlocks.SPRUCE_LECTERN.get(), "spruce_lectern");
        lectern(EBBlocks.BIRCH_LECTERN.get(), "birch_lectern");
        lectern(EBBlocks.JUNGLE_LECTERN.get(), "jungle_lectern");
        lectern(EBBlocks.ACACIA_LECTERN.get(), "acacia_lectern");
        lectern(EBBlocks.DARK_OAK_LECTERN.get(), "dark_oak_lectern");
    }

    private void blockWithItem(DeferredObject<Block> block) {
        simpleBlockWithItem(block.get(), cubeAll(block.get()));
    }

    private void lectern(Block block, String name){
        // model file
        var model = models().withExistingParent(name, "ebwizardry:block/lectern").renderType("cutout")
                .texture("particle", "ebwizardry:block/%s_top".formatted(name))
                .texture("base", "ebwizardry:block/%s_base".formatted(name))
                .texture("side", "ebwizardry:block/%s_side".formatted(name))
                .texture("top", "ebwizardry:block/%s_top".formatted(name))
                .texture("underside", "ebwizardry:block/%s_underside".formatted(name));

        // block state file
        this.getVariantBuilder(block)
                .forAllStates(state ->
                        ConfiguredModel.builder()
                                .modelFile(model)
                                .rotationY(((int) state.getValue(HORIZONTAL_FACING).toYRot() + 180) % 360)
                                .build()
                );

        // simple item with parent
        this.itemModels().getBuilder(name).parent(model);
    }


    private void bookShelf(Block block, String name){
        // model file
        var model = models().withExistingParent(name, "ebwizardry:block/bookshelf")
                .texture("particle", "ebwizardry:block/%s_side".formatted(name))
                .texture("side", "ebwizardry:block/%s_side".formatted(name))
                .texture("inside", "ebwizardry:block/%s_inside".formatted(name))
                .texture("top", "ebwizardry:block/%s_top".formatted(name));

        // block state file
        this.getVariantBuilder(block)
                .forAllStates(state ->
                        ConfiguredModel.builder()
                                .modelFile(model)
                                .rotationY((int) state.getValue(HORIZONTAL_FACING).toYRot())
                                .build()
                );

        // simple item with parent
        this.itemModels().getBuilder(name).parent(model);
    }

    private void runestonePedestal(Block block, String name, String textureName, String topBottomTexture){
        models().withExistingParent(name, "ebwizardry:block/runestone_pedestal")
                .texture("side", "ebwizardry:block/%s".formatted(textureName))
                .texture("top", "ebwizardry:block/%s_0".formatted(topBottomTexture))
                .texture("bottom", "ebwizardry:block/%s_0".formatted(topBottomTexture))
                .texture("overlay", "ebwizardry:block/%s_overlay".formatted(textureName));

        itemModels().getBuilder(name).parent(this.itemModels().getExistingFile(new ResourceLocation(WizardryMainMod.MOD_ID, "item/runestone_pedestal_item")))
                .texture("side", "ebwizardry:block/%s".formatted(textureName))
                .texture("top", "ebwizardry:block/%s_0".formatted(topBottomTexture))
                .texture("bottom", "ebwizardry:block/%s_0".formatted(topBottomTexture))
                .texture("overlay", "ebwizardry:block/%s_overlay".formatted(textureName));

        getVariantBuilder(block)
                .partialState()
                .addModels(ConfiguredModel.builder()
                        .modelFile(models().getExistingFile(modLoc(name)))
                        .build());
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
