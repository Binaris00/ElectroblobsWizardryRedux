package com.electroblob.wizardry;

import com.electroblob.wizardry.api.content.data.*;
import com.electroblob.wizardry.api.content.event.EBPlayerInteractEntityEvent;
import com.electroblob.wizardry.api.content.event.EBPlayerJoinServerEvent;
import com.electroblob.wizardry.api.content.event.EBServerLevelLoadEvent;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import com.electroblob.wizardry.capabilities.*;
import com.electroblob.wizardry.content.spell.abstr.ConjureItemSpell;
import com.electroblob.wizardry.core.PropertiesForgeDataManager;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.registry.EBRegistries;
import com.electroblob.wizardry.setup.registries.*;
import com.electroblob.wizardry.setup.registries.client.EBClientRegister;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import com.electroblob.wizardry.setup.registries.client.EBRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

import java.util.function.Consumer;

/**
 * This class contains
 * - {@link ForgeBusEvents} : Events that are fired by Forge
 * - {@link ModBusEvents} : Events that are fired by the mod
 * - {@link ModBusEventsClient} : Events that are fired by the mod on the client
 */
public class WizardryForgeEvents {

    @Mod.EventBusSubscriber(modid = WizardryMainMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBusEvents {
        @SubscribeEvent
        public static void onWorldLoadEvent(final LevelEvent.Load event) {
            if (event.getLevel().isClientSide()) return;
            WizardryEventBus.getInstance().fire(new EBServerLevelLoadEvent((ServerLevel) event.getLevel()));
        }

        @SubscribeEvent
        public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
            WizardryEventBus.getInstance().fire(new EBPlayerJoinServerEvent(event.getEntity(), event.getEntity().getServer()));
        }

        @SubscribeEvent
        public static void onLootTableLoadEvent(LootTableLoadEvent event) {
            EBLootTables.applyInjections((location, pool) -> {
                if (event.getName().equals(location)) {
                    event.getTable().addPool(pool);
                }
            });
        }

        @SubscribeEvent
        public static void registerReloadListeners(AddReloadListenerEvent event) {
            event.addListener(new PropertiesForgeDataManager());
        }

        @SubscribeEvent
        public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
            if(WizardryEventBus.getInstance().fire(new EBPlayerInteractEntityEvent(event.getEntity(), event.getTarget()))) event.setCanceled(true);
        }

        @SubscribeEvent
        public static void attachCapability(final AttachCapabilitiesEvent<Entity> event) {
            if(event.getObject() instanceof Player player){
                event.addCapability(CastCommandDataHolder.LOCATION, new CastCommandDataHolder.Provider(player));
                event.addCapability(SpellManagerDataHolder.LOCATION, new SpellManagerDataHolder.Provider(player));
                event.addCapability(WizardDataHolder.LOCATION, new WizardDataHolder.Provider(player));
            }

            if(event.getObject() instanceof Mob mob){
                event.addCapability(MinionDataHolder.LOCATION, new MinionDataHolder.Provider(mob));
            }
        }

        @SubscribeEvent
        public static void attachCapabilityItem(final AttachCapabilitiesEvent<ItemStack> event) {
            ItemStack stack = event.getObject();

            if(ConjureItemSpell.isSummonableItem(event.getObject().getItem())){
                final ConjureDataHolder.Provider provider = new ConjureDataHolder.Provider(event.getObject());
                event.addCapability(ConjureDataHolder.LOCATION, provider);
            }

            if (stack.hasTag() && stack.getOrCreateTag().contains("imbuements")) {
                event.addCapability(ImbuementEnchantDataHolder.LOCATION, new ImbuementEnchantDataHolder.Provider(event.getObject()));
            }
        }

        @SubscribeEvent
        public static void onPlayerCloned(PlayerEvent.Clone event) {
            if (!event.isWasDeath()) return; // Only copy data when player respawns after death

            // Revive the original player's capabilities to be able to read them
            event.getOriginal().reviveCaps();

            event.getOriginal().getCapability(WizardDataHolder.INSTANCE).ifPresent(old ->
                    event.getEntity().getCapability(WizardDataHolder.INSTANCE).ifPresent(holder ->
                            holder.copyFrom(old)));

            event.getOriginal().getCapability(SpellManagerDataHolder.INSTANCE).ifPresent(old ->
                    event.getEntity().getCapability(SpellManagerDataHolder.INSTANCE).ifPresent(holder ->
                            holder.copyFrom(old)));

            event.getOriginal().getCapability(CastCommandDataHolder.INSTANCE).ifPresent(old ->
                    event.getEntity().getCapability(CastCommandDataHolder.INSTANCE).ifPresent(holder ->
                            holder.copyFrom(old)));

            // Invalidate the original player's capabilities again
            event.getOriginal().invalidateCaps();
        }


        @SubscribeEvent
        public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
            event.register(WizardDataHolder.class);
            event.register(SpellManagerDataHolder.class);
            event.register(CastCommandDataHolder.class);
            event.register(MinionDataHolder.class);
            event.register(ConjureDataHolder.class);
            event.register(ImbuementEnchantDataHolder.class);

        }
    }

    @Mod.EventBusSubscriber(modid = WizardryMainMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {

        @SubscribeEvent
        public static void registerContent(RegisterEvent event) {
            if (event.getRegistryKey() == Registries.MOB_EFFECT) register(event, EBMobEffects::register);
            else if (event.getRegistryKey() == Registries.BLOCK) register(event, EBBlocks::register);
            else if (event.getRegistryKey() == Registries.BLOCK_ENTITY_TYPE) register(event, EBBlockEntities::register);
            else if (event.getRegistryKey() == Registries.CREATIVE_MODE_TAB) register(event, EBCreativeTabs::register);
            else if (event.getRegistryKey() == Registries.ENTITY_TYPE) register(event, EBEntities::register);
            else if (event.getRegistryKey() == Registries.ITEM) register(event, EBItems::register);
            else if (event.getRegistryKey() == Registries.PARTICLE_TYPE) register(event, EBParticles::registerType);
            else if (event.getRegistryKey() == Registries.SOUND_EVENT) register(event, EBSounds::register);
            else if (event.getRegistryKey() == Registries.LOOT_FUNCTION_TYPE) register(event, EBLootFunctions::register);
            else if (event.getRegistryKey() == Registries.ENCHANTMENT) register(event, EBEnchantments::register);
            else if (event.getRegistryKey() == Registries.MENU) register(event, EBMenus::register);
            else if (event.getRegistryKey() == Registries.RECIPE_TYPE) register(event, EBRecipeTypes::register);
            else if (event.getRegistryKey() == Registries.RECIPE_SERIALIZER) register(event, EBRecipeTypes::registerSerializers);
            else if (event.getRegistryKey() == EBRegistries.ELEMENT) registerForge(event, Elements::registerNull);
            else if (event.getRegistryKey() == EBRegistries.TIER) registerForge(event, SpellTiers::registerNull);
            else if (event.getRegistryKey() == EBRegistries.SPELL) registerForge(event, Spells::registerNull);
        }

        /**
         * Helps to register custom registries and keeping this system
         */
        private static <T> void registerForge(RegisterEvent event, Consumer<RegisterFunction<T>> consumer) {
            consumer.accept((registry, id, value) -> event.register(event.getForgeRegistry().getRegistryKey(), id, () -> value));
        }

        private static <T> void register(RegisterEvent event, Consumer<RegisterFunction<T>> consumer) {
            consumer.accept((registry, id, value) -> event.register(registry.key(), id, () -> value));
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
