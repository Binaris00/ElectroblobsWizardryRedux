package com.binaris.wizardry.core.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Why this is called GST...?
 * General GameTest utility methods.
 */
public class GST {

    private GST() {
    }

    public static Player mockServerPlayer(GameTestHelper helper, Vec3 position) {
        Player serverPlayer = helper.makeMockPlayer();
        GST.assertNotNull(helper, "ServerPlayer is null!", serverPlayer);
        serverPlayer.setPos(helper.absoluteVec(position));
        return serverPlayer;
    }

    /**
     * Asserts that the given condition is true, failing the test with the given message if it is false.
     */
    public static void assertTrue(GameTestHelper helper, String message, boolean condition) {
        if (!condition) {
            helper.fail(message);
        }
    }

    /**
     * Asserts that the given condition is false, failing the test with the given message if it is true.
     */
    public static void assertFalse(GameTestHelper helper, String message, boolean condition) {
        if (condition) {
            helper.fail(message);
        }
    }

    /**
     * Asserts that the given object is not null, failing the test with the given message if it is.
     */
    public static void assertNotNull(GameTestHelper helper, String message, Object object) {
        assertTrue(helper, message, object != null);
    }

    /**
     * Asserts that the given object is null, failing the test with the given message if it is not.
     */
    public static void assertNull(GameTestHelper helper, String message, Object object) {
        assertTrue(helper, message, object == null);
    }

    /**
     * Asserts that the expected and actual values are equal, failing the test with the given message if they are not.
     */
    public static <T> void assertEquals(GameTestHelper helper, String message, T expected, T actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected == null || !expected.equals(actual)) {
            helper.fail(message + " Expected: " + expected + ", but was: " + actual);
        }
    }

    /**
     * Asserts that the given ItemStack is empty, failing the test with the given message if it is not.
     */
    public static void assertEmpty(GameTestHelper helper, String message, ItemStack stack) {
        if (!stack.isEmpty()) {
            helper.fail(message + " Expected empty ItemStack, but found: " + stack);
        }
    }

    /**
     * Asserts that the given ItemStack is not empty, failing the test with the given message if it is.
     */
    public static void assertNotEmpty(GameTestHelper helper, String message, ItemStack stack) {
        if (stack.isEmpty()) {
            helper.fail(message + " Expected non-empty ItemStack, but was empty");
        }
    }
}