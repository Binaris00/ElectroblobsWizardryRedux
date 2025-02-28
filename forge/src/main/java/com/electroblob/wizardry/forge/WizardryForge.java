package com.electroblob.wizardry.forge;

import com.electroblob.wizardry.api.LoaderEnvironment;
import com.electroblob.wizardry.forge.registry.*;
import com.electroblob.wizardry.setup.ClientSetup;
import com.electroblob.wizardry.setup.CommonSetup;
import com.electroblob.wizardry.setup.registries.client.EBRenderers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;

import com.electroblob.wizardry.Wizardry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Wizardry.MOD_ID)
public final class WizardryForge {
    public WizardryForge() {
        Wizardry.init(LoaderEnvironment.FORGE);

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        SpellRegistryForge.initialize(modBus);
        //Blocks need to be registered before items so the block items are getting added to item registry
        BlockRegistryForge.BLOCKS.register(modBus);
        BlockRegistryForge.register();

        ItemRegistryForge.ITEMS.register(modBus);
        ItemRegistryForge.register();

        CreativeTabRegistryForge.CREATIVE_MODE_TABS.register(modBus);
        CreativeTabRegistryForge.register();

        EntityRegistryForge.ENTITY_TYPES.register(modBus);
        EntityRegistryForge.register();

        MobEffectRegistryForge.EFFECTS.register(modBus);
        MobEffectRegistryForge.register();

        SpellRegistryForge.register();

        modBus.addListener(WizardryForge::commonSetup);
        if(FMLEnvironment.dist.isClient()) {
            modBus.addListener(WizardryForge::clientSetup);
            ParticleRegistryForge.PARTICLE_TYPES.register(modBus);
            ParticleRegistryForge.register();
        }
    }



    public static void commonSetup(final FMLCommonSetupEvent event) {
        CommonSetup.setup();
    }

    @SuppressWarnings("unchecked")
    public static void clientSetup(final FMLClientSetupEvent event) {
        ClientSetup.setup();
        EBRenderers.getRenderers().forEach((entity, renderer) ->
                EntityRenderers.register(entity.get(), (EntityRendererProvider<Entity>) renderer)
        );
    }

}
