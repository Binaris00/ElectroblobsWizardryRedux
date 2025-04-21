package com.electroblob.wizardry;

import com.electroblob.wizardry.api.LoaderEnvironment;
import com.electroblob.wizardry.capabilities.ForgePlayerWizardData;
import com.electroblob.wizardry.client.SpellGUIDisplay;
import com.electroblob.wizardry.registry.ElementRegistryForge;
import com.electroblob.wizardry.registry.SpellRegistryForge;
import com.electroblob.wizardry.registry.TierRegistryForge;
import com.electroblob.wizardry.setup.ClientSetup;
import com.electroblob.wizardry.setup.CommonSetup;
import com.electroblob.wizardry.setup.registries.client.EBKeyBinding;
import com.electroblob.wizardry.setup.registries.client.EBRenderers;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(WizardryMainMod.MOD_ID)
public final class WizardryForgeMod {
    public WizardryForgeMod() {
        WizardryMainMod.init(LoaderEnvironment.FORGE);

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        TierRegistryForge.initialize(modBus);
        ElementRegistryForge.initialize(modBus);
        SpellRegistryForge.initialize(modBus);

        TierRegistryForge.register();
        ElementRegistryForge.register();
        SpellRegistryForge.register();

        modBus.addListener(WizardryForgeMod::commonSetup);
        if(FMLEnvironment.dist.isClient()) {
            modBus.addListener(WizardryForgeMod::clientSetup);
        }
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, ForgePlayerWizardData::attachCapability);
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
