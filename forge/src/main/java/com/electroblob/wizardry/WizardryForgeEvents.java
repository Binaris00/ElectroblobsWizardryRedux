package com.electroblob.wizardry;

import com.electroblob.wizardry.api.content.util.RegisterFunction;
import com.electroblob.wizardry.capabilities.ForgePlayerWizardData;
import com.electroblob.wizardry.setup.registries.*;
import com.electroblob.wizardry.setup.registries.client.EBClientRegister;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import com.electroblob.wizardry.setup.registries.client.EBRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

import java.util.function.Consumer;

public class WizardryForgeEvents {
    @Mod.EventBusSubscriber(modid = WizardryMainMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {

        @SubscribeEvent
        public static void registerContent(RegisterEvent event) {
            //Blocks need to be registered before items so the block items are getting added to item registry
            if(event.getRegistryKey() == Registries.MOB_EFFECT) register(event, EBMobEffects::register);
            else if (event.getRegistryKey() == Registries.BLOCK) register(event, EBBlocks::register);
            else if(event.getRegistryKey() == Registries.BLOCK_ENTITY_TYPE) register(event, EBBlockEntities::register);
            else if(event.getRegistryKey() == Registries.CREATIVE_MODE_TAB) register(event, EBCreativeTabs::register);
            else if(event.getRegistryKey() == Registries.ENTITY_TYPE) register(event, EBEntities::register);
            else if(event.getRegistryKey() == Registries.ITEM) register(event, EBItems::register);
            else if(event.getRegistryKey() == Registries.PARTICLE_TYPE) register(event, EBParticles::registerType);
            else if(event.getRegistryKey() == Registries.SOUND_EVENT) register(event, EBSounds::register);
        }

//        @SubscribeEvent
//        public static void createEntityAttributes(EntityAttributeCreationEvent event) {
//            WizardryYyYYyyyy.registerAttributes(event::put);
//        }

        private static <T> void register(RegisterEvent event, Consumer<RegisterFunction<T>> consumer) {
            consumer.accept((registry, id, value) -> event.register(registry.key(), id, () -> value));
        }

//        @SubscribeEvent
//        public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
//            if(event.getEntity() instanceof Player player && !player.level().isClientSide) {
//                WizardryEventBus.getInstance().fire(new EBPlayerJoinServerEvent(player, player.getServer()));
//            }
//        }

        @SubscribeEvent
        public static void register(RegisterCapabilitiesEvent event) {
            event.register(ForgePlayerWizardData.class);
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
