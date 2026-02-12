package com.binaris.wizardry.core.gametest;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.api.content.util.SpellUtil;
import com.binaris.wizardry.api.content.util.WandHelper;
import com.binaris.wizardry.content.blockentity.ArcaneWorkbenchBlockEntity;
import com.binaris.wizardry.content.item.*;
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

@SuppressWarnings("unused")
public final class ArcaneWorkbenchTest {
    private static final BlockPos WORKBENCH_POS = new BlockPos(1, 2, 1);
    private static final Vec3 PLAYER_POS = new Vec3(1.5, 2.0, 1.5);

    public static void applySpellsToWand(GameTestHelper helper, Item wand, Spell... spells) {
        if (!(wand instanceof WandItem wandItem) || spells.length == 0) {
            helper.fail("Invalid parameters for applySpellsToWand test.");
            return;
        }

        ItemStack wandStack = wand.getDefaultInstance();
        ItemStack finalWandStack = wandStack;
        List<Spell> validSpells = Arrays.stream(spells)
                .filter(s -> wandItem.getTier(finalWandStack).getLevel() >= s.getTier().getLevel())
                .toList();

        TestContext ctx = setupTest(helper, wandStack);

        for (int i = 0; i < validSpells.size(); i++) {
            ctx.workbench.setItem(i, SpellUtil.spellBookItem(validSpells.get(i)));
        }
        ctx.menu.onApplyButtonPressed(ctx.player);

        wandStack = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        List<Spell> wandSpells = WandHelper.getSpells(wandStack);

        validSpells.forEach(spell -> GST.assertTrue(helper,
                "Wand %s should contain %s spell after applying.".formatted(wandItem, spell),
                wandSpells.contains(spell)));
    }

    /**
     * Different to {@link #canUpgradeToNextTier}, this test checks the possibility of upgrading a wand to the next tier
     * using an Arcane Tome that has the tier stored in its NBT data instead of its item instance. This is to ensure
     * that both methods of storing the tier in the Arcane Tome work correctly and can be used interchangeably in the
     * upgrade process.
     */
    public static void upgradeWandNextTierNBT(GameTestHelper helper, Item wand) {
        if (!(wand instanceof WandItem wandItem)) {
            helper.fail("Invalid parameters for canUpgradeToNextTier test.");
            return;
        }

        ItemStack wandStack = wand.getDefaultInstance();
        if (wandItem.getTier(wandStack) == SpellTiers.MASTER) return;

        SpellTier nextTier = wandItem.getTier(wandStack).next();
        WandHelper.setProgression(wandStack, nextTier.getProgression());

        TestContext ctx = setupTest(helper, wandStack);
        ctx.workbench.setItem(ArcaneWorkbenchMenu.UPGRADE_SLOT, SpellUtil.createArcaneTome(nextTier));
        ctx.menu.onApplyButtonPressed(ctx.player);

        wandStack = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        GST.assertEquals(helper, "Wand should upgrade to next tier.", nextTier,
                ((WandItem) wandStack.getItem()).getTier(wandStack));
        GST.assertEmpty(helper, "Upgrade item should be consumed.",
                ctx.workbench.getItem(ArcaneWorkbenchMenu.UPGRADE_SLOT));
    }

    /**
     * Similar to {@link #upgradeWandNextTierNBT}, but this test uses an Arcane Tome with the tier determined by its
     * item instance rather than its NBT data. This ensures that the upgrade process correctly recognizes the tier from
     * the item instance and can successfully upgrade the wand to the next tier using this method as well.
     */
    public static void canUpgradeToNextTier(GameTestHelper helper, Item wand) {
        if (!(wand instanceof WandItem wandItem)) {
            helper.fail("Invalid parameters for canUpgradeToNextTier test.");
            return;
        }

        ItemStack wandStack = wand.getDefaultInstance();
        if (wandItem.getTier(wandStack) == SpellTiers.MASTER) return;

        SpellTier nextTier = wandItem.getTier(wandStack).next();
        WandHelper.setProgression(wandStack, nextTier.getProgression());

        TestContext ctx = setupTest(helper, wandStack);
        ctx.workbench.setItem(ArcaneWorkbenchMenu.UPGRADE_SLOT, SpellUtil.getArcaneTome(nextTier));
        ctx.menu.onApplyButtonPressed(ctx.player);

        wandStack = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        GST.assertEquals(helper, "Wand should upgrade to next tier.", nextTier,
                ((WandItem) wandStack.getItem()).getTier(wandStack));
        GST.assertEmpty(helper, "Upgrade item should be consumed.",
                ctx.workbench.getItem(ArcaneWorkbenchMenu.UPGRADE_SLOT));
    }

