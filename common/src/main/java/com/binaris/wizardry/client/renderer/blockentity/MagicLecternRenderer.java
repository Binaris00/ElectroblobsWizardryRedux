package com.binaris.wizardry.client.renderer.blockentity;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.content.block.MagicLecternBlock;
import com.binaris.wizardry.content.blockentity.MagicLecternBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class MagicLecternRenderer implements BlockEntityRenderer<MagicLecternBlockEntity> {
    private static final ResourceLocation BOOK_TEXTURE = WizardryMainMod.location("textures/entity/lectern_book.png");

    private final BookModel bookModel;

    public MagicLecternRenderer(BlockEntityRendererProvider.Context context) {
        this.bookModel = new BookModel(context.bakeLayer(ModelLayers.BOOK));
    }

    @Override
    public void render(MagicLecternBlockEntity entity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Direction facing = entity.getBlockState().getValue(MagicLecternBlock.FACING);

        poseStack.pushPose();
        poseStack.translate(0.5F, 1.0625F, 0.5F);
        // Vanilla lecterns rotate by the clockwise neighbour, not by the facing itself.
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.getClockWise().toYRot()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(67.5F));
        poseStack.translate(0.0F, -0.125F, 0.0F);

        float time = entity.ticksExisted + partialTick;
        float spread = Mth.lerp(partialTick, entity.bookSpreadPrev, entity.bookSpread);
        float page1 = entity.pageFlipPrev + (entity.pageFlip - entity.pageFlipPrev) * partialTick + 0.25F;
        float page2 = entity.pageFlipPrev + (entity.pageFlip - entity.pageFlipPrev) * partialTick + 0.75F;
        page1 = (page1 - Mth.floor(page1)) * 1.6F - 0.3F;
        page2 = (page2 - Mth.floor(page2)) * 1.6F - 0.3F;
        page1 = Mth.clamp(page1, 0, 1);
        page2 = Mth.clamp(page2, 0, 1);

        this.bookModel.setupAnim(time, page1, page2, spread);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entitySolid(BOOK_TEXTURE));
        this.bookModel.render(poseStack, consumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
