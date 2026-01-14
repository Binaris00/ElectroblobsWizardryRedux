package com.binaris.wizardry.client.renderer.entity;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.client.model.IceGiantModel;
import com.binaris.wizardry.content.entity.living.IceGiant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class IceGiantRenderer extends MobRenderer<IceGiant, IceGiantModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            WizardryMainMod.MOD_ID, "textures/entity/ice_giant.png");

    public IceGiantRenderer(EntityRendererProvider.Context context) {
        super(context, new IceGiantModel(context.bakeLayer(IceGiantModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull IceGiant entity) {
        return TEXTURE;
    }

    @Override
    public void render(@NotNull IceGiant entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected void setupRotations(@NotNull IceGiant entity, @NotNull PoseStack poseStack, float ageInTicks,
                                  float rotationYaw, float partialTicks) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks);
        if (!((double) entity.walkAnimation.speed() < 0.01)) {
            float f = 13.0F;
            float f1 = entity.walkAnimation.position(partialTicks) + 6.0F;
            float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
            poseStack.mulPose(Axis.ZP.rotationDegrees(6.5F * f2));
        }
    }
}