    public static void putSpellOnBlankScroll(GameTestHelper helper, Spell spell) {
        TestContext ctx = setupTest(helper, EBItems.BLANK_SCROLL.get().getDefaultInstance());

        ctx.workbench.setItem(0, SpellUtil.spellBookItem(spell));
        ctx.workbench.setItem(ArcaneWorkbenchMenu.CRYSTAL_SLOT,
                new ItemStack(EBItems.MAGIC_CRYSTAL.get(), 10));
        ctx.menu.onApplyButtonPressed(ctx.player);

        ItemStack scroll = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        Spell resultSpell = SpellUtil.getSpell(scroll);

        GST.assertFalse(helper, "Scroll should not be blank or empty: " + scroll,
                scroll.isEmpty() || scroll.getItem() instanceof BlankScrollItem);
        GST.assertEquals(helper, "Scroll should contain " + spell + " spell.", spell, resultSpell);
        GST.assertTrue(helper, "Crystals should only be partially consumed.",
                ctx.workbench.getItem(ArcaneWorkbenchMenu.CRYSTAL_SLOT).getCount() < 10);
    }

    public static void putSpellOnScrollFilled(GameTestHelper helper) {
        ItemStack scroll = SpellUtil.setSpell(EBItems.SCROLL.get().getDefaultInstance(), Spells.FIREBALL);
        TestContext ctx = setupTest(helper, scroll);

        ctx.workbench.setItem(0, SpellUtil.spellBookItem(Spells.ICE_SHARD));
        ctx.workbench.setItem(ArcaneWorkbenchMenu.CRYSTAL_SLOT,
                new ItemStack(EBItems.MAGIC_CRYSTAL.get(), 10));
        ctx.menu.onApplyButtonPressed(ctx.player);

        scroll = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);

