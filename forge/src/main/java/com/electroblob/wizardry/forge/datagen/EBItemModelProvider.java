package com.electroblob.wizardry.forge.datagen;

import com.electroblob.wizardry.Wizardry;
import com.electroblob.wizardry.setup.datagen.DataGenProcessor;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class EBItemModelProvider extends ItemModelProvider {

    public EBItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Wizardry.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        DataGenProcessor.get().items().forEach((name, item) -> simpleItem(name));
        generateWandPointModel();
        DataGenProcessor.get().wandItems().forEach((name, item) -> simpleWand(name));
    }

    private ItemModelBuilder simpleItem(String name) {
        return withExistingParent(name,
                new ResourceLocation("item/generated")).texture(
                "layer0",
                new ResourceLocation(Wizardry.MOD_ID, "item/" + name)
        );
    }

    private ItemModelBuilder simpleWand(String name) {
        return withExistingParent(
                name,
                new ResourceLocation("item/handheld"))
                .texture("layer0", new ResourceLocation(Wizardry.MOD_ID, "item/" + name))
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
