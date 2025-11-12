package com.electroblob.wizardry.mixin;

import com.electroblob.wizardry.content.item.SpectralArmorItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpectralArmorItem.class)
public abstract class SpectralArmorItemMixin extends ArmorItem {
    public SpectralArmorItemMixin(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        String string = "ebwizardry:textures/armor/spectral_armor.png";
        if(slot == EquipmentSlot.LEGS) {
            string = "ebwizardry:textures/armor/spectral_armor_legs.png";
        }
        return string;
    }
}
