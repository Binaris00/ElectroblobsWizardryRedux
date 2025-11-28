package com.electroblob.wizardry;

import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.client.EBClientEventHelper;
import com.electroblob.wizardry.client.gui.screens.ArcaneWorkbenchScreen;
import com.electroblob.wizardry.client.gui.screens.BookshelfScreen;
import com.electroblob.wizardry.client.renderer.blockentity.ArcaneWorkbenchRender;
import com.electroblob.wizardry.client.renderer.blockentity.BookshelfRenderer;
import com.electroblob.wizardry.client.renderer.blockentity.ImbuementAltarRenderer;
import com.electroblob.wizardry.content.menu.BookshelfMenu;
import com.electroblob.wizardry.network.EBForgeNetwork;
import com.electroblob.wizardry.setup.registries.EBBlockEntities;
import com.electroblob.wizardry.setup.registries.EBMenus;
import com.electroblob.wizardry.setup.registries.client.EBItemProperties;
import com.electroblob.wizardry.setup.registries.client.EBRenderers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(WizardryMainMod.MOD_ID)
public final class WizardryForgeMod {
    public WizardryForgeMod() {
        WizardryMainMod.init();

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        EBRegistriesForge.tiers(modBus);
        EBRegistriesForge.elements(modBus);
        EBRegistriesForge.spells(modBus);

        modBus.addListener(WizardryForgeMod::commonSetup);
        if(FMLEnvironment.dist.isClient()) {
            modBus.addListener(WizardryForgeMod::clientSetup);
        }
    }



    public static void commonSetup(final FMLCommonSetupEvent event) {
        EBForgeNetwork.registerMessages();
        BookshelfMenu.initBookItems();
    }

    @SuppressWarnings("unchecked")
    public static void clientSetup(final FMLClientSetupEvent event) {
        EBClientEventHelper.register();
        SpellAction.register();
        EBRenderers.registerRenderers();
        EBRenderers.getRenderers().forEach((entity, renderer) ->
                EntityRenderers.register(entity.get(), (EntityRendererProvider<Entity>) renderer)
        );
        EBItemProperties.register();

        MenuScreens.register(EBMenus.ARCANE_WORKBENCH_MENU.get(), ArcaneWorkbenchScreen::new);
        MenuScreens.register(EBMenus.BOOKSHELF_MENU.get(), BookshelfScreen::new);

        BlockEntityRenderers.register(EBBlockEntities.ARCANE_WORKBENCH.get(), ArcaneWorkbenchRender::new);
        BlockEntityRenderers.register(EBBlockEntities.IMBUEMENT_ALTAR.get(), ImbuementAltarRenderer::new);
        BlockEntityRenderers.register(EBBlockEntities.BOOKSHELF.get(), BookshelfRenderer::new);
    }
}
