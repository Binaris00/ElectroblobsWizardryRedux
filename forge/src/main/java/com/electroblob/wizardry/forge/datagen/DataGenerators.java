package com.electroblob.wizardry.forge.datagen;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.setup.datagen.DataGenProcessor;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = WizardryMainMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenProcessor.activate();
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), EBLootTableProvider.create(packOutput));
        generator.addProvider(event.includeClient(), new EBBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new EBItemModelProvider(packOutput, existingFileHelper));

        EBBlockTagProvider blockTagProvider = generator.addProvider(
                event.includeServer(),
                new EBBlockTagProvider(packOutput, lookupProvider, existingFileHelper)
        );

        generator.addProvider(event.includeServer(), new EBItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), existingFileHelper));

    }

}
