package com.binaris.wizardry.core.gametest;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.api.content.util.RegistryUtils;
import com.binaris.wizardry.api.content.util.CastItemDataHelper;
import com.binaris.wizardry.content.blockentity.ArcaneWorkbenchBlockEntity;
import com.binaris.wizardry.content.item.*;
import com.binaris.wizardry.content.item.armor.WizardArmorItem;
import com.binaris.wizardry.content.item.armor.WizardArmorType;
import com.binaris.wizardry.content.menu.ArcaneWorkbenchMenu;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class AWTestHandler {
    private static final BlockPos WORKBENCH_POS = new BlockPos(1, 2, 1);
    private static final Vec3 PLAYER_POS = new Vec3(1.5, 2.0, 1.5);

    /** Applies the given spells to the wand */
    public static void applySpellsToWand(GameTestHelper helper, Item wand, Spell... spells) {
        GST.assertFalse(helper, "Invalid parameters", !(wand instanceof WandItem) || spells.length == 0);
        assert wand instanceof WandItem;
        WandItem wandItem = (WandItem) wand;

        ItemStack wandStack = wand.getDefaultInstance();
        ItemStack finalWandStack = wandStack;
        List<Spell> validSpells = Arrays.stream(spells)
                .filter(s -> wandItem.getTier(finalWandStack).getLevel() >= s.getTier().getLevel())
                .toList();

        TestContext ctx = setupTest(helper, wandStack);

        IntStream.range(0, validSpells.size()).forEach(i -> ctx.workbench.setItem(i, RegistryUtils.spellBookItem(validSpells.get(i))));
        ctx.menu.onApplyButtonPressed(ctx.player);

        wandStack = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        List<Spell> wandSpells = CastItemDataHelper.getSpells(wandStack);

        validSpells.forEach(spell -> GST.assertTrue(helper, "Wand %s should contain %s spell after applying.".formatted(wandItem, spell), wandSpells.contains(spell)));
    }

    /**
     * Different to {@link #canUpgradeToNextTier}. <p>
     * Checks if the given wand can be upgraded to the next tier (in case if it's not a master wand) based on a NBT arcane tome
     */
    public static void upgradeWandNextTierNBT(GameTestHelper helper, Item wand) {
        GST.assertFalse(helper, "Invalid parameters", !(wand instanceof WandItem));
        assert wand instanceof WandItem;
        WandItem wandItem = (WandItem) wand;

        ItemStack wandStack = wand.getDefaultInstance();
        if (wandItem.getTier(wandStack) == SpellTiers.MASTER) return;

        SpellTier nextTier = wandItem.getTier(wandStack).next();
        CastItemDataHelper.setProgression(wandStack, nextTier.getProgression());

        TestContext ctx = setupTest(helper, wandStack);
        ctx.workbench.setItem(ArcaneWorkbenchMenu.UPGRADE_SLOT, RegistryUtils.createArcaneTome(nextTier));
        ctx.menu.onApplyButtonPressed(ctx.player);

        wandStack = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        GST.assertEquals(helper, "Wand should upgrade to next tier.", nextTier, ((WandItem) wandStack.getItem()).getTier(wandStack));
        GST.assertEmpty(helper, "Upgrade item should be consumed.", ctx.workbench.getItem(ArcaneWorkbenchMenu.UPGRADE_SLOT));
    }

    /**
     * Different to {@link #upgradeWandNextTierNBT}. <p>
     * Checks if the given wand can be upgraded to the next tier (in case if it's not a master wand) based on an item
     * instance based arcane tome.
     */
    public static void canUpgradeToNextTier(GameTestHelper helper, Item wand) {
        GST.assertFalse(helper, "Invalid parameters", !(wand instanceof WandItem));
        assert wand instanceof WandItem;
        WandItem wandItem = (WandItem) wand;

        ItemStack wandStack = wand.getDefaultInstance();
        if (wandItem.getTier(wandStack) == SpellTiers.MASTER) return;

        SpellTier nextTier = wandItem.getTier(wandStack).next();
        CastItemDataHelper.setProgression(wandStack, nextTier.getProgression());

        TestContext ctx = setupTest(helper, wandStack);
        ctx.workbench.setItem(ArcaneWorkbenchMenu.UPGRADE_SLOT, RegistryUtils.getArcaneTome(nextTier));
        ctx.menu.onApplyButtonPressed(ctx.player);

        wandStack = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        GST.assertEquals(helper, "Wand should upgrade to next tier.", nextTier, ((WandItem) wandStack.getItem()).getTier(wandStack));
        GST.assertEmpty(helper, "Upgrade item should be consumed.", ctx.workbench.getItem(ArcaneWorkbenchMenu.UPGRADE_SLOT));
    }

    /** Puts the given spell into a new spell scroll using a blank scroll */
    public static void putSpellOnBlankScroll(GameTestHelper helper, Spell spell) {
        TestContext ctx = setupTest(helper, EBItems.BLANK_SCROLL.get().getDefaultInstance());

        ctx.workbench.setItem(0, RegistryUtils.spellBookItem(spell));
        ctx.workbench.setItem(ArcaneWorkbenchMenu.CRYSTAL_SLOT, new ItemStack(EBItems.MAGIC_CRYSTAL.get(), 10));
        ctx.menu.onApplyButtonPressed(ctx.player);

        ItemStack scroll = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        Spell resultSpell = RegistryUtils.getSpell(scroll);

        GST.assertFalse(helper, "Scroll should not be blank or empty: " + scroll, scroll.isEmpty() || scroll.getItem() instanceof BlankScrollItem);
        GST.assertEquals(helper, "Scroll should contain " + spell + " spell.", spell, resultSpell);
        GST.assertTrue(helper, "Crystals should only be partially consumed.", ctx.workbench.getItem(ArcaneWorkbenchMenu.CRYSTAL_SLOT).getCount() < 10);
    }

    /** Try to put a spell into a not blank scroll, should be false */
    public static void putSpellOnScrollFilled(GameTestHelper helper) {
        ItemStack scroll = RegistryUtils.setSpell(EBItems.SCROLL.get().getDefaultInstance(), Spells.FIREBALL);
        TestContext ctx = setupTest(helper, scroll);

        ctx.workbench.setItem(0, RegistryUtils.spellBookItem(Spells.ICE_SHARD));
        ctx.workbench.setItem(ArcaneWorkbenchMenu.CRYSTAL_SLOT, new ItemStack(EBItems.MAGIC_CRYSTAL.get(), 10));
        ctx.menu.onApplyButtonPressed(ctx.player);

        scroll = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);

        GST.assertFalse(helper, "Scroll should not be blank or empty: " + scroll, scroll.isEmpty() || scroll.getItem() instanceof BlankScrollItem);
        GST.assertFalse(helper, "Crystals shouldn't be consumed.", ctx.workbench.getItem(ArcaneWorkbenchMenu.CRYSTAL_SLOT).isEmpty());
        GST.assertEquals(helper, "Scroll should still contain original spell.", Spells.FIREBALL, RegistryUtils.getSpell(scroll));
        helper.succeed();
    }

    /** Tests upgrading normal wizard armor to a higher tier using an upgrade item. */
    public static void upgradeNormalArmor(GameTestHelper helper, Item armor, Item upgradeItem) {
        GST.assertFalse(helper, "Invalid parameters", !(armor instanceof WizardArmorItem));
        assert armor instanceof WizardArmorItem;
        WizardArmorItem wizardArmorItem = (WizardArmorItem) armor;

        TestContext ctx = setupTest(helper, armor.getDefaultInstance());
        ctx.workbench.setItem(ArcaneWorkbenchMenu.UPGRADE_SLOT, upgradeItem.getDefaultInstance());
        ctx.menu.onApplyButtonPressed(ctx.player);

        WizardArmorItem upgradedArmor = (WizardArmorItem) ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT).getItem();

        GST.assertTrue(helper, "Armor %s changed equipment slot after upgrade %s".formatted(armor, upgradeItem), upgradedArmor.getEquipmentSlot() == wizardArmorItem.getEquipmentSlot());
        GST.assertTrue(helper, "Armor %s should be upgraded after applying upgrade item %s.".formatted(armor, upgradeItem), upgradedArmor.getWizardArmorType() != WizardArmorType.WIZARD);
        GST.assertEmpty(helper, "Upgrade item should be consumed.", ctx.workbench.getItem(ArcaneWorkbenchMenu.UPGRADE_SLOT));
    }

    /** Tests that maxed out wizard armor cannot be upgraded further. */
    public static void cannotUpgradeMaxedArmor(GameTestHelper helper, Item armor, Item upgradeItem) {
        GST.assertFalse(helper, "Invalid parameters", !(armor instanceof WizardArmorItem));
        assert armor instanceof WizardArmorItem;
        WizardArmorItem wizardArmorItem = (WizardArmorItem) armor;

        TestContext ctx = setupTest(helper, armor.getDefaultInstance());
        ctx.workbench.setItem(ArcaneWorkbenchMenu.UPGRADE_SLOT, upgradeItem.getDefaultInstance());
        ctx.menu.onApplyButtonPressed(ctx.player);

        WizardArmorItem upgradedArmor = (WizardArmorItem) ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT).getItem();

        GST.assertTrue(helper, "Armor %s should not be upgraded further after applying upgrade item %s.".formatted(armor, upgradeItem), upgradedArmor.getWizardArmorType() == wizardArmorItem.getWizardArmorType());
        GST.assertNotEmpty(helper, "Upgrade item should not be consumed.", ctx.workbench.getItem(ArcaneWorkbenchMenu.UPGRADE_SLOT));
    }

    /** Tests repairing a damaged wand using 1 crystal. */
    public static void repairWand(GameTestHelper helper, Item wand, Item crystal) {
        GST.assertFalse(helper, "Invalid parameters", !(wand instanceof WandItem) || !(crystal instanceof CrystalItem));

        ItemStack wandStack = wand.getDefaultInstance();
        wandStack.setDamageValue(120);

        TestContext ctx = setupTest(helper, wandStack);
        ctx.workbench.setItem(ArcaneWorkbenchMenu.CRYSTAL_SLOT, crystal.getDefaultInstance());
        ctx.menu.onApplyButtonPressed(ctx.player);

        wandStack = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        GST.assertTrue(helper, "Wand %s should be repaired after applying crystal %s.".formatted(wand, crystal), wandStack.getDamageValue() < 120);
        GST.assertTrue(helper, "Crystal %s should be consumed after repairing wand %s.".formatted(crystal, wand), ctx.workbench.getItem(ArcaneWorkbenchMenu.CRYSTAL_SLOT).isEmpty());
    }

    /** Tests that blank scrolls cannot exceed the stack limit of 16 in the workbench center slot. */
    public static void cannotExceedBlankScrollLimit(GameTestHelper helper) {
        TestContext ctx = setupTest(helper, ItemStack.EMPTY);
        ctx.workbench.setItem(ArcaneWorkbenchMenu.CENTRE_SLOT, new ItemStack(EBItems.BLANK_SCROLL.get(), 16));
        ctx.player.getInventory().add(new ItemStack(EBItems.BLANK_SCROLL.get(), 64));

        int playerSlotIndex = findItemInMenu(ctx.menu, EBItems.BLANK_SCROLL.get());
        GST.assertTrue(helper, "Player should have blank scrolls in inventory", playerSlotIndex != -1);

        ctx.menu.quickMoveStack(ctx.player, playerSlotIndex);
        int finalCentreCount = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT).getCount();

        GST.assertTrue(helper, "Centre slot should not exceed 16 blank scrolls", finalCentreCount <= 16);
        helper.succeed();
    }

    /** Tests that spell books cannot exceed the stack limit of 1 per slot in the workbench. */
    public static void cannotExceedSpellBookLimit(GameTestHelper helper, Spell spell) {
        TestContext ctx = setupTest(helper, EBItems.NOVICE_HEALING_WAND.get().getDefaultInstance());
        ctx.workbench.setItem(0, RegistryUtils.spellBookItem(spell));

        ItemStack spellBooks = RegistryUtils.spellBookItem(spell);
        spellBooks.setCount(64);
        ctx.player.getInventory().add(spellBooks);

        int playerSlotIndex = findSpellBookInMenu(ctx.menu, spell);
        GST.assertTrue(helper, "Player should have spell books in inventory", playerSlotIndex != -1);

        ctx.menu.quickMoveStack(ctx.player, playerSlotIndex);

        IntStream.range(0, 8).mapToObj(ctx.workbench::getItem).filter(slotItem -> slotItem.getItem() instanceof SpellBookItem).forEach(slotItem -> GST.assertTrue(helper, "Each spell book slot should not exceed 1 item", slotItem.getCount() <= 1));

        helper.succeed();
    }

    /** Tests that upgrade items cannot exceed the stack limit of 1 in the upgrade slot. */
    public static void cannotExceedUpgradeLimit(GameTestHelper helper, Item upgradeItem) {
        TestContext ctx = setupTest(helper, EBItems.ADVANCED_EARTH_WAND.get().getDefaultInstance());
        ctx.workbench.setItem(ArcaneWorkbenchMenu.UPGRADE_SLOT, new ItemStack(upgradeItem, 1));
        ctx.player.getInventory().add(new ItemStack(upgradeItem, 64));

        int playerSlotIndex = findItemInMenu(ctx.menu, upgradeItem);
        GST.assertTrue(helper, "Player should have upgrade items in inventory", playerSlotIndex != -1);

        ctx.menu.quickMoveStack(ctx.player, playerSlotIndex);
        int finalUpgradeCount = ctx.workbench.getItem(ArcaneWorkbenchMenu.UPGRADE_SLOT).getCount();

        GST.assertTrue(helper, "Upgrade slot should not exceed 1 item", finalUpgradeCount <= 1);
        helper.succeed();
    }

    /**
     * Finds the first slot in the menu player inventory that contains the specified item.
     *
     * @param menu the Arcane Workbench menu to search in
     * @param item the item to find
     * @return the slot index if found, or -1 if not found
     */
    private static int findItemInMenu(ArcaneWorkbenchMenu menu, Item item) {
        return IntStream.range(11, menu.slots.size()).filter(i -> menu.getSlot(i).getItem().getItem() == item).findFirst().orElse(-1);
    }

    /**
     * Finds the first slot in the menu player inventory that contains a spell book with the specified spell.
     *
     * @param menu the Arcane Workbench menu to search in
     * @param spell the spell to find in a spell book
     * @return the slot index if found, or -1 if not found
     */
    private static int findSpellBookInMenu(ArcaneWorkbenchMenu menu, Spell spell) {
        for (int i = 11; i < menu.slots.size(); i++) {
            ItemStack slotItem = menu.getSlot(i).getItem();
            if (slotItem.getItem() instanceof SpellBookItem && RegistryUtils.getSpell(slotItem) == spell) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Initializes the workbench at the predefined position, creates a test context
     * with the player and menu, and places the given item in the workbench center slot.
     *
     * @param helper the GameTestHelper instance for the test
     * @param centerItem the ItemStack to place in the centre slot of the workbench
     * @return a TestContext containing the workbench, player, and menu for further test operations
     */
    static TestContext setupTest(GameTestHelper helper, ItemStack centerItem) {
        ArcaneWorkbenchBlockEntity workbench = (ArcaneWorkbenchBlockEntity) helper.getBlockEntity(WORKBENCH_POS);
        GST.assertNotNull(helper, "Arcane Workbench BlockEntity is null", workbench);

        Player player = GST.mockPlayer(helper, PLAYER_POS);
        ArcaneWorkbenchMenu menu = null; // this shouldn't be null btw
        if (workbench != null) {
            workbench.setItem(ArcaneWorkbenchMenu.CENTRE_SLOT, centerItem);
            menu = new ArcaneWorkbenchMenu(0, player.getInventory(), workbench);
            player.containerMenu = menu;
        }

        return new TestContext(workbench, player, menu);
    }

    /**
     * Record that holds the context for a single Arcane Workbench test.
     *
     * @param workbench the Arcane Workbench block entity
     * @param player the test player
     * @param menu the Arcane Workbench menu associated with the player and workbench
     */
    record TestContext(ArcaneWorkbenchBlockEntity workbench, Player player, ArcaneWorkbenchMenu menu) {
    }

    private AWTestHandler() {
    }
}
