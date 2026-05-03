package com.binaris.wizardry.core.gametest;

import com.binaris.wizardry.api.content.item.IManaItem;
import com.binaris.wizardry.content.item.armor.WizardArmorItem;
import com.binaris.wizardry.setup.registries.EBItems;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class WizardArmorTestHandler {
    private static final Vec3 PLAYER_POS = new Vec3(1.5, 2.0, 1.5);

    public static void armorNeverBreaks(GameTestHelper helper) {
        ItemStack stack = EBItems.WIZARD_BOOTS_FIRE.get().getDefaultInstance();
        IManaItem manaItem = (IManaItem) stack.getItem();
        Player player = GST.mockPlayer(helper, PLAYER_POS);

        stack.hurtAndBreak(9999, player, (a) -> {});

        GST.assertFalse(helper, "Armor should never be destroyed regardless of damage applied", stack.isEmpty());
        GST.assertTrue(helper, "after applied a lot of damage, the armor mana should be 0", manaItem.getMana(stack) == 0);

        helper.succeed();
    }

    public static void armorAttributesWithMana(GameTestHelper helper) {
        ItemStack stack = EBItems.WIZARD_BOOTS.get().getDefaultInstance();
        WizardArmorItem armorItem = (WizardArmorItem) stack.getItem();
        var modifiers = armorItem.getCustomAttributes(stack, EquipmentSlot.FEET);
        GST.assertFalse(helper, "Armor should provide protection attributes when mana is full", modifiers.get(Attributes.ARMOR).isEmpty());
        helper.succeed();
    }

    public static void armorNoAttributesWithoutMana(GameTestHelper helper) {
        ItemStack stack = EBItems.WIZARD_HAT.get().getDefaultInstance();
        WizardArmorItem armorItem = (WizardArmorItem) stack.getItem();
        IManaItem manaItem = (IManaItem) stack.getItem();
        manaItem.setMana(stack, 0);

        var modifiers = armorItem.getCustomAttributes(stack, EquipmentSlot.HEAD);

        GST.assertTrue(helper, "Armor should NOT provide protection attributes when mana is 0", modifiers.get(Attributes.ARMOR).isEmpty());

        helper.succeed();
    }

    private WizardArmorTestHandler() {
    }
}