        GST.assertFalse(helper, "Scroll should not be blank or empty: " + scroll,
                scroll.isEmpty() || scroll.getItem() instanceof BlankScrollItem);
        GST.assertFalse(helper, "Crystals shouldn't be consumed.",
                ctx.workbench.getItem(ArcaneWorkbenchMenu.CRYSTAL_SLOT).isEmpty());
        GST.assertEquals(helper, "Scroll should still contain original spell.",
                Spells.FIREBALL, SpellUtil.getSpell(scroll));
        helper.succeed();
    }

    public static void upgradeNormalArmor(GameTestHelper helper, Item armor, Item upgradeItem) {
        if (!(armor instanceof WizardArmorItem wizardArmorItem)) {
            helper.fail("Invalid parameters for upgradeNormalArmor test.");
            return;
        }

        TestContext ctx = setupTest(helper, armor.getDefaultInstance());
        ctx.workbench.setItem(ArcaneWorkbenchMenu.UPGRADE_SLOT, upgradeItem.getDefaultInstance());
        ctx.menu.onApplyButtonPressed(ctx.player);

        WizardArmorItem upgradedArmor = (WizardArmorItem) ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT).getItem();

        GST.assertTrue(helper, "Armor %s changed equipment slot after upgrade %s".formatted(armor, upgradeItem),
                upgradedArmor.getEquipmentSlot() == wizardArmorItem.getEquipmentSlot());
        GST.assertTrue(helper, "Armor %s should be upgraded after applying upgrade item %s.".formatted(armor, upgradeItem),
                upgradedArmor.getWizardArmorType() != WizardArmorType.WIZARD);
        GST.assertEmpty(helper, "Upgrade item should be consumed.",
                ctx.workbench.getItem(ArcaneWorkbenchMenu.UPGRADE_SLOT));
    }

    public static void cannotUpgradeMaxedArmor(GameTestHelper helper, Item armor, Item upgradeItem) {
        if (!(armor instanceof WizardArmorItem wizardArmorItem)) {
            helper.fail("Invalid parameters for cannotUpgradeMaxedArmor test.");
            return;
        }

        TestContext ctx = setupTest(helper, armor.getDefaultInstance());
        ctx.workbench.setItem(ArcaneWorkbenchMenu.UPGRADE_SLOT, upgradeItem.getDefaultInstance());
        ctx.menu.onApplyButtonPressed(ctx.player);

        WizardArmorItem upgradedArmor = (WizardArmorItem) ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT).getItem();

        GST.assertTrue(helper, "Armor %s should not be upgraded further after applying upgrade item %s.".formatted(armor, upgradeItem),
                upgradedArmor.getWizardArmorType() == wizardArmorItem.getWizardArmorType());
        GST.assertNotEmpty(helper, "Upgrade item should not be consumed.",
                ctx.workbench.getItem(ArcaneWorkbenchMenu.UPGRADE_SLOT));
    }

    public static void repairWand(GameTestHelper helper, Item wand, Item crystal) {
        if (!(wand instanceof WandItem) || !(crystal instanceof CrystalItem)) {
            helper.fail("Invalid parameters for repairWand test.");
            return;
        }

        ItemStack wandStack = wand.getDefaultInstance();
        wandStack.setDamageValue(120);

        TestContext ctx = setupTest(helper, wandStack);
        ctx.workbench.setItem(ArcaneWorkbenchMenu.CRYSTAL_SLOT, crystal.getDefaultInstance());
        ctx.menu.onApplyButtonPressed(ctx.player);

        wandStack = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        GST.assertTrue(helper, "Wand %s should be repaired after applying crystal %s."
                .formatted(wand, crystal), wandStack.getDamageValue() < 120);
        GST.assertTrue(helper, "Crystal %s should be consumed after repairing wand %s."
                .formatted(crystal, wand), ctx.workbench.getItem(ArcaneWorkbenchMenu.CRYSTAL_SLOT).isEmpty());
    }

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

    public static void cannotExceedSpellBookLimit(GameTestHelper helper, Spell spell) {
        TestContext ctx = setupTest(helper, EBItems.NOVICE_HEALING_WAND.get().getDefaultInstance());
        ctx.workbench.setItem(0, SpellUtil.spellBookItem(spell));

        ItemStack spellBooks = SpellUtil.spellBookItem(spell);
        spellBooks.setCount(64);
        ctx.player.getInventory().add(spellBooks);

        int playerSlotIndex = findSpellBookInMenu(ctx.menu, spell);
        GST.assertTrue(helper, "Player should have spell books in inventory", playerSlotIndex != -1);

        ctx.menu.quickMoveStack(ctx.player, playerSlotIndex);

        for (int i = 0; i < 8; i++) {
            ItemStack slotItem = ctx.workbench.getItem(i);
            if (slotItem.getItem() instanceof SpellBookItem) {
                GST.assertTrue(helper, "Each spell book slot should not exceed 1 item", slotItem.getCount() <= 1);
            }
        }

        helper.succeed();
    }

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

    private static int findItemInMenu(ArcaneWorkbenchMenu menu, Item item) {
        for (int i = 11; i < menu.slots.size(); i++) {
            if (menu.getSlot(i).getItem().getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    private static int findSpellBookInMenu(ArcaneWorkbenchMenu menu, Spell spell) {
        for (int i = 11; i < menu.slots.size(); i++) {
            ItemStack slotItem = menu.getSlot(i).getItem();
            if (slotItem.getItem() instanceof SpellBookItem && SpellUtil.getSpell(slotItem) == spell) {
                return i;
            }
        }
        return -1;
    }

    static TestContext setupTest(GameTestHelper helper, ItemStack centerItem) {
        ArcaneWorkbenchBlockEntity workbench = (ArcaneWorkbenchBlockEntity) helper.getBlockEntity(WORKBENCH_POS);
        GST.assertNotNull(helper, "Arcane Workbench BlockEntity is null", workbench);

        Player player = GST.mockServerPlayer(helper, PLAYER_POS);
        workbench.setItem(ArcaneWorkbenchMenu.CENTRE_SLOT, centerItem);
        ArcaneWorkbenchMenu menu = new ArcaneWorkbenchMenu(0, player.getInventory(), workbench);
        player.containerMenu = menu;

        return new TestContext(workbench, player, menu);
    }

    record TestContext(ArcaneWorkbenchBlockEntity workbench, Player player, ArcaneWorkbenchMenu menu) {
    }

    private ArcaneWorkbenchTest() {
    }
}
