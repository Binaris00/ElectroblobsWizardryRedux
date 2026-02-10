package com.binaris.wizardry;

import com.binaris.wizardry.api.content.event.*;
import com.binaris.wizardry.client.NotImplementedItems;
import com.binaris.wizardry.content.menu.BookshelfMenu;
import com.binaris.wizardry.core.PropertiesFabricDataManager;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.binaris.wizardry.network.EBFabricServerNetwork;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.Registry;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.storage.loot.LootPool;

public final class WizardryFabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        WizardryMainMod.init();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> WizardryEventBus.getInstance().fire(new EBPlayerJoinServerEvent(handler.getPlayer(), server)));

        ServerWorldEvents.LOAD.register(((minecraftServer, serverLevel) -> WizardryEventBus.getInstance().fire(new EBServerLevelLoadEvent(serverLevel))));

        EBBlocks.register(Registry::register);
        EBBlockEntities.register(Registry::register);
        EBItems.register(Registry::register);
        EBEntities.register(Registry::register);
        EBEntities.registerAttributes(FabricDefaultAttributeRegistry::register);
        BookshelfMenu.initBookItems();

        SpellTiers.register(EBRegistriesFabric.TIERS, Registry::register);
        Elements.register(EBRegistriesFabric.ELEMENTS, Registry::register);
        Spells.register(EBRegistriesFabric.SPELLS, Registry::register);

        EBCreativeTabs.register(Registry::register);
        EBMobEffects.register(Registry::register);
        EBSounds.register(Registry::register);
        EBEnchantments.register(Registry::register);
        EBLootFunctions.register(Registry::register);
        EBMenus.register(Registry::register);

        EBRecipeTypes.register(Registry::register);
        EBRecipeTypes.registerSerializers(Registry::register);

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            LootPool.Builder poolBuilder = new LootPool.Builder();

            EBLootTables.applyInjections((location, pool) -> {
                if (id.equals(location)) {
                    poolBuilder.with(pool.entries[0]).build();
                }
            });
            tableBuilder.withPool(poolBuilder);
        });

        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Decoration.UNDERGROUND_ORES, EBWorldGen.CRYSTAL_ORE);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Decoration.VEGETAL_DECORATION, EBWorldGen.CRYSTAL_FLOWER);
        BiomeModifications.addSpawn(
                BiomeSelectors.foundInOverworld().and(ctx -> !ctx.getBiome().getMobSettings().getMobs(MobCategory.MONSTER).unwrap().isEmpty()),
                MobCategory.MONSTER, EBEntities.EVIL_WIZARD.get(), 8, 1, 1);

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (WizardryEventBus.getInstance().fire(new EBPlayerInteractEntityEvent(player, entity))) {
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (WizardryEventBus.getInstance().fire(new EBPlayerUseBlockEvent(player, world, hitResult.getBlockPos(), hand))) {
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> !WizardryEventBus.getInstance().fire(new EBPlayerBreakBlockEvent(player, world, pos)));

        EBFabricServerNetwork.registerC2SMessages();
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new PropertiesFabricDataManager());
        WandUpgrades.initUpgrades();
        NotImplementedItems.init();
        EBParticles.registerType(Registry::register);
    }
}
