package com.binaris.wizardry.client.renderer.entity;//package com.electroblob.wizardry.client.renderer.entity;
//
//import com.electroblob.wizardry.WizardryMainMod;
//import com.electroblob.wizardry.common.content.entity.construct.EntityIceBarrier;
//import com.mojang.blaze3d.vertex.PoseStack;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.RenderType;
//import net.minecraft.client.renderer.entity.EntityRenderer;
//import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
//import net.minecraft.client.renderer.texture.OverlayTexture;
//import net.minecraft.resources.ResourceLocation;
//
//public class IceBarrierRenderer extends EntityRenderer<EntityIceBarrier> {
//    private static final ResourceLocation TEXTURE = new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/ice_barrier.png");
//
//    private ModelIceBarrier model;
//
//    public IceBarrierRenderer(Context p_174008_) {
//        super(p_174008_);
//        this.model = new ModelIceBarrier(p_174008_.bakeLayer(ModelIceBarrier.LAYER_LOCATION));
//    }
//
//    @Override
//    public void render(EntityIceBarrier entity, float p_114486_, float p_114487_, PoseStack p_114488_, MultiBufferSource p_114489_, int p_114490_) {
//        p_114488_.pushPose();
//        p_114488_.translate(0, entity.getBbHeight() / 2, 0);
//        p_114488_.mulPose(Vector3f.ZP.rotationDegrees(180));
//        p_114488_.mulPose(Vector3f.YP.rotationDegrees(p_114486_));
//        p_114488_.translate(0, -entity.getBbHeight() / 2 - 0.3, 0);
//
//        float s = entity.getSizeMultiplier();
//        p_114488_.scale(s, s, s);
//
//        this.model.renderToBuffer(p_114488_, p_114489_.getBuffer(RenderType.entityTranslucentCull(TEXTURE)), p_114490_, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
//
//        p_114488_.popPose();
//    }
//
//    @Override
//    public ResourceLocation getTextureLocation(EntityIceBarrier p_114482_) {
//        return TEXTURE;
//    }
//}
