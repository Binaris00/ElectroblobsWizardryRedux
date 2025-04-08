package com.electroblob.wizardry.client;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.client.render.WizardArmorRenderer;
import com.electroblob.wizardry.content.item.WizardArmorItem;
import com.electroblob.wizardry.setup.registries.EBItems;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EBArmorRenderFabric {
    private static final Map<Item, HumanoidModel<?>> models = new HashMap<>();

    public static void load(){
        EBItems.getArmors().forEach((item) -> {
            if(item.get() instanceof ArmorItem armorItem && armorItem.getEquipmentSlot() != EquipmentSlot.LEGS) ArmorRenderer.register(new WizardArmorRenderer(), item.get());
        });

        List<ItemLike> leggings = new ArrayList<>();
        EBItems.getLeggings().forEach(item -> leggings.add(item.get()));
        ArmorRenderer.register((matrices, vertexConsumers, stack, entity, slot, light, model) -> {
            HumanoidModel<?> armorModel;
            if(!(stack.getItem() instanceof WizardArmorItem wizardArmorItem)) return;


            if(!models.containsKey(stack.getItem()))
                models.put(stack.getItem(), new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)));

            armorModel = models.get(stack.getItem());
            model.copyPropertiesTo((HumanoidModel<LivingEntity>) armorModel);
            armorModel.setAllVisible(false);
            armorModel.leftLeg.visible = slot == EquipmentSlot.LEGS;
            armorModel.rightLeg.visible = slot == EquipmentSlot.LEGS;
            ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, armorModel,
                    WizardryMainMod.location(WizardArmorRenderer.getArmorTexture(wizardArmorItem, slot)));
        }, leggings.toArray(new ItemLike[0]));
    }
}
