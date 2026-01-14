package com.binaris.wizardry.mixin;

import com.binaris.wizardry.client.BlendingHumanoidModel;
import com.binaris.wizardry.content.item.SpectralArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Consumer;

@Mixin(SpectralArmorItem.class)
public abstract class SpectralArmorItemMixin extends ArmorItem {
    @Unique
    private final SpectralArmorItem wizardry$spectralArmorItem = (SpectralArmorItem) (Object) this;

    public SpectralArmorItemMixin(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @Nullable HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                ModelPart innerPart = Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR);
                ModelPart outerPart = Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR);

                HumanoidModel<LivingEntity> innerModel = new BlendingHumanoidModel(innerPart);
                HumanoidModel<LivingEntity> outerModel = new BlendingHumanoidModel(outerPart);

                if (wizardry$spectralArmorItem.getEquipmentSlot() == EquipmentSlot.LEGS) {
                    return original;
                }

                HumanoidModel<?> chosen = equipmentSlot == EquipmentSlot.LEGS ? innerModel : outerModel;
                chosen.setAllVisible(false);
                switch (equipmentSlot) {
                    case HEAD -> {
                        chosen.head.visible = true;
                        chosen.hat.visible = true;
                    }
                    case CHEST -> {
                        chosen.body.visible = true;
                        chosen.rightArm.visible = true;
                        chosen.leftArm.visible = true;
                    }
                    case LEGS, FEET -> {
                        chosen.rightLeg.visible = true;
                        chosen.leftLeg.visible = true;
                    }
                }

                return chosen;
            }
        });
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        String string = "ebwizardry:textures/armor/spectral_armor.png";
        if (slot == EquipmentSlot.LEGS) {
            string = "ebwizardry:textures/armor/spectral_armor_legs.png";
        }
        return string;
    }
}
