package com.electroblob.wizardry.client.renderer.entity;

import com.electroblob.wizardry.Wizardry;
import com.electroblob.wizardry.common.content.entity.projectile.ConjuredArrow;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ConjureArrowRenderer extends ArrowRenderer<ConjuredArrow> {
    public ConjureArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ConjuredArrow entity) {
        return new ResourceLocation(Wizardry.MOD_ID, "textures/entity/conjured_arrow.png");
    }

    @Override
    public void render(ConjuredArrow abstractArrow, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        super.render(abstractArrow, f, g, poseStack, multiBufferSource, i);
    }
}
