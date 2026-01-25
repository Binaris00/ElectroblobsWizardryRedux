package com.binaris.wizardry.core.gametest;

import com.binaris.wizardry.api.content.item.ArtefactItem;
import com.binaris.wizardry.api.content.item.IManaStoringItem;
import com.binaris.wizardry.content.item.artifact.ArcaneDefenseAmuletEffect;
import com.binaris.wizardry.content.item.artifact.CondensingRingEffect;
import com.binaris.wizardry.setup.registries.EBItems;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ArtifactTest {
    private static final Vec3 PLAYER_POS = new Vec3(1, 2.0, 1);

    public static void condensingRingRecharge(GameTestHelper helper) {
        Player player = GST.mockServerPlayer(helper, PLAYER_POS);
        ItemStack ring = new ItemStack(EBItems.RING_CONDENSING.get());
        ItemStack wand = new ItemStack(EBItems.NOVICE_WAND.get());
        IManaStoringItem manaItem = (IManaStoringItem) wand.getItem();

        // Set wand with reduced mana
        manaItem.setMana(wand, manaItem.getManaCapacity(wand) - 2);
        player.getInventory().items.set(player.getInventory().selected, wand);
        player.getInventory().items.set(player.getInventory().selected + 1, ring);

        player.tickCount = CondensingRingEffect.MANA_RECHARGE_INTERVAL_TICKS; // Simulate ticks

        ((ArtefactItem) ring.getItem()).getEffect().onTick(player, player.level(), ring);

        int currentMana = ((IManaStoringItem) player.getInventory().getSelected().getItem()).getMana(player.getInventory().getSelected());
        int expectedMana = manaItem.getManaCapacity(wand) - 1;
        GST.assertEquals(helper, "Wand should recharge +1 mana", expectedMana, currentMana);
        helper.succeed();
    }

    public static void condensingRingRechargeMultipleItems(GameTestHelper helper) {
        Player player = GST.mockServerPlayer(helper, PLAYER_POS);
        ItemStack ring = new ItemStack(EBItems.RING_CONDENSING.get());
        ItemStack wand1 = new ItemStack(EBItems.NOVICE_WAND.get());
        ItemStack wand2 = new ItemStack(EBItems.APPRENTICE_WAND.get());

        IManaStoringItem manaItem1 = (IManaStoringItem) wand1.getItem();
        IManaStoringItem manaItem2 = (IManaStoringItem) wand2.getItem();

        manaItem1.setMana(wand1, manaItem1.getManaCapacity(wand1) - 5);
        manaItem2.setMana(wand2, manaItem2.getManaCapacity(wand2) - 5);

        player.getInventory().items.set(0, wand1);
        player.getInventory().items.set(1, wand2);
        player.getInventory().items.set(2, ring);

        player.tickCount = CondensingRingEffect.MANA_RECHARGE_INTERVAL_TICKS; // Simulate ticks
        ((ArtefactItem) ring.getItem()).getEffect().onTick(player, player.level(), ring);

        GST.assertEquals(helper, "Wand 1 should recharge +1",
                manaItem1.getManaCapacity(wand1) - 4, manaItem1.getMana(wand1));
        GST.assertEquals(helper, "Wand 2 should recharge +1",
                manaItem2.getManaCapacity(wand2) - 4, manaItem2.getMana(wand2));
        helper.succeed();
    }

    public static void arcaneDefenseAmuletRecharge(GameTestHelper helper) {
        Player player = GST.mockServerPlayer(helper, PLAYER_POS);
        ItemStack amulet = new ItemStack(EBItems.AMULET_ARCANE_DEFENCE.get());
        ItemStack wizardHat = new ItemStack(EBItems.WIZARD_HAT.get());

        IManaStoringItem manaArmor = (IManaStoringItem) wizardHat.getItem();
        manaArmor.setMana(wizardHat, manaArmor.getManaCapacity(wizardHat) - 3);

        player.setItemSlot(EquipmentSlot.HEAD, wizardHat);
        player.getInventory().items.set(0, amulet);

        player.tickCount = ArcaneDefenseAmuletEffect.MANA_RECHARGE_INTERVAL_TICKS; // Simulate ticks
        ((ArtefactItem) amulet.getItem()).getEffect().onTick(player, player.level(), amulet);

        GST.assertEquals(helper, "Hat should recharge +1",
                manaArmor.getManaCapacity(wizardHat) - 2, manaArmor.getMana(wizardHat));
        helper.succeed();
    }

    public static void arcaneDefenseAmuletRechargesMultipleArmor(GameTestHelper helper) {
        Player player = GST.mockServerPlayer(helper, PLAYER_POS);
        ItemStack amulet = new ItemStack(EBItems.AMULET_ARCANE_DEFENCE.get());
        ItemStack wizardHat = new ItemStack(EBItems.WIZARD_HAT.get());
        ItemStack wizardRobe = new ItemStack(EBItems.WIZARD_ROBE.get());
        ItemStack wizardBoots = new ItemStack(EBItems.WIZARD_BOOTS.get());

        IManaStoringItem manaHat = (IManaStoringItem) wizardHat.getItem();
        IManaStoringItem manaRobe = (IManaStoringItem) wizardRobe.getItem();
        IManaStoringItem manaBoots = (IManaStoringItem) wizardBoots.getItem();

        manaHat.setMana(wizardHat, manaHat.getManaCapacity(wizardHat) - 5);
        manaRobe.setMana(wizardRobe, manaRobe.getManaCapacity(wizardRobe) - 5);
        manaBoots.setMana(wizardBoots, manaBoots.getManaCapacity(wizardBoots) - 5);

        player.setItemSlot(EquipmentSlot.HEAD, wizardHat);
        player.setItemSlot(EquipmentSlot.CHEST, wizardRobe);
        player.setItemSlot(EquipmentSlot.FEET, wizardBoots);
        player.getInventory().items.set(0, amulet);

        player.tickCount = ArcaneDefenseAmuletEffect.MANA_RECHARGE_INTERVAL_TICKS; // Simulate ticks
        ((ArtefactItem) amulet.getItem()).getEffect().onTick(player, player.level(), amulet);

        GST.assertEquals(helper, "Hat must recharge +1",
                manaHat.getManaCapacity(wizardHat) - 4, manaHat.getMana(wizardHat));
        GST.assertEquals(helper, "Robe must recharge +1",
                manaRobe.getManaCapacity(wizardRobe) - 4, manaRobe.getMana(wizardRobe));
        GST.assertEquals(helper, "Boots must recharge +1",
                manaBoots.getManaCapacity(wizardBoots) - 4, manaBoots.getMana(wizardBoots));
        helper.succeed();
    }
}
