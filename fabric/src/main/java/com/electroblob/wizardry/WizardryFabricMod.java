package com.electroblob.wizardry;

import com.electroblob.wizardry.api.content.event.EBPlayerJoinServerEvent;
import com.electroblob.wizardry.api.content.event.EBServerLevelLoadEvent;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.network.EBFabricNetwork;
import com.electroblob.wizardry.setup.registries.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.GenerationStep;

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
        EBMenus.register(Registry::register);

        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Decoration.UNDERGROUND_ORES, EBWorldGen.CRYSTAL_ORE);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Decoration.VEGETAL_DECORATION, EBWorldGen.CRYSTAL_FLOWER);

        EBFabricNetwork.registerC2SMessages();
    }
}
