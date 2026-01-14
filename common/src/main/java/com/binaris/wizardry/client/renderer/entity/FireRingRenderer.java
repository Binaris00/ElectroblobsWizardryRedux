package com.binaris.wizardry.client.renderer.entity;

import com.binaris.wizardry.api.content.util.DrawingUtils;
import com.binaris.wizardry.content.entity.construct.FireRingConstruct;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class FireRingRenderer extends EntityRenderer<FireRingConstruct> {
    private final ResourceLocation texture;

    public FireRingRenderer(EntityRendererProvider.Context context, ResourceLocation texture) {
        super(context);
        this.texture = texture;
    }

    @Override
    public void render(FireRingConstruct entity, float p_114486_, float partialTicks, PoseStack p_114488_, @NotNull MultiBufferSource p_114489_, int p_114490_) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        p_114488_.pushPose();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        RenderSystem._setShaderTexture(0, texture);
        float f6 = 1.0F;
        float f7 = 0.5F;
        float f8 = 0.5F;

        p_114488_.mulPose(Axis.XP.rotationDegrees(-90));

        float s = DrawingUtils.smoothScaleFactor(entity.lifetime, entity.tickCount, partialTicks, 10, 10);
        p_114488_.scale(entity.getBbWidth() * s, entity.getBbWidth() * s, entity.getBbWidth() * s);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(p_114488_.last().pose(), 0.0F - f7, 0.0F - f8, 0.01f).uv(0, 1).endVertex();
        buffer.vertex(p_114488_.last().pose(), f6 - f7, 0.0F - f8, 0.01f).uv(1, 1).endVertex();
        buffer.vertex(p_114488_.last().pose(), f6 - f7, 1.0F - f8, 0.01f).uv(1, 0).endVertex();
        buffer.vertex(p_114488_.last().pose(), 0.0F - f7, 1.0F - f8, 0.01f).uv(0, 0).endVertex();

        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.disableBlend();
        p_114488_.popPose();

        if (s >= 1) {
            TextureAtlasSprite icon = Minecraft.getInstance().getBlockRenderer()
                    .getBlockModel(Blocks.FIRE.defaultBlockState()).getParticleIcon();
            float s1 = entity.getBbWidth() / 5;
            int sides = (int) (16 * s1);
            float height = 1.0f;

            for (int k = 0; k < sides; k++) {
                p_114488_.pushPose();
                p_114488_.translate(0, 0.05f, 0);
                float f2 = 0.5F;
                float f3 = 0.0F;
                float f4 = 0.2f;
                float f5 = (float) (0.0);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                float f61 = 0.0F;
                int i = 0;

                p_114488_.mulPose(Axis.YP.rotationDegrees((360f / (float) sides) * k));
                p_114488_.translate(0, 0, -2.3f * s1);

                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

                while (f4 > 0.0F) {
                    RenderSystem._setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
                    float f71 = icon.getU0();
                    float f81 = icon.getV0();
                    float f9 = icon.getU1();
                    float f10 = icon.getV1();

                    if (i / 2 % 2 == 0) {
                        float f11 = f9;
                        f9 = f71;
                        f71 = f11;
                    }

                    buffer.vertex(p_114488_.last().pose(), (f2 - f3), (0.0F - f5), f61).uv(f9, f10).endVertex();
                    buffer.vertex(p_114488_.last().pose(), (-f2 - f3), (0.0F - f5), f61).uv(f71, f10).endVertex();
                    buffer.vertex(p_114488_.last().pose(), (-f2 - f3), (height - f5), f61).uv(f71, f81).endVertex();
                    buffer.vertex(p_114488_.last().pose(), (f2 - f3), (height - f5), f61).uv(f9, f81).endVertex();
                    f4 -= 0.45F;
                    f5 -= 0.45F;
                    f2 *= 0.9F;
                    f61 += 0.03F;
                    ++i;
                }

                BufferUploader.drawWithShader(buffer.end());

                p_114488_.popPose();
            }

            for (int k = 0; k < sides; k++) {
                p_114488_.pushPose();
                p_114488_.translate(0, 0.05f, 0);
                float f2 = 0.5F;
                float f3 = 0.0F;
                float f4 = 0.2f;
                float f5 = (float) (0.0);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                float f61 = 0.0F;
                int i = 0;

                p_114488_.mulPose(Axis.YP.rotationDegrees((360f / (float) sides) * k));
                p_114488_.translate(0, 0, 2.3f * s1);

                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

                while (f4 > 0.0F) {
                    RenderSystem._setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
                    float f71 = icon.getU0();
                    float f81 = icon.getV0();
                    float f9 = icon.getU1();
                    float f10 = icon.getV1();

                    if (i / 2 % 2 == 0) {
                        float f11 = f9;
                        f9 = f71;
                        f71 = f11;
                    }

                    buffer.vertex(p_114488_.last().pose(), (f2 - f3), (0.0F - f5), f61).uv(f9, f10).endVertex();
                    buffer.vertex(p_114488_.last().pose(), (-f2 - f3), (0.0F - f5), f61).uv(f71, f10).endVertex();
                    buffer.vertex(p_114488_.last().pose(), (-f2 - f3), (height - f5), f61).uv(f71, f81).endVertex();
                    buffer.vertex(p_114488_.last().pose(), (f2 - f3), (height - f5), f61).uv(f9, f81).endVertex();
                    f4 -= 0.45F;
                    f5 -= 0.45F;
                    f2 *= 0.9F;
                    f61 += 0.03F;
                    ++i;
                }

                BufferUploader.drawWithShader(buffer.end());

                p_114488_.popPose();
            }
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FireRingConstruct p_114482_) {
        return null;
    }
}
