package com.electroblob.wizardry;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.event.EBPlayerJoinServerEvent;
import com.electroblob.wizardry.api.content.event.EBServerLevelLoadEvent;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import com.electroblob.wizardry.capabilities.ForgePlayerWizardData;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.registry.EBRegistries;
import com.electroblob.wizardry.setup.registries.*;
import com.electroblob.wizardry.setup.registries.client.EBClientRegister;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import com.electroblob.wizardry.setup.registries.client.EBRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * This class contains
 * - {@link ForgeBusEvents} : Events that are fired by Forge
 * - {@link ModBusEvents} : Events that are fired by the mod
 * - {@link ModBusEventsClient} : Events that are fired by the mod on the client
 * */
public class WizardryForgeEvents {

    @Mod.EventBusSubscriber(modid = WizardryMainMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBusEvents{
        @SubscribeEvent
        public static void onWorldLoadEvent(final LevelEvent.Load event) {
            if(event.getLevel().isClientSide()) return;
            WizardryEventBus.getInstance().fire(new EBServerLevelLoadEvent((ServerLevel) event.getLevel()));
        }

        @SubscribeEvent
        public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
            WizardryEventBus.getInstance().fire(new EBPlayerJoinServerEvent(event.getEntity(), event.getEntity().getServer()));
        }

        @SubscribeEvent
        public static void onLootTableLoadEvent(LootTableLoadEvent event) {
//            RUINED_SPELL_BOOK_LOOT_TABLES = Arrays.stream(Elements.registry.get().getValues().toArray(new Element[Elements.registry.get().getValues().size()]))
//                    .filter(e -> e != Element.MAGIC)
//                    .map(e -> new ResourceLocation(Wizardry.MODID, "gameplay/imbuement_altar/ruined_spell_book_" + e.getName().getString()))
//                    .toArray(ResourceLocation[]::new);
            if (Arrays.asList(EBConfig.lootInjectionLocations).contains(event.getName())) {
                EBLogger.warn("Working");
                event.getTable().addPool(getAdditive("%s:chests/dungeon_additions".formatted(WizardryMainMod.MOD_ID), "%s_additional_dungeon_loot".formatted(WizardryMainMod.MOD_ID)));
            }

            if (event.getName().toString().matches("minecraft:chests/jungle_temple_dispenser")) {
                event.getTable().addPool(getAdditive(WizardryMainMod.MOD_ID + ":chests/jungle_dispenser_additions", WizardryMainMod.MOD_ID + "_additional_dispenser_loot"));
            }
//
//            if (Wizardry.settings.injectMobDrops) {
//                if (Arrays.asList(Wizardry.settings.mobLootTableWhitelist).contains(event.getName())) {
//                    event.getTable().addPool(getAdditive(Wizardry.MODID + ":entities/mob_additions", Wizardry.MODID + "_additional_mob_drops"));
//                } else if (!Arrays.asList(Wizardry.settings.mobLootTableBlacklist).contains(event.getName()) && event.getName().getPath().contains("entities") || event.getName().getPath().contains("entity")) {
//                    String[] split = event.getName().getPath().split("/");
//                    String entityName = split[split.length - 1];
//
//                    EntityType<?> entry = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(entityName));
//                    if (entry == null) {
//                        return;
//                    }
//
//                    if (entry.getCategory().equals(MobCategory.MONSTER)) {
//                        event.getTable().addPool(getAdditive(Wizardry.MODID + ":entities/mob_additions", Wizardry.MODID + "_additional_mob_drops"));
//                    }
//                }
//            }
//            if (event.getName().toString().matches("minecraft:gameplay/fishing/junk")) {
//                event.getTable().addPool(LootPool.lootPool().add(getAdditiveEntry(Wizardry.MODID + ":gameplay/fishing/junk_additions", 4)).build());
//            } else if (event.getName().toString().matches("minecraft:gameplay/fishing/treasure")) {
//                event.getTable().addPool(LootPool.lootPool().add(getAdditiveEntry(Wizardry.MODID + ":gameplay/fishing/treasure_additions", 4)).build());
//            }
        }

        private static LootPool getAdditive(String entryName, String poolName) {
            return LootPool.lootPool().add(getAdditiveEntry(entryName, 1)).setRolls(ConstantValue.exactly(1)).setBonusRolls(UniformGenerator.between(0, 1)).name(WizardryMainMod.MOD_ID + "_" + poolName).build();
        }

        private static LootPoolEntryContainer.Builder<?> getAdditiveEntry(String name, int weight) {
            return LootTableReference.lootTableReference(new ResourceLocation(name)).setWeight(weight).setQuality(0);
        }
    }

    @Mod.EventBusSubscriber(modid = WizardryMainMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {

        @SubscribeEvent
        public static void registerContent(RegisterEvent event) {
            if(event.getRegistryKey() == Registries.MOB_EFFECT) register(event, EBMobEffects::register);
            else if (event.getRegistryKey() == Registries.BLOCK) register(event, EBBlocks::register);
            else if(event.getRegistryKey() == Registries.BLOCK_ENTITY_TYPE) register(event, EBBlockEntities::register);
            else if(event.getRegistryKey() == Registries.CREATIVE_MODE_TAB) register(event, EBCreativeTabs::register);
            else if(event.getRegistryKey() == Registries.ENTITY_TYPE) register(event, EBEntities::register);
            else if(event.getRegistryKey() == Registries.ITEM) register(event, EBItems::register);
            else if(event.getRegistryKey() == Registries.PARTICLE_TYPE) register(event, EBParticles::registerType);
            else if(event.getRegistryKey() == Registries.SOUND_EVENT) register(event, EBSounds::register);
            else if(event.getRegistryKey() == Registries.LOOT_FUNCTION_TYPE) register(event, EBLootFunctions::register);
            else if(event.getRegistryKey() == Registries.ENCHANTMENT) register(event, EBEnchantments::register);
            else if(event.getRegistryKey() == Registries.MENU) register(event, EBMenus::register);
            else if(event.getRegistryKey() == EBRegistries.ELEMENT) registerForge(event, Elements::registerNull);
            else if(event.getRegistryKey() == EBRegistries.TIER) registerForge(event, SpellTiers::registerNull);
            else if(event.getRegistryKey() == EBRegistries.SPELL) registerForge(event, Spells::registerNull);
        }

        /** Helps to register custom registries and keeping this system */
        private static <T> void registerForge(RegisterEvent event, Consumer<RegisterFunction<T>> consumer) {
            consumer.accept((registry, id, value) -> event.register(event.getForgeRegistry().getRegistryKey(), id, () -> value));
        }

        private static <T> void register(RegisterEvent event, Consumer<RegisterFunction<T>> consumer) {
            consumer.accept((registry, id, value) -> event.register(registry.key(), id, () -> value));
        }

        @SubscribeEvent
        public static void register(RegisterCapabilitiesEvent event) {
            event.register(ForgePlayerWizardData.class);
        }

        @SubscribeEvent
        public static void createEntityAttributes(EntityAttributeCreationEvent event) {
            EBEntities.registerAttributes(event::put);
        }
    }

    @Mod.EventBusSubscriber(modid = WizardryMainMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBusEventsClient {
        @SubscribeEvent
        public static void registerProviders(RegisterParticleProvidersEvent event) {
            EBClientRegister.registerParticleProviders(collection -> collection.forEach((type, provider)
                    -> event.registerSpriteSet(type.get(), provider::apply)
            ));
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            EBRenderers.createEntityLayers(event::registerLayerDefinition);
        }
    }
}
