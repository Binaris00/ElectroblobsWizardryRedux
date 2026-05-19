package com.binaris.wizardry.core.gametest;

import com.binaris.wizardry.api.content.data.ImbuementEnchantData;
import com.binaris.wizardry.api.content.event.EBLivingTick;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.core.DataEvents;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;

public final class ImbuementEnchantsTestHandler {
    private static final Vec3 PLAYER_POS = new Vec3(1.5, 2.0, 1.5);

    /**
     * Puts an imbuement enchant to an item and check if the enchant is present
     */
    public static void simpleEnchantTest(GameTestHelper helper) {
        // The check is done in setupTest
        TestContext ctx = setupTest(helper, Items.DIAMOND_SWORD.getDefaultInstance(), Enchantments.FIRE_ASPECT);
        helper.succeed();
    }

    /**
     * Puts an imbuement enchant alongside a normal enchant, checks if both are present
     */
    public static void simpleEnchantCompanyTest(GameTestHelper helper) {
        TestContext ctx = setupTest(helper, Items.DIAMOND_SWORD.getDefaultInstance(), Enchantments.FIRE_ASPECT);
        ctx.stack.enchant(Enchantments.UNBREAKING, 1);
        GST.assertTrue(helper, "Imbuement enchant not present after applying", EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, ctx.player) > 0);
        GST.assertTrue(helper, "Normal enchant not present after applying with imbuement enchant", EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, ctx.player) > 0);
        helper.succeed();
    }

    /**
     * Puts an imbuement enchant to an item and checks after the end time if the enchantment is removed
     */
    public static void imbuementTickTest(GameTestHelper helper) {
        TestContext ctx = setupTest(helper, Items.DIAMOND_SWORD.getDefaultInstance(), Enchantments.FIRE_ASPECT);

        helper.onEachTick(() -> DataEvents.onPlayerTick(new EBLivingTick(ctx.player, ctx.player.level())));

        helper.runAtTickTime(60, () -> {
            DataEvents.onPlayerTick(new EBLivingTick(ctx.player, ctx.player.level()));
            GST.assertFalse(helper, "Imbuement enchant should be removed after 40 ticks (end time)", EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, ctx.player) > 0);
            helper.succeed();
        });
    }

    /**
     * Puts an imbuement enchant alongside a normal enchant, checks if the normal enchant is still present and the imbuement is removed after the end time
     */
    public static void imbuementTickCompanyTest(GameTestHelper helper) {
        TestContext ctx = setupTest(helper, Items.DIAMOND_SWORD.getDefaultInstance(), Enchantments.SHARPNESS);
        ctx.stack.enchant(Enchantments.UNBREAKING, 1);

        helper.onEachTick(() -> DataEvents.onPlayerTick(new EBLivingTick(ctx.player, ctx.player.level())));

        helper.runAtTickTime(60, () -> {
            DataEvents.onPlayerTick(new EBLivingTick(ctx.player, ctx.player.level()));
            GST.assertFalse(helper, "Imbuement enchant should be removed after 40 ticks (end time)", EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, ctx.player) > 0);
            GST.assertTrue(helper, "Normal enchant not present after removing imbuement", EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, ctx.player) > 0);
            helper.succeed();
        });
    }

    static TestContext setupTest(GameTestHelper helper, ItemStack stack, Enchantment enchantment) {
        Player player = GST.mockPlayer(helper, PLAYER_POS);
        player.setItemSlot(EquipmentSlot.MAINHAND, stack);
        ImbuementEnchantData data = Services.OBJECT_DATA.getImbuementData(stack);
        stack.enchant(enchantment, 1);
        data.addImbuement(enchantment, player.level().getGameTime() + 40);

        GST.assertTrue(helper, "Imbuement enchant not present after applying", EnchantmentHelper.getEnchantmentLevel(enchantment, player) > 0);
        return new TestContext(player, stack, enchantment, data);
    }

    record TestContext(Player player, ItemStack stack, Enchantment enchantment, ImbuementEnchantData data) {
    }

    private ImbuementEnchantsTestHandler() {
    }
}