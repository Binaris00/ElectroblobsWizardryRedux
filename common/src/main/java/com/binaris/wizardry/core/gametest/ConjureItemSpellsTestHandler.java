package com.binaris.wizardry.core.gametest;

import com.binaris.wizardry.api.content.data.ConjureData;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.InventoryUtil;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.DataEvents;
import com.binaris.wizardry.core.EBLogger;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class ConjureItemSpellsTestHandler {
    private static final Vec3 PLAYER_POS = new Vec3(1.5, 2.0, 1.5);

    /**
     * Test if the Conjure Item Spells are working, spawning the item and placing it in the player's inventory, taking the
     * spell Flamecatcher as an example.
     */
    public static void spawnConjureItem(GameTestHelper helper) {
        Player player = GST.mockPlayer(helper, PLAYER_POS);
        Spells.FLAMECATCHER.cast(new PlayerCastContext(helper.getLevel(), player, InteractionHand.MAIN_HAND, 0, new SpellModifiers()));

        InventoryUtil.getAllItems(player).stream().filter(stack -> stack.getItem().equals(EBItems.FLAMECATCHER.get())).findAny()
                .ifPresentOrElse(stack -> helper.succeed(),
                        () -> helper.fail("Player did not receive the conjured item (flamecatcher)"));
    }

    /** Test if the Conjure Item Spells are working, despawing the item after the time limit */
    public static void conjureItemDespawn(GameTestHelper helper) {
        TestContext ctx = setupTest(helper, EBItems.FLAMECATCHER.get(), 20);
        helper.runAtTickTime(30, () -> {
            DataEvents.conjureItemTick(ctx.player);

            if (InventoryUtil.getAllItems(ctx.player).stream().anyMatch(stack -> stack.getItem().equals(EBItems.FLAMECATCHER.get()))) {
                helper.fail("Conjured item still in player's inventory after duration has expired");
                return;
            }

            helper.succeed();
        });
    }

    public static void buildTable(GameTestHelper helper) {
        SpellTables.Builder table = new SpellTables.Builder()
                .addColumn(SpellTables.columnByProperty("Lifetime", DefaultProperties.ITEM_LIFETIME, SpellModifiers.DURATION));

        SpellTables.addDefaultColumns(table);
        SpellTables.addDefaultRows(table, Spells.FLAMECATCHER, helper, PLAYER_POS, Elements.FIRE);
        EBLogger.info(table.build().toString());
        helper.succeed();
    }

    static TestContext setupTest(GameTestHelper helper, Item item, int duration) {
        Player player = GST.mockPlayer(helper, PLAYER_POS);
        ItemStack stack = new ItemStack(item);
        ConjureData data = Services.OBJECT_DATA.getConjureData(stack);

        data.setExpireTime(helper.getLevel().getGameTime() + duration);
        data.setDuration(duration);
        data.setSummoned(true);

        player.getInventory().add(stack);
        return new TestContext(player, data, stack);
    }

    protected record TestContext(Player player, ConjureData data, ItemStack stack) {
    }

    private ConjureItemSpellsTestHandler() {
    }
}
