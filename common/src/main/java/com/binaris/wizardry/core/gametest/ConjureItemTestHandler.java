package com.binaris.wizardry.core.gametest;

import com.binaris.wizardry.api.content.data.ConjureData;
import com.binaris.wizardry.api.content.event.EBLivingTick;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.InventoryUtil;
import com.binaris.wizardry.core.DataEvents;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class ConjureItemTestHandler {
    private static final Vec3 PLAYER_POS = new Vec3(1.5, 2.0, 1.5);

    /**
     * Here we just want to check if the way of spawning conjure items are working, in order to not make the test too
     * complex we use the flamecatcher spell as it is one of the simple conjure item spells, and we just check if the
     * player receives the item after casting the spell.
     */
    public static void spawnConjureItem(GameTestHelper helper) {
        Player player = GST.mockServerPlayer(helper, PLAYER_POS);
        Spells.FLAMECATCHER.cast(new PlayerCastContext(helper.getLevel(), player, InteractionHand.MAIN_HAND, 0, new SpellModifiers()));

        InventoryUtil.getAllItems(player).stream().filter(stack -> stack.getItem().equals(EBItems.FLAMECATCHER.get())).findAny()
                .ifPresentOrElse(stack -> helper.succeed(),
                        () -> helper.fail("Player did not receive the conjured item (flamecatcher)"));
    }


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


    private static TestContext setupTest(GameTestHelper helper, Item item, int duration) {
        Player player = GST.mockServerPlayer(helper, PLAYER_POS);
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

    private ConjureItemTestHandler() {
    }
}
