package com.binaris.wizardry.client.renderer.entity;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.client.model.RemnantModel;
import com.binaris.wizardry.content.entity.living.Remnant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class RemnantRenderer extends EntityRenderer<Remnant> {
    private static final ResourceLocation TEX = new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/remnant.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEX);

    private final RemnantModel model;

    public RemnantRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.5f;
        this.model = new RemnantModel(ctx.bakeLayer(RemnantModel.LAYER_LOCATION));
    }

    @Override
    public void render(Remnant entity, float entityYaw, float partialTicks,
                       PoseStack ps, MultiBufferSource buffer, int packedLight) {

        float age = entity.tickCount + partialTicks;
        float expansion = 0.9f;
        if (entity.deathTime > 0) {
            float f = (entity.deathTime + partialTicks) * 0.25f;
            f = 1f - 1f / (f + 1f);
            expansion -= f * 0.75f;
        } else if (entity.hurtTime > 0) {
            float f = (entity.hurtTime - partialTicks) / entity.hurtDuration;
            f = Mth.sin(f * f * f * f * (float) Math.PI);
            expansion += f * 0.2f;
        }
        float rotationSpeed = entity.isSwimming() ? 10f : 3f;

        ps.pushPose();
        ps.translate(0, entity.getBbHeight() / 2.0, 0);
        ps.pushPose();
        ps.scale(expansion, expansion, expansion);
        ps.mulPose(Axis.YP.rotationDegrees(age * rotationSpeed / 2f));
        ps.mulPose(new Quaternionf().setAngleAxis((float) Math.toRadians(60),
                (float) Math.sin(Math.PI / 4), 0, (float) Math.sin(Math.PI / 4)));
        ps.mulPose(Axis.YP.rotationDegrees(age * rotationSpeed));
        VertexConsumer vb = buffer.getBuffer(RENDER_TYPE);
        this.model.renderToBuffer(ps, vb, packedLight,
                OverlayTexture.NO_OVERLAY,
                1f, 1f, 1f, 1f);
        ps.popPose();

        ps.pushPose();
        ps.scale(0.875f, 0.875f, 0.875f);
        ps.mulPose(new Quaternionf().setAngleAxis((float) Math.toRadians(60),
                (float) Math.sin(Math.PI / 4), 0, (float) Math.sin(Math.PI / 4)));
        ps.mulPose(Axis.YP.rotationDegrees(age * rotationSpeed));
        this.model.renderToBuffer(ps, vb, packedLight,
                OverlayTexture.NO_OVERLAY,
                1f, 1f, 1f, 1f);
        ps.popPose();

        ps.popPose();

        super.render(entity, entityYaw, partialTicks, ps, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Remnant entity) {
        return TEX;
    }

    @Override
    public boolean shouldRender(Remnant livingEntity, Frustum camera,
                                double camX, double camY, double camZ) {
        return super.shouldRender(livingEntity, camera, camX, camY, camZ);
    }
}