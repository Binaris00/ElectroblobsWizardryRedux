package com.electroblob.wizardry.datagen;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.datagen.provider.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 * <b>Electroblob's Wizardry Internal Use Only</b>
 * <br><br>
 * <p>
 * This is the class that handles the datagen for the mod, we register all the basic features with the class
 * {@link com.electroblob.wizardry.setup.datagen.EBDataGenProcessor EBDatagenProcessor} to avoid repetitive code,
 * also, <i>this is generated inside the common part of the mod.</i>
 */
@Mod.EventBusSubscriber(modid = WizardryMainMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EBForgeDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();


        generator.addProvider(event.includeServer(), new ForgeAdvancementProvider(
                packOutput, lookupProvider, existingFileHelper,
                Collections.singletonList(new EBAdvancementsProvider())
        ));

        generator.addProvider(event.includeServer(), EBLootTableProvider.create(packOutput));
        generator.addProvider(event.includeClient(), new EBBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new EBItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeServer(), new EBSpellsProvider(packOutput));

        EBBlockTagProvider blockTagProvider = generator.addProvider(event.includeServer(), new EBBlockTagProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new EBItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), existingFileHelper));

        event.getGenerator().addProvider(event.includeServer(), (DataProvider.Factory<EBRecipeProvider>) EBRecipeProvider::new);
    }

}
