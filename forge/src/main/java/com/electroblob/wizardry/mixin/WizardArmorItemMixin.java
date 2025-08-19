package com.electroblob.wizardry.mixin;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.client.model.armor.RobeArmorModel;
import com.electroblob.wizardry.client.model.armor.WizardArmorModel;
import com.electroblob.wizardry.content.item.WizardArmorItem;
import com.electroblob.wizardry.content.item.WizardArmorType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Consumer;

@SuppressWarnings("all")
@Mixin(WizardArmorItem.class)
public abstract class WizardArmorItemMixin extends ArmorItem {
    @Shadow private Element element;
    @Unique
    WizardArmorItem wizardArmorItem = (WizardArmorItem) (Object)this;

    public WizardArmorItemMixin(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                HumanoidModel<?> model;
                WizardArmorModel<?> wizardArmor = new WizardArmorModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(WizardArmorModel.LAYER_LOCATION));
                RobeArmorModel<?> robeArmor = new RobeArmorModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(RobeArmorModel.LAYER_LOCATION), true);

                if(wizardArmorItem.getEquipmentSlot() == EquipmentSlot.LEGS){
                    return original;
                }

                wizardArmor.hat.visible = equipmentSlot == EquipmentSlot.HEAD;
                wizardArmor.armorBody.visible = equipmentSlot == EquipmentSlot.CHEST;
                wizardArmor.robe.visible = equipmentSlot == EquipmentSlot.CHEST;
                wizardArmor.left_arm.visible = equipmentSlot == EquipmentSlot.CHEST;
                wizardArmor.right_arm.visible = equipmentSlot == EquipmentSlot.CHEST;
                wizardArmor.rightLeg.visible = equipmentSlot == EquipmentSlot.LEGS;
                wizardArmor.leftLeg.visible = equipmentSlot == EquipmentSlot.LEGS;
                wizardArmor.right_shoe.visible = equipmentSlot == EquipmentSlot.FEET;
                wizardArmor.left_shoe.visible = equipmentSlot == EquipmentSlot.FEET;

                robeArmor.armorHead.visible = equipmentSlot == EquipmentSlot.HEAD;
                robeArmor.armorBody.visible = equipmentSlot == EquipmentSlot.CHEST;
                robeArmor.robe.visible = equipmentSlot == EquipmentSlot.CHEST;
                robeArmor.left_arm.visible = equipmentSlot == EquipmentSlot.CHEST;
                robeArmor.right_arm.visible = equipmentSlot == EquipmentSlot.CHEST;
                robeArmor.rightLeg.visible = equipmentSlot == EquipmentSlot.LEGS;
                robeArmor.leftLeg.visible = equipmentSlot == EquipmentSlot.LEGS;
                robeArmor.right_shoe.visible = equipmentSlot == EquipmentSlot.FEET;
                robeArmor.left_shoe.visible = equipmentSlot == EquipmentSlot.FEET;

                switch (wizardArmorItem.getWizardArmorType()) {
                    case WIZARD:
                        model = wizardArmor;
                        break;
                    case SAGE:
                        model = wizardArmor;
                        break;
                    case BATTLEMAGE:
                        model = robeArmor;
                        break;
                    case WARLOCK:
                        model = robeArmor;
                        break;
                    default:
                        model = wizardArmor;
                        break;
                }
                return model;
            }
        });
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        String s = wizardArmorItem.getWizardArmorType().getName() + "_armor";

        if (WizardryMainMod.IS_THE_SEASON && wizardArmorItem.getWizardArmorType() == WizardArmorType.WIZARD) {
            s = s + "_festive";
        } else {
            if (this.element != null) s = s + "_" + this.element.getName();
        }

        String string = "ebwizardry:textures/armor/" + s + ".png";
        if(slot == EquipmentSlot.LEGS) {
            string = "ebwizardry:textures/armor/" + s + "_legs.png";
        }
        return string;
    }
}
