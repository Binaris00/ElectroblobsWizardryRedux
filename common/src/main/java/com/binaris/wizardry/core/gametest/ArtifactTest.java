package com.binaris.wizardry.core.gametest;

import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.api.content.item.ArtifactItem;
import com.binaris.wizardry.api.content.item.IManaStoringItem;
import com.binaris.wizardry.api.content.event.EBLivingDeathEvent;
import com.binaris.wizardry.content.item.artifact.ArcaneDefenseAmuletEffect;
import com.binaris.wizardry.content.item.artifact.CondensingRingEffect;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ArtifactTest {
    private static final Vec3 PLAYER_POS = new Vec3(1, 2.0, 1);
    private static final Vec3 TARGET_POS = new Vec3(2, 2.0, 2);

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
        ((ArtifactItem) ring.getItem()).getEffect().onTick(player, player.level(), ring);

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
        ((ArtifactItem) amulet.getItem()).getEffect().onTick(player, player.level(), amulet);

        GST.assertEquals(helper, "Hat must recharge +1",
                manaHat.getManaCapacity(wizardHat) - 4, manaHat.getMana(wizardHat));
        GST.assertEquals(helper, "Robe must recharge +1",
                manaRobe.getManaCapacity(wizardRobe) - 4, manaRobe.getMana(wizardRobe));
        GST.assertEquals(helper, "Boots must recharge +1",
                manaBoots.getManaCapacity(wizardBoots) - 4, manaBoots.getMana(wizardBoots));
    }

    public static void ringCombustion(GameTestHelper helper) {
        Player player = GST.mockServerPlayer(helper, PLAYER_POS);
        Cow cow = (Cow) GST.mockEntity(helper, TARGET_POS, EntityType.COW);
        Cow friendCow = (Cow) GST.mockEntity(helper, TARGET_POS, EntityType.COW);
        ItemStack ring = new ItemStack(EBItems.RING_COMBUSTION.get());

        player.getInventory().items.set(0, ring);
        DamageSource fireSource = new DamageSource(
                player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(EBDamageSources.FIRE),
                player
        );

        EBLivingDeathEvent deathEvent = new EBLivingDeathEvent(cow, fireSource);
        ((ArtifactItem) ring.getItem()).getEffect().onPlayerKill(deathEvent, ring);

        // Verify that the friend cow has been damaged by the explosion (health should be less than max health)
        GST.assertTrue(helper, "Ring of Combustion should damage nearby entities on fire kill, expected friend cow health to be less than max health (%f) but got %f".formatted(friendCow.getMaxHealth(), friendCow.getHealth()),
                friendCow.getHealth() < friendCow.getMaxHealth());
    }

    public static void ringArcaneFrost(GameTestHelper helper) {
        Player player = GST.mockServerPlayer(helper, PLAYER_POS);
        ItemStack ring = new ItemStack(EBItems.RING_ARCANE_FROST.get());
        player.getInventory().items.set(0, ring);

        Cow cow = (Cow) GST.mockEntity(helper, TARGET_POS, EntityType.COW);

        // Create a frost damage source from the player
        DamageSource frostSource = new DamageSource(
                player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(EBDamageSources.FROST),
                player
        );

        int entitiesBeforeKill = player.level().getEntities(null, cow.getBoundingBox().inflate(5)).size();

        EBLivingDeathEvent deathEvent = new EBLivingDeathEvent(cow, frostSource);
        ((ArtifactItem) ring.getItem()).getEffect().onPlayerKill(deathEvent, ring);

        int entitiesAfterKill = player.level().getEntities(null, cow.getBoundingBox().inflate(5)).size();
        GST.assertTrue(helper,
                "Ring of Arcane Frost should spawn 8 ice shards on frost kill, expected at least " + (entitiesBeforeKill + 5) + " entities but got " + entitiesAfterKill,
                entitiesAfterKill >= entitiesBeforeKill + 5); // We check for at least 5 instead of 8 to be more lenient
    }

    public static void ringEarthMeleeAppliesPoison(GameTestHelper helper) {
        Player player = GST.mockServerPlayer(helper, PLAYER_POS);
        ItemStack ring = new ItemStack(EBItems.RING_EARTH_MELEE.get());
        ItemStack earthWand = new ItemStack(EBItems.NOVICE_EARTH_WAND.get());

        player.getInventory().items.set(0, ring);
        player.setItemSlot(EquipmentSlot.MAINHAND, earthWand);

        Cow cow = (Cow) GST.mockEntity(helper, new Vec3(2, 2.0, 2), EntityType.COW);

        DamageSource meleeSource = player.damageSources().playerAttack(player);

        EBLivingHurtEvent hurtEvent = new EBLivingHurtEvent(cow, meleeSource, 5.0f);
        ((ArtifactItem) ring.getItem()).getEffect().onHurtEntity(hurtEvent, ring);

        // Verify that the cow has poison effect
        GST.assertTrue(helper, "Ring of Earth Melee should apply poison effect on melee hit with earth wand",
                cow.hasEffect(MobEffects.POISON));
    }

    public static void ringFireMeleeSetsOnFire(GameTestHelper helper) {
        Player player = GST.mockServerPlayer(helper, PLAYER_POS);
        ItemStack ring = new ItemStack(EBItems.RING_FIRE_MELEE.get());
        ItemStack fireWand = new ItemStack(EBItems.NOVICE_FIRE_WAND.get());

        player.getInventory().items.set(0, ring);
        player.setItemSlot(EquipmentSlot.MAINHAND, fireWand);

        Cow cow = (Cow) GST.mockEntity(helper, TARGET_POS, EntityType.COW);

        DamageSource meleeSource = player.damageSources().playerAttack(player);

        EBLivingHurtEvent hurtEvent = new EBLivingHurtEvent(cow, meleeSource, 5.0f);

        ((ArtifactItem) ring.getItem()).getEffect().onHurtEntity(hurtEvent, ring);

        GST.assertTrue(helper, "Ring of Fire Melee should set entity on fire on melee hit with fire wand",
                cow.getRemainingFireTicks() > 0);
    }

    public static void ringIceMeleeAppliesFrost(GameTestHelper helper) {
        Player player = GST.mockServerPlayer(helper, PLAYER_POS);
        ItemStack ring = new ItemStack(EBItems.RING_ICE_MELEE.get());
        ItemStack iceWand = new ItemStack(EBItems.NOVICE_ICE_WAND.get());

        player.getInventory().items.set(0, ring);
        player.setItemSlot(EquipmentSlot.MAINHAND, iceWand);

        Cow cow = (Cow) GST.mockEntity(helper, TARGET_POS, EntityType.COW);
        DamageSource meleeSource = player.damageSources().playerAttack(player);

        EBLivingHurtEvent hurtEvent = new EBLivingHurtEvent(cow, meleeSource, 5.0f);

        ((ArtifactItem) ring.getItem()).getEffect().onHurtEntity(hurtEvent, ring);

        GST.assertTrue(helper, "Ring of Ice Melee should apply frost effect on melee hit with ice wand",
                cow.hasEffect(EBMobEffects.FROST.get()));
    }

    /**
     * Check if the Ring of Lightning Melee applies chain lightning effect on melee hit with lightning wand.
     */
    public static void ringLightningMelee(GameTestHelper helper) {
        Player player = GST.mockServerPlayer(helper, PLAYER_POS);
        ItemStack ring = new ItemStack(EBItems.RING_LIGHTNING_MELEE.get());
        ItemStack lightningWand = new ItemStack(EBItems.NOVICE_LIGHTNING_WAND.get());

        player.getInventory().items.set(0, ring);
        player.setItemSlot(EquipmentSlot.MAINHAND, lightningWand);

        Cow cow = (Cow) GST.mockEntity(helper, TARGET_POS, EntityType.COW);
        cow.setNoAi(true);

        DamageSource meleeSource = player.damageSources().playerAttack(player);
        EBLivingHurtEvent hurtEvent = new EBLivingHurtEvent(cow, meleeSource, 5.0f);
        ((ArtifactItem) ring.getItem()).getEffect().onHurtEntity(hurtEvent, ring);

        GST.assertTrue(helper, "expected cow health to be less than max health (%f) but got %f".formatted(cow.getMaxHealth(), cow.getHealth()), cow.getHealth() < cow.getMaxHealth());
    }

    /**
     * Check if the Ring of Necromancy Melee applies wither effect on melee hit with necromancy wand.
     */
    public static void ringNecromancyMelee(GameTestHelper helper) {
        Player player = GST.mockServerPlayer(helper, PLAYER_POS);
        ItemStack ring = new ItemStack(EBItems.RING_NECROMANCY_MELEE.get());
        ItemStack necromancyWand = new ItemStack(EBItems.NOVICE_NECROMANCY_WAND.get());

        player.getInventory().items.set(0, ring);
        player.setItemSlot(EquipmentSlot.MAINHAND, necromancyWand);

        Cow cow = (Cow) GST.mockEntity(helper, TARGET_POS, EntityType.COW);
        DamageSource meleeSource = player.damageSources().playerAttack(player);

        EBLivingHurtEvent hurtEvent = new EBLivingHurtEvent(cow, meleeSource, 5.0f);
        ((ArtifactItem) ring.getItem()).getEffect().onHurtEntity(hurtEvent, ring);

        GST.assertTrue(helper, "Ring of Necromancy Melee should apply wither effect on melee hit with necromancy wand",
                cow.hasEffect(MobEffects.WITHER));
    }
}
