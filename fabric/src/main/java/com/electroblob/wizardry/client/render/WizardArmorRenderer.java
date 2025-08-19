package com.electroblob.wizardry.client.render;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.client.model.armor.RobeArmorModel;
import com.electroblob.wizardry.client.model.armor.WizardArmorModel;
import com.electroblob.wizardry.content.item.WizardArmorItem;
import com.electroblob.wizardry.content.item.WizardArmorType;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class WizardArmorRenderer implements ArmorRenderer {
    private static final Map<Item, HumanoidModel<?>> models = new HashMap<>();

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, ItemStack stack, LivingEntity livingEntity, EquipmentSlot slot, int i, HumanoidModel<LivingEntity> humanoidModel) {
        if(!(stack.getItem() instanceof WizardArmorItem wizardItem)) return;
        HumanoidModel<?> armorModel;

        WizardArmorModel<?> wizardArmor = new WizardArmorModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(WizardArmorModel.LAYER_LOCATION));

        wizardArmor.hat.visible = slot == EquipmentSlot.HEAD;
        wizardArmor.armorBody.visible = slot == EquipmentSlot.CHEST;
        wizardArmor.robe.visible = slot == EquipmentSlot.CHEST;
        wizardArmor.left_arm.visible = slot == EquipmentSlot.CHEST;
        wizardArmor.right_arm.visible = slot == EquipmentSlot.CHEST;
        wizardArmor.rightLeg.visible = slot == EquipmentSlot.LEGS;
        wizardArmor.leftLeg.visible = slot == EquipmentSlot.LEGS;
        wizardArmor.right_shoe.visible = slot == EquipmentSlot.FEET;
        wizardArmor.left_shoe.visible = slot == EquipmentSlot.FEET;

        RobeArmorModel<?> robeArmor = new RobeArmorModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(RobeArmorModel.LAYER_LOCATION), true);

        robeArmor.armorHead.visible = slot == EquipmentSlot.HEAD;
        robeArmor.armorBody.visible = slot == EquipmentSlot.CHEST;
        robeArmor.robe.visible = slot == EquipmentSlot.CHEST;
        robeArmor.left_arm.visible = slot == EquipmentSlot.CHEST;
        robeArmor.right_arm.visible = slot == EquipmentSlot.CHEST;
        robeArmor.rightLeg.visible = slot == EquipmentSlot.LEGS;
        robeArmor.leftLeg.visible = slot == EquipmentSlot.LEGS;
        robeArmor.right_shoe.visible = slot == EquipmentSlot.FEET;
        robeArmor.left_shoe.visible = slot == EquipmentSlot.FEET;

        armorModel = models.computeIfAbsent(wizardItem, key -> {
            if(wizardItem.getWizardArmorType() == WizardArmorType.WIZARD) return wizardArmor;
            else if(wizardItem.getWizardArmorType() == WizardArmorType.SAGE) return wizardArmor;
            else if(wizardItem.getWizardArmorType() == WizardArmorType.BATTLEMAGE) return robeArmor;
            else if(wizardItem.getWizardArmorType() == WizardArmorType.WARLOCK) return robeArmor;
            return null;
        });

        if(armorModel == null) return;

        humanoidModel.copyPropertiesTo((HumanoidModel<LivingEntity>) armorModel);

        ArmorRenderer.renderPart(poseStack, multiBufferSource, i, stack, armorModel,
                WizardryMainMod.location(getArmorTexture(wizardItem, slot)));
    }

    public static String getArmorTexture(WizardArmorItem wizardArmorItem, EquipmentSlot slot) {
        String s = wizardArmorItem.getWizardArmorType().getName() + "_armor";

        if (WizardryMainMod.IS_THE_SEASON && wizardArmorItem.getWizardArmorType() == WizardArmorType.WIZARD) {
            s = s + "_festive";
        } else {
            if (wizardArmorItem.getElement() != null) s = s + "_" + wizardArmorItem.getElement().getName();
        }

        String string = "textures/armor/" + s + ".png";
        if(slot == EquipmentSlot.LEGS) {
            string = "textures/armor/" + s + "_legs.png";
        }
        return string;
    }
}
