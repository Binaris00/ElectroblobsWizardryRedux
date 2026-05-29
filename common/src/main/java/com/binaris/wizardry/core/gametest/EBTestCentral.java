package com.binaris.wizardry.core.gametest;

import com.binaris.wizardry.api.content.DeferredObject;
import com.binaris.wizardry.content.item.armor.WizardArmorItem;
import com.binaris.wizardry.content.item.armor.WizardArmorType;
import com.binaris.wizardry.content.spell.abstr.ConjureItemSpell;
import com.binaris.wizardry.setup.datagen.EBDataGenProcessor;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

/**
 * Central class for all game tests. Exposes static methods for each loader to use in their own test system. In this we made
 * most tests to have parameters so that they can be configured depending on the needs (e.g. different items, different spells, etc.)
 */
public final class EBTestCentral {
    private static final List<Item> UPGRADES = List.of(EBItems.CRYSTAL_SILVER_PLATING.get(), EBItems.ETHEREAL_CRYSTAL_WEAVE.get(), EBItems.RESPLENDENT_THREAD.get());

    private static final List<Item> ALL_UPGRADES = List.of(
            EBItems.ARCANE_TOME.get(),
            EBItems.APPRENTICE_ARCANE_TOME.get(),
            EBItems.ADVANCED_ARCANE_TOME.get(),
            EBItems.MASTER_ARCANE_TOME.get(),
            EBItems.CRYSTAL_SILVER_PLATING.get(),
            EBItems.ETHEREAL_CRYSTAL_WEAVE.get(),
            EBItems.RESPLENDENT_THREAD.get()
    );

    public static class ArcaneWorkbench {
        /**
         * Applies the given spells to the wand using a dummy arcane workbench. It could fail if the spells aren't inside the
         * wand after being applied.
         */
        public static void applySpellsToWand(GameTestHelper helper) {
            EBDataGenProcessor.wandItems().values().forEach(wand ->
                    ArcaneWorkbenchTestHandler.applySpellsToWand(helper, wand.get(), Spells.COBWEBS, Spells.FIREBALL));
            helper.succeed();
        }

        /**
         * Different to {@link #canUpgradeToNextTier}. <p>
         * Checks if the given wand can be upgraded to the next tier (in case if it's not a master wand) based on a NBT arcane tome
         * (doesn't work on forge)
         */
        public static void upgradeWandNextTierNBT(GameTestHelper helper) {
            EBDataGenProcessor.wandItems().values().forEach(wand -> ArcaneWorkbenchTestHandler.upgradeWandNextTierNBT(helper, wand.get()));
            helper.succeed();
        }

        /**
         * Different to {@link #upgradeWandNextTierNBT}. <p>
         * Checks if the given wand can be upgraded to the next tier (in case if it's not a master wand) based on an item
         * instance based arcane tome.
         * (doesn't work on forge)
         */
        public static void canUpgradeToNextTier(GameTestHelper helper) {
            EBDataGenProcessor.wandItems().values().forEach(wand -> ArcaneWorkbenchTestHandler.canUpgradeToNextTier(helper, wand.get()));
            helper.succeed();
        }

        /** Puts the given spell into a new spell scroll using a blank scroll */
        public static void putSpellOnBlankScroll(GameTestHelper helper) {
            ArcaneWorkbenchTestHandler.putSpellOnBlankScroll(helper, Spells.ARCANE_LOCK);
            helper.succeed();
        }

        /** Try to put a spell into a not blank scroll, should be false */
        public static void putSpellOnScrollFilled(GameTestHelper helper) {
            ArcaneWorkbenchTestHandler.putSpellOnScrollFilled(helper);
        }

        /** Tests upgrading normal wizard armor to a higher tier using an upgrade item. */
        public static void upgradeNormalArmor(GameTestHelper helper) {
            EBItems.getArmors().stream()
                    .map(DeferredObject::get)
                    .filter(item -> ((WizardArmorItem) item).getWizardArmorType() == WizardArmorType.WIZARD)
                    .forEach(armor -> UPGRADES.forEach(upgrade ->
                            ArcaneWorkbenchTestHandler.upgradeNormalArmor(helper, armor, upgrade)));
            helper.succeed();
        }

        /** Tests that maxed out wizard armor cannot be upgraded further. */
        public static void cannotUpgradeMaxedArmor(GameTestHelper helper) {
            EBItems.getArmors().stream()
                    .map(DeferredObject::get)
                    .filter(item -> ((WizardArmorItem) item).getWizardArmorType() != WizardArmorType.WIZARD)
                    .forEach(armor -> UPGRADES.forEach(upgrade ->
                            ArcaneWorkbenchTestHandler.cannotUpgradeMaxedArmor(helper, armor, upgrade)));
            helper.succeed();
        }

        /** Tests repairing a damaged wand using 1 crystal. */
        public static void repairWand(GameTestHelper helper) {
            EBDataGenProcessor.wandItems().values().forEach(wand ->
                    ArcaneWorkbenchTestHandler.repairWand(helper, wand.get(), EBItems.MAGIC_CRYSTAL.get()));
            helper.succeed();
        }

        /** Tests that blank scrolls cannot exceed the stack limit of 16 in the workbench center slot. */
        public static void cannotExceedBlankScrollLimit(GameTestHelper helper) {
            ArcaneWorkbenchTestHandler.cannotExceedBlankScrollLimit(helper);
        }

        /** Tests that spell books cannot exceed the stack limit of 1 per slot in the workbench. */
        public static void cannotExceedSpellBookLimit(GameTestHelper helper) {
            ArcaneWorkbenchTestHandler.cannotExceedSpellBookLimit(helper, Spells.FIREBALL);
            helper.succeed();
        }

