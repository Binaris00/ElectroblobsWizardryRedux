package com.electroblob.wizardry.client.render;

import com.electroblob.wizardry.WizardryMainMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class SpectralArmorRenderer implements ArmorRenderer {
    private static final ResourceLocation ARMOR_TEXTURE = WizardryMainMod.location("textures/armor/spectral_armor.png");
    private static final ResourceLocation ARMOR_LEGS_TEXTURE = WizardryMainMod.location("textures/armor/spectral_armor_legs.png");

    private HumanoidModel<?> innerModel;
    private HumanoidModel<?> outerModel;

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, HumanoidModel<LivingEntity> contextModel) {
        if (innerModel == null) {
            innerModel = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
        }
        if (outerModel == null) {
            outerModel = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));
        }

        HumanoidModel<?> armorModel = slot == EquipmentSlot.LEGS ? innerModel : outerModel;

        contextModel.copyPropertiesTo((HumanoidModel<LivingEntity>) armorModel);
        ResourceLocation texture = slot == EquipmentSlot.LEGS ? ARMOR_LEGS_TEXTURE : ARMOR_TEXTURE;

        ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, armorModel, texture);
    }
}
