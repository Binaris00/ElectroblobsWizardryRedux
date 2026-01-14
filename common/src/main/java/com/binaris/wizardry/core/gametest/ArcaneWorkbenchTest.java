package com.binaris.wizardry.core.gametest;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.api.content.util.SpellUtil;
import com.binaris.wizardry.api.content.util.WandHelper;
import com.binaris.wizardry.content.blockentity.ArcaneWorkbenchBlockEntity;
import com.binaris.wizardry.content.item.BlankScrollItem;
import com.binaris.wizardry.content.item.WandItem;
import com.binaris.wizardry.content.menu.ArcaneWorkbenchMenu;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;

@SuppressWarnings("unused")
public class ArcaneWorkbenchTest {
    private static final BlockPos WORKBENCH_POS = new BlockPos(1, 2, 1);
    private static final Vec3 PLAYER_POS = new Vec3(1.5, 2.0, 1.5);

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void applySpellsToWand(GameTestHelper helper) {
        TestContext ctx = setupTest(helper, EBItems.MASTER_LIGHTNING_WAND.get().getDefaultInstance());

        ctx.workbench.setItem(0, SpellUtil.spellBookItem(Spells.COBWEBS));
        ctx.workbench.setItem(1, SpellUtil.spellBookItem(Spells.FIREBALL));
        ctx.menu.onApplyButtonPressed(ctx.player);

        ItemStack wand = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        List<Spell> spells = WandHelper.getSpells(wand);

        GST.assertTrue(helper, "Wand should contain COBWEBS spell. Nbt: " + wand.getOrCreateTag(), spells.contains(Spells.COBWEBS));
        GST.assertTrue(helper, "Wand should contain FIREBALL spell. Nbt: " + wand.getOrCreateTag(), spells.contains(Spells.FIREBALL));
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void canUpgradeToNextTier(GameTestHelper helper) {
        ItemStack wand = EBItems.ADVANCED_EARTH_WAND.get().getDefaultInstance();
        TestContext ctx = setupTest(helper, wand);

        SpellTier nextTier = ((WandItem) wand.getItem()).getTier(wand).next();
        WandHelper.setProgression(wand, nextTier.getProgression());
        ctx.workbench.setItem(ArcaneWorkbenchMenu.UPGRADE_SLOT, SpellUtil.arcaneTomeItem(nextTier));
        ctx.menu.onApplyButtonPressed(ctx.player);

        wand = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        GST.assertEquals(helper, "Wand should upgrade to next tier.", nextTier, ((WandItem) wand.getItem()).getTier(wand));

        ItemStack upgradeItem = ctx.workbench.getItem(ArcaneWorkbenchMenu.UPGRADE_SLOT);
        GST.assertEmpty(helper, "Upgrade item should be consumed.", upgradeItem);

        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void cannotUpgradeWithoutTome(GameTestHelper helper) {
        ItemStack wand = EBItems.NOVICE_ICE_WAND.get().getDefaultInstance();
        TestContext ctx = setupTest(helper, wand);
        SpellTier nextTier = ((WandItem) wand.getItem()).getTier(wand).next();

        // Test upgrade without tome
        ctx.workbench.setItem(ArcaneWorkbenchMenu.UPGRADE_SLOT, ItemStack.EMPTY);
        ctx.menu.onApplyButtonPressed(ctx.player);

        wand = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        GST.assertEquals(helper, "Wand should not upgrade without tome.",
                SpellTiers.NOVICE, ((WandItem) wand.getItem()).getTier(wand));

        // Test upgrade without enough progression
        WandHelper.setProgression(wand, WandHelper.getProgression(wand) + 1);
        ctx.workbench.setItem(ArcaneWorkbenchMenu.UPGRADE_SLOT, SpellUtil.arcaneTomeItem(nextTier));
        ctx.menu.onApplyButtonPressed(ctx.player);

        wand = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        GST.assertEquals(helper, "Wand should not upgrade without enough progression.",
                SpellTiers.NOVICE, ((WandItem) wand.getItem()).getTier(wand));

        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void whenUpdatingMasterWand(GameTestHelper helper) {
        ItemStack wand = EBItems.MASTER_FIRE_WAND.get().getDefaultInstance();
        TestContext ctx = setupTest(helper, wand);

        // Test with apprentice tome
        ctx.workbench.setItem(ArcaneWorkbenchMenu.UPGRADE_SLOT, SpellUtil.arcaneTomeItem(SpellTiers.APPRENTICE));
        ctx.menu.onApplyButtonPressed(ctx.player);
        assertMasterWandUnchanged(helper, ctx, "with apprentice tome");

        // Test with master tome
        ctx.workbench.setItem(ArcaneWorkbenchMenu.UPGRADE_SLOT, SpellUtil.arcaneTomeItem(SpellTiers.MASTER));
        ctx.menu.onApplyButtonPressed(ctx.player);
        assertMasterWandUnchanged(helper, ctx, "with master tome");

        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void putSpellOnBlankScroll(GameTestHelper helper) {
        ItemStack scroll = EBItems.BLANK_SCROLL.get().getDefaultInstance();
        TestContext ctx = setupTest(helper, scroll);

        ctx.workbench.setItem(0, SpellUtil.spellBookItem(Spells.ARCANE_LOCK));
        ctx.workbench.setItem(ArcaneWorkbenchMenu.CRYSTAL_SLOT, new ItemStack(EBItems.MAGIC_CRYSTAL.get(), 10));

        ctx.menu.onApplyButtonPressed(ctx.player);

        scroll = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        Spell spell = SpellUtil.getSpell(scroll);
        GST.assertFalse(helper, "Scroll should not be blank or empty: " + scroll, scroll.isEmpty() || scroll.getItem() instanceof BlankScrollItem);
        GST.assertEquals(helper, "Scroll should contain ARCANE_LOCK spell.", Spells.ARCANE_LOCK, spell);
        GST.assertTrue(helper, "Crystals should only be partially consumed.", ctx.workbench.getItem(ArcaneWorkbenchMenu.CRYSTAL_SLOT).getCount() < 10);
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void putSpellOnScroll(GameTestHelper helper) {
        ItemStack scroll = SpellUtil.setSpell(EBItems.SCROLL.get().getDefaultInstance(), Spells.FIREBALL);
        TestContext ctx = setupTest(helper, scroll);

        ctx.workbench.setItem(0, SpellUtil.spellBookItem(Spells.ICE_SHARD));
        ctx.workbench.setItem(ArcaneWorkbenchMenu.CRYSTAL_SLOT, new ItemStack(EBItems.MAGIC_CRYSTAL.get(), 10));

        ctx.menu.onApplyButtonPressed(ctx.player);

        scroll = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        Spell spell = SpellUtil.getSpell(scroll);
        GST.assertFalse(helper, "Scroll should not be blank or empty: " + scroll, scroll.isEmpty() || scroll.getItem() instanceof BlankScrollItem);
        GST.assertEquals(helper, "Scroll should not have changed spell.", Spells.FIREBALL, spell);
        GST.assertTrue(helper, "Crystals should not be consumed.", ctx.workbench.getItem(ArcaneWorkbenchMenu.CRYSTAL_SLOT).getCount() == 10);
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void upgradeNormalArmor(GameTestHelper helper) {
        ItemStack armor = new ItemStack(EBItems.WIZARD_ROBE.get());
        TestContext ctx = setupTest(helper, armor);

        ctx.workbench.setItem(ArcaneWorkbenchMenu.UPGRADE_SLOT, EBItems.CRYSTAL_SILVER_PLATING.get().getDefaultInstance());
        ctx.menu.onApplyButtonPressed(ctx.player);

        armor = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        GST.assertTrue(helper, "Armor should be upgraded (battlemage).", armor.getItem() == EBItems.BATTLEMAGE_CHESTPLATE.get());
        GST.assertEmpty(helper, "Upgrade item should be consumed.", ctx.workbench.getItem(ArcaneWorkbenchMenu.UPGRADE_SLOT));

        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void cannotUpgradeMaxedArmor(GameTestHelper helper) {
        ItemStack armor = new ItemStack(EBItems.BATTLEMAGE_BOOTS.get());
        TestContext ctx = setupTest(helper, armor);

        ctx.workbench.setItem(ArcaneWorkbenchMenu.UPGRADE_SLOT, EBItems.ETHEREAL_CRYSTAL_WEAVE.get().getDefaultInstance());
        ctx.menu.onApplyButtonPressed(ctx.player);

        armor = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        GST.assertTrue(helper, "Armor should not be upgraded further (still battlemage).", armor.getItem() == EBItems.BATTLEMAGE_BOOTS.get());
        GST.assertNotEmpty(helper, "Upgrade item should not be consumed.", ctx.workbench.getItem(ArcaneWorkbenchMenu.UPGRADE_SLOT));

        helper.succeed();
    }

    // Helper methods
    private static TestContext setupTest(GameTestHelper helper, ItemStack centerItem) {
        ArcaneWorkbenchBlockEntity workbench = getWorkbench(helper);
        Player player = GST.mockServerPlayer(helper, PLAYER_POS);
        workbench.setItem(ArcaneWorkbenchMenu.CENTRE_SLOT, centerItem);
        ArcaneWorkbenchMenu menu = new ArcaneWorkbenchMenu(0, player.getInventory(), workbench);
        player.containerMenu = menu;
        return new TestContext(workbench, player, menu);
    }

    private static ArcaneWorkbenchBlockEntity getWorkbench(GameTestHelper helper) {
        ArcaneWorkbenchBlockEntity workbench = (ArcaneWorkbenchBlockEntity) helper.getBlockEntity(WORKBENCH_POS);
        GST.assertNotNull(helper, "Arcane Workbench BlockEntity is null", workbench);
        return workbench;
    }

    private static void assertMasterWandUnchanged(GameTestHelper helper, TestContext ctx, String context) {
        ItemStack wand = ctx.workbench.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        ItemStack upgradeItem = ctx.workbench.getItem(ArcaneWorkbenchMenu.UPGRADE_SLOT);

        GST.assertEquals(helper, "Master wand should not change tier " + context + ".", SpellTiers.MASTER, ((WandItem) wand.getItem()).getTier(wand));
        GST.assertNotEmpty(helper, "Upgrade item should not be consumed " + context + ".", upgradeItem);
    }

    private record TestContext(ArcaneWorkbenchBlockEntity workbench, Player player, ArcaneWorkbenchMenu menu) {
    }
}