        /** Tests that upgrade items cannot exceed the stack limit of 1 in the upgrade slot. */
        public static void cannotExceedUpgradeLimit(GameTestHelper helper) {
            ALL_UPGRADES.forEach(upgrade ->
                    ArcaneWorkbenchTestHandler.cannotExceedUpgradeLimit(helper, upgrade));
            helper.succeed();
        }
    }

    public static class ConjureSpells {
        /**
         * Test if the Conjure Item Spells are working, spawning the item and placing it in the player's inventory, taking the
         * spell Flamecatcher as an example.
         */
        public static void spawnConjureItem(GameTestHelper helper) {
            ConjureSpellsTestHandler.spawnConjureItem(helper, (ConjureItemSpell) Spells.FLAMECATCHER, EBItems.FLAMECATCHER.get());
        }

        /** Test if the Conjure Item Spells are working, despawing the item after the time limit */
        public static void conjureItemDespawn(GameTestHelper helper) {
            ConjureSpellsTestHandler.conjureItemDespawn(helper, EBItems.FLAMECATCHER.get());
        }

        public static void buildTable(GameTestHelper helper) {
            ConjureSpellsTestHandler.buildTable(helper);
        }
    }

    public static class TempEnchants {
        /**
         * Puts an imbuement enchant to an item and check if the enchant is present
         */
        public static void simpleEnchantTest(GameTestHelper helper) {
            TempEnchantsTestHandler.simpleEnchantTest(helper, Items.DIAMOND_SWORD, Enchantments.FIRE_ASPECT);
        }

        /**
         * Puts an imbuement enchant alongside a normal enchant, checks if both are present
         */
        public static void simpleEnchantCompanyTest(GameTestHelper helper) {
            TempEnchantsTestHandler.simpleEnchantCompanyTest(helper, Items.DIAMOND_SWORD, Enchantments.FIRE_ASPECT, Enchantments.UNBREAKING);
        }

        /**
         * Puts an imbuement enchant to an item and checks after the end time if the enchantment is removed
         */
        public static void imbuementTickTest(GameTestHelper helper) {
            TempEnchantsTestHandler.imbuementTickTest(helper, Items.DIAMOND_SWORD, Enchantments.FIRE_ASPECT);
        }

        /**
         * Puts an imbuement enchant alongside a normal enchant, checks if the normal enchant is still present and the imbuement
         * is removed after the end time
         */
        public static void imbuementTickCompanyTest(GameTestHelper helper) {
            TempEnchantsTestHandler.imbuementTickCompanyTest(helper, Items.DIAMOND_SWORD, Enchantments.FIRE_ASPECT, Enchantments.UNBREAKING);
        }
    }

    public static class WizardArmor {
        /**
         * Tests the internal mixins that prevent the wizard armor to break after reaching 0 durability, it fails in case the armor breaks.
         */
        public static void armorNeverBreaks(GameTestHelper helper) {
            WizardArmorTestHandler.armorNeverBreaks(helper, (WizardArmorItem) EBItems.WIZARD_BOOTS_FIRE.get());
        }

        /**
         * Checks if the armor attributes (like protection) are present when the armor has mana, fails if the attributes are not present.
         */
        public static void armorAttributesWithMana(GameTestHelper helper) {
            WizardArmorTestHandler.armorAttributesWithMana(helper, (WizardArmorItem) EBItems.WIZARD_BOOTS_FIRE.get());
        }

        /**
         * Checks if the armor attributes (like protection) are not present when the armor has no mana, fails if the attributes are present
         * even when the armor has no mana.
         */
        public static void armorNoAttributesWithoutMana(GameTestHelper helper) {
            WizardArmorTestHandler.armorNoAttributesWithoutMana(helper, (WizardArmorItem) EBItems.WIZARD_HAT.get());
        }
    }

    public static class Wand {
        /**
         * Tests basic spell navigation through a wand's spell list. Basically, checks if the previous/next spell selection
         * on wand is working.
         */
        public static void wandBasicMovement(GameTestHelper helper) {
            WandTestHandler.wandBasicMovement(helper, List.of(Spells.FIREBALL, Spells.ICE_SHARD, Spells.HEAL, Spells.LIFE_DRAIN, Spells.EVADE));
        }

        /**
         * Tests spell navigation when a wand has only one spell loaded, this should be cycling starting on that spell and
         * then return to it
         */
        public static void wandPartiallyEmpty(GameTestHelper helper) {
            WandTestHandler.wandPartiallyEmpty(helper, Spells.FIREBALL);
        }

        /**
         * Verifies that selecting next/previous spells wraps around correctly when cyclin through all spell slots multiple
         * times.
         */
        public static void wandCircularSelection(GameTestHelper helper) {
            WandTestHandler.wandCircularSelection(helper, Spells.FIREBALL);
        }

        /**
         * Verifies that selecting a specific spell slot by literal index correctly
         * changes the active spell to the spell at that index.
         */
        public static void wandLiteralIndex(GameTestHelper helper) {
            WandTestHandler.wandLiteralIndex(helper, List.of(Spells.FIREBALL, Spells.ICE_SHARD, Spells.HEAL, Spells.LIFE_DRAIN, Spells.EVADE));
        }

        /** Tests direct spell selection by index on a wand with only one spell loaded. */
        public static void wandLiteralIndexPartiallyEmpty(GameTestHelper helper) {
            WandTestHandler.wandLiteralIndexPartiallyEmpty(helper, Spells.ICE_SHARD);
        }

        /** Tests the siphon upgrade's behavior when a player kills a mob. */
        public static void siphonUpgradePlayerKillMob(GameTestHelper helper) {
            WandTestHandler.siphonUpgradePlayerKillMob(helper);
        }
    }

    private EBTestCentral() {
    }
}
