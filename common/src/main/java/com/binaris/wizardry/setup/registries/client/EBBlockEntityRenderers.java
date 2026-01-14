package com.binaris.wizardry.setup.registries.client;

import com.binaris.wizardry.api.content.DeferredObject;
import com.binaris.wizardry.client.renderer.blockentity.ArcaneWorkbenchRender;
import com.binaris.wizardry.client.renderer.blockentity.BookshelfRenderer;
import com.binaris.wizardry.client.renderer.blockentity.ImbuementAltarRenderer;
import com.binaris.wizardry.setup.registries.EBBlockEntities;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Handles the registration of block entity renderers. Check {@code EBBlockEntities}
 */
public final class EBBlockEntityRenderers {
    private static final Map<DeferredObject<? extends BlockEntityType<? extends BlockEntity>>, BlockEntityRendererProvider<?>> providers = Maps.newHashMap();

    private EBBlockEntityRenderers() {
    }

    public static void init() {
        registerBlockEntityRenderer(EBBlockEntities.ARCANE_WORKBENCH, ArcaneWorkbenchRender::new);
        registerBlockEntityRenderer(EBBlockEntities.IMBUEMENT_ALTAR, ImbuementAltarRenderer::new);
        registerBlockEntityRenderer(EBBlockEntities.BOOKSHELF, BookshelfRenderer::new);
    }

    private static <T extends BlockEntity> void registerBlockEntityRenderer(
            DeferredObject<? extends BlockEntityType<T>> blockEntityType,
            BlockEntityRendererProvider<T> provider) {
        providers.put(blockEntityType, provider);
    }

    @SuppressWarnings("unchecked")
    public static void register(BiConsumer<BlockEntityType<? extends BlockEntity>, BlockEntityRendererProvider<BlockEntity>> consumer) {
        providers.forEach((blockEntityType, provider) ->
                consumer.accept(blockEntityType.get(), (BlockEntityRendererProvider<BlockEntity>) provider));
    }
}
