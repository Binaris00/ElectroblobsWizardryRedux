package com.binaris.wizardry.client;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.client.effect.ArcaneLockRender;
import com.binaris.wizardry.client.effect.ContainmentFieldRender;
import com.binaris.wizardry.network.EBFabricClientNetwork;
import com.binaris.wizardry.setup.registries.EBBlocks;
import com.binaris.wizardry.setup.registries.client.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;

public final class WizardryFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EBFabricClientNetwork.registerS2CMessages();
        EBClientEventHelper.register();

        SpellAction.register();
        EBRenderers.registerRenderers();
        EBRenderers.getRenderers().forEach((entity, renderer) -> EntityRendererRegistry.register(entity.get(), (EntityRendererProvider<Entity>) renderer));

        EBParticleProviders.registerProvider((p, f) -> {
            var reg = ParticleFactoryRegistry.getInstance();
            reg.register(p.get(), f::apply);
        });

        HudRenderCallback.EVENT.register(((guiGraphics, delta) -> SpellGUIDisplay.draw(guiGraphics, guiGraphics.pose(), delta)));

        EBRenderers.createEntityLayers((layer, supplier) -> EntityModelLayerRegistry.registerModelLayer(layer, supplier::get));
        EBArmorRenderFabric.load();

        EBItemProperties.register();
        KeyBindingHelper.registerKeyBinding(EBKeyBinding.NEXT_SPELL);
        KeyBindingHelper.registerKeyBinding(EBKeyBinding.PREVIOUS_SPELL);
        for (int i = 0; i < EBKeyBinding.SPELL_QUICK_ACCESS.length; i++) {
            KeyBindingHelper.registerKeyBinding(EBKeyBinding.SPELL_QUICK_ACCESS[i]);
        }

        EBMenuScreens.init();
        EBMenuScreens.register((menuType, screenFactory) -> MenuScreens.register(menuType, screenFactory::create));

        EBBlockEntityRenderers.init();
        EBBlockEntityRenderers.register(BlockEntityRenderers::register);

        BlockRenderLayerMap.INSTANCE.putBlock(EBBlocks.VANISHING_COBWEB.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(EBBlocks.CRYSTAL_FLOWER.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(EBBlocks.POTTED_CRYSTAL_FLOWER.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(EBBlocks.OAK_LECTERN.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(EBBlocks.SPRUCE_LECTERN.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(EBBlocks.BIRCH_LECTERN.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(EBBlocks.JUNGLE_LECTERN.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(EBBlocks.ACACIA_LECTERN.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(EBBlocks.DARK_OAK_LECTERN.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(EBBlocks.IMBUEMENT_ALTAR.get(), RenderType.cutoutMipped());

        WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderContainmentField);
    }

    private void renderContainmentField(WorldRenderContext ctx) {
        ContainmentFieldRender.render(ctx.camera(), ctx.matrixStack(), ctx.tickDelta());
        ArcaneLockRender.render(ctx.camera(), ctx.matrixStack(), ctx.tickDelta());
    }
}
