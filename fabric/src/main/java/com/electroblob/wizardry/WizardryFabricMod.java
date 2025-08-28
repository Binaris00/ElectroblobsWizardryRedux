package com.electroblob.wizardry;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.event.EBPlayerJoinServerEvent;
import com.electroblob.wizardry.api.content.event.EBServerLevelLoadEvent;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.network.EBFabricNetwork;
import com.electroblob.wizardry.setup.registries.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;

import java.util.Arrays;

public final class WizardryFabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        WizardryMainMod.init();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            WizardryEventBus.getInstance().fire(new EBPlayerJoinServerEvent(handler.getPlayer(), server));
        });

        ServerWorldEvents.LOAD.register(((minecraftServer, serverLevel) -> WizardryEventBus.getInstance().fire(new EBServerLevelLoadEvent(serverLevel))));

        EBBlocks.register(Registry::register);
        EBBlockEntities.register(Registry::register);
        EBItems.register(Registry::register);
        EBEntities.register(Registry::register);
        EBEntities.registerAttributes(FabricDefaultAttributeRegistry::register);

        SpellTiers.register(EBRegistriesFabric.TIERS, Registry::register);
        Elements.register(EBRegistriesFabric.ELEMENTS, Registry::register);
        Spells.register(EBRegistriesFabric.SPELLS, Registry::register);

        EBCreativeTabs.register(Registry::register);
        EBMobEffects.register(Registry::register);
        EBSounds.register(Registry::register);
        EBEnchantments.register(Registry::register);
        EBLootFunctions.register(Registry::register);
        EBMenus.register(Registry::register);

        // TODO MISSING LOOT
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            // Let's only modify built-in loot tables and leave data pack loot tables untouched by checking the source.
            // We also check that the loot table ID is equal to the ID we want.
            LootPool.Builder poolBuilder = new LootPool.Builder();

            if (Arrays.asList(EBConfig.lootInjectionLocations).contains(id))
                poolBuilder.with(getAdditiveEntry("%s:chests/dungeon_additions".formatted(WizardryMainMod.MOD_ID), 1).build());


            if (id.toString().matches("minecraft:chests/jungle_temple_dispenser"))
                poolBuilder.with(getAdditiveEntry(WizardryMainMod.MOD_ID + ":chests/jungle_dispenser_additions", 1).build());


            tableBuilder.withPool(poolBuilder);
        });

        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Decoration.UNDERGROUND_ORES, EBWorldGen.CRYSTAL_ORE);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Decoration.VEGETAL_DECORATION, EBWorldGen.CRYSTAL_FLOWER);
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), MobCategory.MONSTER, EBEntities.EVIL_WIZARD.get(), 8, 1, 1);

        EBFabricNetwork.registerC2SMessages();
    }

//    private static LootPoolEntryContainer getAdditive(String entryName, String poolName) {
//        return LootPool.lootPool().add(getAdditiveEntry(entryName, 1)).setRolls(ConstantValue.exactly(1)).setBonusRolls(UniformGenerator.between(0, 1));
//    }

    private static LootPoolEntryContainer.Builder<?> getAdditiveEntry(String name, int weight) {
        return LootTableReference.lootTableReference(new ResourceLocation(name)).setWeight(weight).setQuality(0);
    }
}
