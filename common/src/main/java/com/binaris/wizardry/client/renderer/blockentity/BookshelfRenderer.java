package com.binaris.wizardry.client.renderer.blockentity;

import com.binaris.wizardry.content.blockentity.BookshelfBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Renderer for the Bookshelf block entity.
 */
public class BookshelfRenderer implements BlockEntityRenderer<BookshelfBlockEntity> {

    public BookshelfRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BookshelfBlockEntity entity, float partialTicks, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        // Empty renderer - no custom rendering
    }
}
