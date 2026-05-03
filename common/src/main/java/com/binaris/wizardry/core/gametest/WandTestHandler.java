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

public final class WandTestHandler {
    /** List of spells used in full spell list tests. */
    private static final List<Spell> FULL_SPELL_LIST = List.of(Spells.FIREBALL, Spells.ICE_SHARD, Spells.HEAL, Spells.LIFE_DRAIN, Spells.EVADE);

    /**
     * Tests basic spell navigation through a wand's spell list. Basically, checks if the previous/next spell selection
     * on wand is working.
     */
    public static void wandBasicMovement(GameTestHelper helper) {
        ItemStack wand = setupWandWithSpells(helper, FULL_SPELL_LIST);
        ICastItem wandItem = (ICastItem) wand.getItem();

        wandItem.selectPreviousSpell(wand);
        GST.assertSpellEquals(helper, wand, Spells.EVADE, "selecting next spell from 'Fireball'");

        wandItem.selectPreviousSpell(wand);
        GST.assertSpellEquals(helper, wand, Spells.LIFE_DRAIN, "selecting next spell from 'Evade'");

        wandItem.selectNextSpell(wand);
        GST.assertSpellEquals(helper, wand, Spells.EVADE, "selecting previous spell from 'Life Drain'");

        wandItem.selectNextSpell(wand);
        GST.assertSpellEquals(helper, wand, Spells.FIREBALL, "selecting previous spell from 'Evade'");
    }

    /**
     * Tests spell navigation when a wand has only one spell loaded, this should be cycling starting on that spell and
     * then return to it
     */
    public static void wandPartiallyEmpty(GameTestHelper helper) {
        ItemStack wand = setupWandWithSpells(helper, List.of(Spells.FIREBALL));
        ICastItem wandItem = (ICastItem) wand.getItem();

        wandItem.selectNextSpell(wand);
        wandItem.selectNextSpell(wand);
        wandItem.selectPreviousSpell(wand);
        wandItem.selectPreviousSpell(wand);

        GST.assertSpellEquals(helper, wand, Spells.FIREBALL, "cycling through empty slots");
    }

    /**
     * Verifies that selecting next/previous spells wraps around correctly when cyclin through all spell slots multiple
     * times.
     */
    public static void wandCircularSelection(GameTestHelper helper) {
        ItemStack wand = setupWandWithSpells(helper, List.of(Spells.FIREBALL));
        ICastItem wandItem = (ICastItem) wand.getItem();

        IntStream.range(0, 5).mapToObj(i -> wand).forEach(wandItem::selectNextSpell);
        GST.assertSpellEquals(helper, wand, Spells.FIREBALL, "cycling next through all slots");

        IntStream.range(0, 5).mapToObj(i -> wand).forEach(wandItem::selectPreviousSpell);
        GST.assertSpellEquals(helper, wand, Spells.FIREBALL, "cycling previous through all slots");
    }

    /**
     * Verifies that selecting a specific spell slot by literal index correctly
     * changes the active spell to the spell at that index.
     */
    public static void wandLiteralIndex(GameTestHelper helper) {
        ItemStack wand = setupWandWithSpells(helper, FULL_SPELL_LIST);
        ICastItem wandItem = (ICastItem) wand.getItem();

        wandItem.selectSpell(wand, 2);
        GST.assertSpellEquals(helper, wand, Spells.HEAL, "selecting index 2");

        wandItem.selectSpell(wand, 4);
        GST.assertSpellEquals(helper, wand, Spells.EVADE, "selecting index 4");

        wandItem.selectSpell(wand, 0);
        GST.assertSpellEquals(helper, wand, Spells.FIREBALL, "selecting index 0");
    }

    /** Tests direct spell selection by index on a wand with only one spell loaded. */
    public static void wandLiteralIndexPartiallyEmpty(GameTestHelper helper) {
        ItemStack wand = setupWandWithSpells(helper, List.of(Spells.ICE_SHARD));
        ICastItem wandItem = (ICastItem) wand.getItem();

        wandItem.selectSpell(wand, 3);
        GST.assertIndexEquals(helper, wand, 3, "selecting index 3 on a partially empty wand");

        wandItem.selectSpell(wand, 2);
        GST.assertIndexEquals(helper, wand, 2, "selecting index 2 on a partially empty wand");
    }

    /** Tests the siphon upgrade's behavior when a player kills a mob. */
    public static void siphonUpgradePlayerKillMob(GameTestHelper helper) {
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
    private static ItemStack setupWandWithSpells(GameTestHelper helper, List<Spell> spells) {
        ItemStack wand = EBItems.MASTER_WAND.get().getDefaultInstance();
        AWTestHandler.TestContext ctx = AWTestHandler.setupTest(helper, wand);

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