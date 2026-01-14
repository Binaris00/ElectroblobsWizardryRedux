package com.binaris.wizardry.client.renderer.entity;

import com.binaris.wizardry.content.entity.MeteorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MeteorRenderer extends EntityRenderer<MeteorEntity> {
    private final BlockRenderDispatcher dispatcher;

    public MeteorRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.shadowRadius = 0.5F;
        this.dispatcher = $$0.getBlockRenderDispatcher();
    }

    @Override
    public void render(MeteorEntity $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        BlockState $$6 = $$0.getBlockState();
        if ($$6.getRenderShape() == RenderShape.MODEL) {
            Level $$7 = $$0.level();
            if ($$6 != $$7.getBlockState($$0.blockPosition()) && $$6.getRenderShape() != RenderShape.INVISIBLE) {
                $$3.pushPose();
                BlockPos $$8 = BlockPos.containing($$0.getX(), $$0.getBoundingBox().maxY, $$0.getZ());
                $$3.translate(-0.5, 0.0, -0.5);
                this.dispatcher
                        .getModelRenderer()
                        .tesselateBlock(
                                $$7,
                                this.dispatcher.getBlockModel($$6),
                                $$6,
                                $$8,
                                $$3,
                                $$4.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType($$6)),
                                false,
                                RandomSource.create(),
                                $$6.getSeed($$0.getStartPos()),
                                OverlayTexture.NO_OVERLAY
                        );
                $$3.popPose();
                super.render($$0, $$1, $$2, $$3, $$4, $$5);
            }
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MeteorEntity var1) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
