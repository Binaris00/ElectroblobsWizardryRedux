package com.electroblob.wizardry.client;

import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.client.gui.screens.ArcaneWorkbenchScreen;
import com.electroblob.wizardry.client.gui.screens.BookshelfScreen;
import com.electroblob.wizardry.client.renderer.blockentity.ArcaneWorkbenchRender;
import com.electroblob.wizardry.client.renderer.blockentity.BookshelfRenderer;
import com.electroblob.wizardry.client.renderer.blockentity.ImbuementAltarRenderer;
import com.electroblob.wizardry.setup.registries.EBBlockEntities;
import com.electroblob.wizardry.setup.registries.EBMenus;
import com.electroblob.wizardry.setup.registries.client.EBItemProperties;
import com.electroblob.wizardry.setup.registries.client.EBRenderers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class WizardryForgeClient {

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
