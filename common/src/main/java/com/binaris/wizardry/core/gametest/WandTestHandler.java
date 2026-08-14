package com.binaris.wizardry.core.gametest;

import com.binaris.wizardry.api.content.event.EBLivingDeathEvent;
import com.binaris.wizardry.api.content.item.IManaItem;
import com.binaris.wizardry.api.content.item.ICastItem;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.RegistryUtils;
import com.binaris.wizardry.api.content.util.CastItemDataHelper;
import com.binaris.wizardry.content.item.WandUpgradeItem;
import com.binaris.wizardry.content.menu.ArcaneWorkbenchMenu;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("all")
public final class WandTestHandler {
    static void wandBasicMovement(GameTestHelper helper, List<Spell> spells) {
        ItemStack wand = setupWandWithSpells(helper, spells);
        ICastItem wandItem = (ICastItem) wand.getItem();

        wandItem.selectPreviousSpell(wand);
        GST.assertcurrentSpellEquals(helper, wand, spells.get(spells.size() - 1), "spell selection is incorrect after basic previous selection");

        wandItem.selectPreviousSpell(wand);
        GST.assertcurrentSpellEquals(helper, wand, spells.get(spells.size() - 2), "spell selection is incorrect after basic previous selection");

        wandItem.selectNextSpell(wand);
        GST.assertcurrentSpellEquals(helper, wand, spells.get(spells.size() - 1), "spell selection is incorrect after basic next selection");

        wandItem.selectNextSpell(wand);
        GST.assertcurrentSpellEquals(helper, wand, spells.get(0), "spell selection is incorrect after basic next selection");
    }

    static void wandPartiallyEmpty(GameTestHelper helper, Spell singleSpell) {
        ItemStack wand = setupWandWithSpells(helper, List.of(singleSpell));
        ICastItem wandItem = (ICastItem) wand.getItem();

        wandItem.selectNextSpell(wand);
        wandItem.selectNextSpell(wand);
        wandItem.selectPreviousSpell(wand);
        wandItem.selectPreviousSpell(wand);

        GST.assertcurrentSpellEquals(helper, wand, singleSpell, "cycling through empty slots on a wand with a single spell didn't work");
    }

    static void wandCircularSelection(GameTestHelper helper, Spell singleSpell) {
        ItemStack wand = setupWandWithSpells(helper, List.of(singleSpell));
        ICastItem wandItem = (ICastItem) wand.getItem();

        IntStream.range(0, 5).mapToObj(i -> wand).forEach(wandItem::selectNextSpell);
        GST.assertcurrentSpellEquals(helper, wand, singleSpell, "cycling next through all slots on a wand with a single spell didn't work");

        IntStream.range(0, 5).mapToObj(i -> wand).forEach(wandItem::selectPreviousSpell);
        GST.assertcurrentSpellEquals(helper, wand, singleSpell, "cycling previous through all slots on a wand with a single spell didn't work");
    }

    static void wandLiteralIndex(GameTestHelper helper, List<Spell> spells) {
        ItemStack wand = setupWandWithSpells(helper, spells);
        ICastItem wandItem = (ICastItem) wand.getItem();

        wandItem.selectSpell(wand, 2);
        GST.assertcurrentSpellEquals(helper, wand, spells.get(2), "selecting index 2 returned a spell that wasn't expected");

        wandItem.selectSpell(wand, 4);
        GST.assertcurrentSpellEquals(helper, wand, spells.get(4), "selecting index 4 returned a spell that wasn't expected");

        wandItem.selectSpell(wand, 0);
        GST.assertcurrentSpellEquals(helper, wand, spells.get(0), "selecting index 0 returned a spell that wasn't expected");
    }

    static void wandLiteralIndexPartiallyEmpty(GameTestHelper helper, Spell singleSpell) {
        ItemStack wand = setupWandWithSpells(helper, List.of(singleSpell));
        ICastItem wandItem = (ICastItem) wand.getItem();

        wandItem.selectSpell(wand, 3);
        GST.assertIndexEquals(helper, wand, 3, "selecting index 3 on a partially empty wand");

        wandItem.selectSpell(wand, 2);
        GST.assertIndexEquals(helper, wand, 2, "selecting index 2 on a partially empty wand");
    }

    static void siphonUpgradePlayerKillMob(GameTestHelper helper) {
        Player player = GST.mockPlayer(helper, new Vec3(1, 2.0, 1));
        ItemStack wand = EBItems.MASTER_WAND.get().getDefaultInstance();
        CastItemDataHelper.applyUpgrade(wand, EBItems.SIPHON_UPGRADE.get());

        player.getInventory().items.set(0, wand);

        IManaItem manaItem = (IManaItem) wand.getItem();
        manaItem.setMana(wand, manaItem.getManaCapacity(wand) - 1);
        DamageSource deathSource = GST.createDamageSource(player, EBDamageSources.MAGIC);

        Cow cow = (Cow) GST.mockEntity(helper, new Vec3(2, 2.0, 2), EntityType.COW);
        EBLivingDeathEvent deathEvent = new EBLivingDeathEvent(cow, deathSource);
        WandUpgradeItem.onPlayerKillMob(deathEvent);

        GST.assertEquals(helper, "Mana should be recharged by siphon upgrade on player kill mob.",
                manaItem.getManaCapacity(wand),
                manaItem.getMana(wand));
    }

    /** Sets up a wand with the specified spells loaded into the Arcane Workbench. */
    static ItemStack setupWandWithSpells(GameTestHelper helper, List<Spell> spells) {
        ItemStack wand = EBItems.MASTER_WAND.get().getDefaultInstance();
        ArcaneWorkbenchTestHandler.TestContext ctx = ArcaneWorkbenchTestHandler.setupTest(helper, wand);

        IntStream.range(0, spells.size()).forEach(i -> ctx.workbench().setItem(i, RegistryUtils.spellBookItem(spells.get(i))));
        ctx.menu().onApplyButtonPressed(ctx.player());

        wand = ctx.workbench().getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        List<Spell> wandSpells = CastItemDataHelper.getSpells(wand);

        for (Spell spell : spells) {
            GST.assertTrue(helper, "Wand %s should contain %s spell after applying.".formatted(wand, spell), wandSpells.contains(spell));
        }

        return wand;
    }

    private WandTestHandler() {
    }
}