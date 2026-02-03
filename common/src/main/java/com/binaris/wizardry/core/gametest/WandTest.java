package com.binaris.wizardry.core.gametest;

import com.binaris.wizardry.api.content.item.ISpellCastingItem;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.SpellUtil;
import com.binaris.wizardry.api.content.util.WandHelper;
import com.binaris.wizardry.content.menu.ArcaneWorkbenchMenu;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class WandTest {

    public static void wandBasicMovement(GameTestHelper helper) {
        ItemStack wand = EBItems.MASTER_WAND.get().getDefaultInstance();
        List<Spell> spells = List.of(
                Spells.FIREBALL,
                Spells.ICE_SHARD,
                Spells.HEAL,
                Spells.LIFE_DRAIN,
                Spells.EVADE
        );

        ArcaneWorkbenchTest.TestContext ctx = ArcaneWorkbenchTest.setupTest(helper, wand);
        for (int i = 0; i < spells.size(); i++) {
            ctx.workbench().setItem(i, SpellUtil.spellBookItem(spells.get(i)));
        }

        ctx.menu().onApplyButtonPressed(ctx.player());

        wand = ctx.workbench().getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        List<Spell> wandSpells = WandHelper.getSpells(wand);

        ItemStack finalWand = wand;
        spells.forEach(spell -> GST.assertTrue(helper,
                "Wand %s should contain %s spell after applying.".formatted(finalWand, spell),
                wandSpells.contains(spell)));

        ((ISpellCastingItem) wand.getItem()).selectPreviousSpell(finalWand);

        Spell selectedSpell = WandHelper.getCurrentSpell(wand);
        GST.assertEquals(helper,
                "Selected spell should be 'Evade' after selecting next spell from 'Fireball'.",
                Spells.EVADE,
                selectedSpell);

        ((ISpellCastingItem) wand.getItem()).selectPreviousSpell(finalWand);
        selectedSpell = WandHelper.getCurrentSpell(wand);
        GST.assertEquals(helper,
                "Selected spell should be 'Life Drain' after selecting next spell from 'Evade'.",
                Spells.LIFE_DRAIN,
                selectedSpell);

        ((ISpellCastingItem) wand.getItem()).selectNextSpell(finalWand);
        selectedSpell = WandHelper.getCurrentSpell(wand);
        GST.assertEquals(helper,
                "Selected spell should be 'Evade' after selecting previous spell from 'Life Drain'.",
                Spells.EVADE,
                selectedSpell);

        ((ISpellCastingItem) wand.getItem()).selectNextSpell(finalWand);
        selectedSpell = WandHelper.getCurrentSpell(wand);
        GST.assertEquals(helper,
                "Selected spell should be 'Fireball' after selecting previous spell from 'Evade'.",
                Spells.FIREBALL,
                selectedSpell);
    }

    public static void wandPartiallyEmpty(GameTestHelper helper) {
        ItemStack wand = EBItems.MASTER_WAND.get().getDefaultInstance();
        Spell spell = Spells.FIREBALL;

        ArcaneWorkbenchTest.TestContext ctx = ArcaneWorkbenchTest.setupTest(helper, wand);
        ctx.workbench().setItem(0, SpellUtil.spellBookItem(spell));
        ctx.menu().onApplyButtonPressed(ctx.player());

        wand = ctx.workbench().getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        List<Spell> wandSpells = WandHelper.getSpells(wand);

        ItemStack finalWand = wand;
        GST.assertTrue(helper,
                "Wand %s should contain %s spell after applying.".formatted(finalWand, spell),
                wandSpells.contains(spell));

        ((ISpellCastingItem) wand.getItem()).selectNextSpell(finalWand);
        ((ISpellCastingItem) wand.getItem()).selectNextSpell(finalWand);
        ((ISpellCastingItem) wand.getItem()).selectPreviousSpell(finalWand);
        ((ISpellCastingItem) wand.getItem()).selectPreviousSpell(finalWand);

        Spell selectedSpell = WandHelper.getCurrentSpell(wand);
        GST.assertEquals(helper,
                "Selected spell should be 'Fireball' after cycling through empty slots.",
                spell,
                selectedSpell);
    }

    public static void wandCircularSelection(GameTestHelper helper) {
        ItemStack wand = EBItems.MASTER_WAND.get().getDefaultInstance();
        Spell spell = Spells.FIREBALL;

        ArcaneWorkbenchTest.TestContext ctx = ArcaneWorkbenchTest.setupTest(helper, wand);
        ctx.workbench().setItem(0, SpellUtil.spellBookItem(spell));
        ctx.menu().onApplyButtonPressed(ctx.player());

        wand = ctx.workbench().getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);

        // repeat 5 times to ensure circular behavior
        for (int i = 0; i < 5; i++) {
            ((ISpellCastingItem) wand.getItem()).selectNextSpell(wand);
        }

        Spell selectedSpell = WandHelper.getCurrentSpell(wand);
        GST.assertEquals(helper,
                "Selected spell should be 'Fireball' after cycling next through all slots.",
                spell,
                selectedSpell);

        for (int i = 0; i < 5; i++) {
            ((ISpellCastingItem) wand.getItem()).selectPreviousSpell(wand);
        }
        selectedSpell = WandHelper.getCurrentSpell(wand);
        GST.assertEquals(helper,
                "Selected spell should be 'Fireball' after cycling previous through all slots.",
                spell,
                selectedSpell);
    }

    public static void wandLiteralIndex(GameTestHelper helper) {
        ItemStack wand = EBItems.MASTER_WAND.get().getDefaultInstance();
        List<Spell> spells = List.of(
                Spells.FIREBALL,
                Spells.ICE_SHARD,
                Spells.HEAL,
                Spells.LIFE_DRAIN,
                Spells.EVADE
        );

        ArcaneWorkbenchTest.TestContext ctx = ArcaneWorkbenchTest.setupTest(helper, wand);
        for (int i = 0; i < spells.size(); i++) {
            ctx.workbench().setItem(i, SpellUtil.spellBookItem(spells.get(i)));
        }

        ctx.menu().onApplyButtonPressed(ctx.player());

        wand = ctx.workbench().getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        List<Spell> wandSpells = WandHelper.getSpells(wand);
        ItemStack finalWand = wand;
        spells.forEach(spell -> GST.assertTrue(helper,
                "Wand %s should contain %s spell after applying.".formatted(finalWand, spell),
                wandSpells.contains(spell)));

        ((ISpellCastingItem) wand.getItem()).selectSpell(wand, 2);
        Spell selectedSpell = WandHelper.getCurrentSpell(wand);
        GST.assertEquals(helper,
                "Selected spell should be 'Heal' after selecting index 2.",
                Spells.HEAL,
                selectedSpell);

        ((ISpellCastingItem) wand.getItem()).selectSpell(wand, 4);
        selectedSpell = WandHelper.getCurrentSpell(wand);
        GST.assertEquals(helper,
                "Selected spell should be 'Evade' after selecting index 4.",
                Spells.EVADE,
                selectedSpell);

        ((ISpellCastingItem) wand.getItem()).selectSpell(wand, 0);
        selectedSpell = WandHelper.getCurrentSpell(wand);
        GST.assertEquals(helper,
                "Selected spell should be 'Fireball' after selecting index 0.",
                Spells.FIREBALL,
                selectedSpell);
    }

    public static void wandLiteralIndexPartiallyEmpty(GameTestHelper helper) {
        ItemStack wand = EBItems.MASTER_WAND.get().getDefaultInstance();
        Spell spell = Spells.ICE_SHARD;

        ArcaneWorkbenchTest.TestContext ctx = ArcaneWorkbenchTest.setupTest(helper, wand);
        ctx.workbench().setItem(0, SpellUtil.spellBookItem(spell));
        ctx.menu().onApplyButtonPressed(ctx.player());

        wand = ctx.workbench().getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        ((ISpellCastingItem) wand.getItem()).selectSpell(wand, 3);

        int selectedIndex = WandHelper.getCurrentSpellIndex(wand);
        // should be 3, even though only index 0 is filled
        GST.assertEquals(helper,
                "Selected spell index should be 3 after selecting index 3 on a partially empty wand.",
                3,
                selectedIndex);

        ((ISpellCastingItem) wand.getItem()).selectSpell(wand, 2);
        selectedIndex = WandHelper.getCurrentSpellIndex(wand);
        GST.assertEquals(helper,
                "Selected spell index should be 2 after selecting index 2 on a partially empty wand.",
                2,
                selectedIndex);
    }

    private WandTest() {
    }
}
