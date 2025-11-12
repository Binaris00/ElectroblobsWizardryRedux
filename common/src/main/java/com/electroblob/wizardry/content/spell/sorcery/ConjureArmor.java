package com.electroblob.wizardry.content.spell.sorcery;

import com.electroblob.wizardry.api.content.data.ConjureData;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.util.InventoryUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.ConjureItemSpell;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class ConjureArmor extends ConjureItemSpell {
    private static final Map<EquipmentSlot, Item> SPECTRAL_ARMOUR_MAP = ImmutableMap.of(
            EquipmentSlot.HEAD, EBItems.SPECTRAL_HELMET.get(),
            EquipmentSlot.CHEST, EBItems.SPECTRAL_CHESTPLATE.get(),
            EquipmentSlot.LEGS, EBItems.SPECTRAL_LEGGINGS.get(),
            EquipmentSlot.FEET, EBItems.SPECTRAL_BOOTS.get());


    public ConjureArmor() {
        super(EBItems.SPECTRAL_HELMET.get());
        registerSupportedItem(EBItems.SPECTRAL_CHESTPLATE.get());
        registerSupportedItem(EBItems.SPECTRAL_LEGGINGS.get());
        registerSupportedItem(EBItems.SPECTRAL_BOOTS.get());
    }

    @Override
    protected boolean conjureItem(PlayerCastContext ctx) {
        ItemStack armor;
        boolean flag = false;

        for(EquipmentSlot slot : InventoryUtil.ARMOR_SLOTS){
            // Player already has this piece of armor, skip to next slot
            if(!ctx.caster().getItemBySlot(slot).isEmpty() && InventoryUtil.doesPlayerHaveItem(ctx.caster(), SPECTRAL_ARMOUR_MAP.get(slot))){
                continue;
            }

            armor = new ItemStack(SPECTRAL_ARMOUR_MAP.get(slot));
            armor = addItemExtras(ctx, armor);

            ConjureData data = Services.OBJECT_DATA.getConjureData(armor);
            int duration = property(DefaultProperties.ITEM_LIFETIME);
            long currentGameTime = ctx.world().getGameTime();
            data.setExpireTime(currentGameTime + duration);
            data.setDuration(duration);
            data.setSummoned(true);
            ctx.caster().setItemSlot(slot, armor);
            flag = true;
        }

        return flag;
    }
}
