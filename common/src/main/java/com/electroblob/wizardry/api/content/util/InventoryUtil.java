package com.electroblob.wizardry.api.content.util;

import com.electroblob.wizardry.api.content.item.IManaStoringItem;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.content.item.WizardArmorItem;
import com.electroblob.wizardry.content.item.WizardArmorType;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryUtil {
    public static final EquipmentSlot[] ARMOUR_SLOTS;

    static {
        List<EquipmentSlot> slots = new ArrayList<>(Arrays.asList(EquipmentSlot.values()));
        slots.removeIf(slot -> slot.getType() != EquipmentSlot.Type.ARMOR);
        ARMOUR_SLOTS = slots.toArray(new EquipmentSlot[0]);
    }

    public static List<ItemStack> getPrioritisedHotBarAndOffhand(Player player) {
        List<ItemStack> hotbar = getHotbar(player);
        hotbar.add(0, player.getOffhandItem());
        hotbar.remove(player.getMainHandItem());
        hotbar.add(0, player.getMainHandItem());
        return hotbar;
    }

    public static boolean doesPlayerHaveItem(Player player, Item item) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() == item) {
                return true;
            }
        }

        for (ItemStack stack : player.getInventory().armor) {
            if (stack.getItem() == item) {
                return true;
            }
        }

        for (ItemStack stack : player.getInventory().offhand) {
            if (stack.getItem() == item) {
                return true;
            }
        }

        return false;
    }

    public static List<ItemStack> getHotbar(Player player) {
        NonNullList<ItemStack> hotBar = NonNullList.create();
        hotBar.addAll(player.getInventory().items.subList(0, 9));
        return hotBar;
    }

    public static boolean isWearingFullSet(LivingEntity entity, @Nullable Element element, @Nullable WizardArmorType armor){
        ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
        if(!(helmet.getItem() instanceof WizardArmorItem wizardArmor)) return false;

        Element e = element == null ? wizardArmor.getElement() : element;
        WizardArmorType ac = armor == null ? wizardArmor.getWizardArmorType() : armor;
        return Arrays.stream(ARMOUR_SLOTS)
                .allMatch(slot -> entity.getItemBySlot(slot).getItem() instanceof WizardArmorItem armor2
                        && armor2.getElement() == e
                        && armor2.getWizardArmorType() == ac);
    }

    public static boolean doAllArmourPiecesHaveMana(LivingEntity entity){
        return Arrays.stream(ARMOUR_SLOTS).noneMatch(s -> entity.getItemBySlot(s).getItem() instanceof IManaStoringItem manaStoringItem
                && manaStoringItem.isManaEmpty(entity.getItemBySlot(s)));
    }
}
