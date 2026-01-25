package com.binaris.wizardry.client;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.setup.registries.EBBlocks;
import com.binaris.wizardry.setup.registries.client.EBBlockEntityRenderers;
import com.binaris.wizardry.setup.registries.client.EBItemProperties;
import com.binaris.wizardry.setup.registries.client.EBMenuScreens;
import com.binaris.wizardry.setup.registries.client.EBRenderers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class WizardryForgeClient {

    @SuppressWarnings({"unchecked", "deprecation"})
    public static void clientSetup(final FMLClientSetupEvent event) {
        EBClientEventHelper.register();
        SpellAction.register();
        EBRenderers.registerRenderers();
        EBRenderers.getRenderers().forEach((entity, renderer) ->
                EntityRenderers.register(entity.get(), (EntityRendererProvider<Entity>) renderer)
        );
        EBItemProperties.register();

        EBMenuScreens.init();
        EBMenuScreens.register((menuType, screenFactory) ->
                MenuScreens.register(menuType, screenFactory::create)
        );

        EBBlockEntityRenderers.init();
        EBBlockEntityRenderers.register(BlockEntityRenderers::register
        );

        // Set render types for blocks that need transparency/cutout
        event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(EBBlocks.VANISHING_COBWEB.get(), RenderType.cutout()));
    }
}
