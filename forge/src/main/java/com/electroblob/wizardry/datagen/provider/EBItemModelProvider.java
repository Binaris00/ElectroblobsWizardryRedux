package com.electroblob.wizardry.datagen.provider;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.setup.datagen.EBDataGenProcessor;
import com.electroblob.wizardry.setup.registries.EBBlocks;
import com.electroblob.wizardry.setup.registries.EBItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public final class EBItemModelProvider extends ItemModelProvider {

    public EBItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, WizardryMainMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        EBDataGenProcessor.items().forEach((name, item) -> simpleItem(name));
        generateWandPointModel();
        EBDataGenProcessor.wandItems().forEach((name, item) -> simpleWand(name));
        simpleBlockItemBlockTexture(EBBlocks.CRYSTAL_FLOWER);

        spawnEgg(EBItems.WIZARD_SPAWN_EGG);
        spawnEgg(EBItems.EVIL_WIZARD_SPAWN_EGG);
        spawnEgg(EBItems.REMNANT_SPAWN_EGG);
    }

    private void simpleBlockItemBlockTexture(DeferredObject<Block> item) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(item.get());

        withExistingParent(id.getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(WizardryMainMod.MOD_ID, "block/" + id.getPath()));
    }

    private void spawnEgg(DeferredObject<Item> item){
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.get());
        withExistingParent(id.getPath(), mcLoc("item/template_spawn_egg"));
    }

    private void simpleItem(String name) {
        withExistingParent(name,
                new ResourceLocation("item/generated")).texture(
                "layer0",
                new ResourceLocation(WizardryMainMod.MOD_ID, "item/" + name)
        );
    }

    private void simpleWand(String name) {
        withExistingParent(
                name,
                new ResourceLocation("item/handheld"))
                .texture("layer0", new ResourceLocation(WizardryMainMod.MOD_ID, "item/" + name))
                .override().predicate(new ResourceLocation("this_gets_ignored"), 1)
                .model(withExistingParent("wand_point", "item/handheld")).end();
    }

    private void generateWandPointModel() {
        ItemModelBuilder builder = withExistingParent("wand_point", "item/handheld");

        builder.transforms()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                .rotation(0, -90, 105)
                .translation(0, 0, -3.25f)
                .scale(0.85f)
                .end()
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                .rotation(0, 90, -105)
                .translation(0, 0, -3.25f)
                .scale(0.85f)
                .end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                .rotation(0, -90, 105)
                .translation(1.13f, 3.2f, 0.5f)
                .scale(0.68f)
                .end()
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                .rotation(0, 90, -105)
                .translation(1.13f, 3.2f, 0.5f)
                .scale(0.68f)
                .end();
    }


}
