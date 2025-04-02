package com.electroblob.wizardry.client.renderer.entity;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.content.entity.construct.IceSpikeConstruct;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class IceSpikeRenderer extends EntityRenderer<IceSpikeConstruct> {
    private static final ResourceLocation texture = new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/ice_spike.png");

    public IceSpikeRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
    }

    @Override
    public void render(IceSpikeConstruct entity, float p_114486_, float p_114487_, PoseStack p_114488_, MultiBufferSource p_114489_, int p_114490_) {
        p_114488_.pushPose();
        RenderSystem.enableDepthTest();
        p_114488_.mulPose(Axis.YP.rotationDegrees(entity.getYRot() - 90.0F));
        p_114488_.mulPose(Axis.ZP.rotationDegrees(entity.getXRot() - 90));

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(p_114488_.last().pose(), 0, 0, 0.5f).uv(1, 1).endVertex();
        buffer.vertex(p_114488_.last().pose(), 0, 1, 0.5f).uv(1, 0).endVertex();
        buffer.vertex(p_114488_.last().pose(), 0, 1, -0.5f).uv(0, 0).endVertex();
        buffer.vertex(p_114488_.last().pose(), 0, 0, -0.5f).uv(0, 1).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(p_114488_.last().pose(), 0.5f, 0, 0).uv(1, 1).endVertex();
        buffer.vertex(p_114488_.last().pose(), 0.5f, 1, 0).uv(1, 0).endVertex();
        buffer.vertex(p_114488_.last().pose(), -0.5f, 1, 0).uv(0, 0).endVertex();
        buffer.vertex(p_114488_.last().pose(), -0.5f, 0, 0).uv(0, 1).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(p_114488_.last().pose(), 0, 0, -0.5f).uv(0, 1).endVertex();
        buffer.vertex(p_114488_.last().pose(), 0, 1, -0.5f).uv(0, 0).endVertex();
        buffer.vertex(p_114488_.last().pose(), 0, 1, 0.5f).uv(1, 0).endVertex();
        buffer.vertex(p_114488_.last().pose(), 0, 0, 0.5f).uv(1, 1).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(p_114488_.last().pose(), -0.5f, 0, 0).uv(0, 1).endVertex();
        buffer.vertex(p_114488_.last().pose(), -0.5f, 1, 0).uv(0, 0).endVertex();
        buffer.vertex(p_114488_.last().pose(), 0.5f, 1, 0).uv(1, 0).endVertex();
        buffer.vertex(p_114488_.last().pose(), 0.5f, 0, 0).uv(1, 1).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        p_114488_.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull IceSpikeConstruct var1) {
        return texture;
    }
}
