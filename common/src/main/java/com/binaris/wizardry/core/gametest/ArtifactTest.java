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
    }

    public static void arcaneDefenseAmuletRecharge(GameTestHelper helper) {
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
    }

//    public static void autoSmeltCharmEffect(GameTestHelper helper) {
//        Player player = GST.mockServerPlayer(helper, PLAYER_POS);
//        ItemStack charm = new ItemStack(EBItems.CHARM_AUTO_SMELT.get());
//        ItemStack wand = new ItemStack(EBItems.NOVICE_WAND.get());
//
//        // Add charm and wand to player's hotbar
//        player.getInventory().items.set(0, charm);
//        player.getInventory().items.set(1, wand);
//
//        // Add smeltable items to player's inventory (64+ raw iron to trigger auto-smelt)
//        ItemStack rawIron = new ItemStack(Items.RAW_IRON, 64);
//        player.getInventory().items.set(2, rawIron);
//
//        // Set full mana on wand for casting
//        IManaStoringItem manaItem = (IManaStoringItem) wand.getItem();
//        manaItem.setMana(wand, manaItem.getManaCapacity(wand));
//
//        // Create an item entity to pick up
//        ItemStack pickupItem = new ItemStack(Items.STONE, 1);
//        ItemEntity itemEntity = new ItemEntity(
//                player.level(),
//                player.getX(),
//                player.getY(),
//                player.getZ(),
//                pickupItem
//        );
//        player.level().addFreshEntity(itemEntity);
//
//        // Simulate player touching the item (triggers playerTouch in mixin)
//        itemEntity.playerTouch(player);
//
//        // Check if the raw iron was smelted
//        ItemStack result = player.getInventory().items.get(2);
//        GST.assertEquals(helper, "Raw iron should be auto-smelted when picking up items",
//                Items.IRON_INGOT, result.getItem());
//    }
}
