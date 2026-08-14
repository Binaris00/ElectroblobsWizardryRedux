package com.binaris.wizardry.api.content.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/// Contract for items that store a mana value, such as wands and wizard armor.
///
/// Implemented by {@code WandItem}, {@code WizardArmorItem}, and {@code CrystalItem}. The three
/// abstract methods ({@code getMana}, {@code setMana}, {@code getManaCapacity}) define the raw
/// storage interface, six default methods build common operations on top: consume, recharge,
/// fullness checks, and visibility control in the Arcane Workbench GUI. Callers throughout the
/// codebase test {@code instanceof IManaItem} on item stacks to conditionally apply mana logic.
public interface IManaItem {
    /// Returns the current mana of the given item stack.
    ///
    /// The canonical implementation stores mana as the stack's damage value ({@code getDamageValue()}),
    /// treating higher damage as lower mana. This means vanilla durability tooltips are suppressed
    /// in favor of a custom mana bar via {@code ItemStackMixin}.
    ///
    /// @param stack the item stack to query.
    ///
    /// @return the current mana value; never negative in practice.
    int getMana(ItemStack stack);

    /// Sets the current mana of the given item stack.
    ///
    /// Implementations typically update the stack's damage value inversely ({@code setDamageValue(capacity - mana)}).
    /// Must be called server-side when the value should persist; client-side calls are not synced
    /// automatically.
    ///
    /// @param stack the item stack to modify.
    /// @param mana  the new mana value, clamped to [0, capacity] by callers — implementations
    ///              are responsible for clamping.
    void setMana(ItemStack stack, int mana);

    /// Returns the maximum mana capacity of the given item stack.
    ///
    /// Used to compute fullness ratios, determine whether an item needs recharging, and calculate
    /// how many crystals are needed in the workbench. This is a property of the item type (or its
    /// tier), not the current stack damage.
    ///
    /// @param stack the item stack to query.
    ///
    /// @return the maximum mana this item can hold.
    int getManaCapacity(ItemStack stack);

    /// Whether this item should display its mana bar in the Arcane Workbench GUI.
    ///
    /// The workbench tooltip checks this before rendering the mana readout. Override to return
    /// {@code false} for items that should not expose their mana in that UI (e.g. {@code CrystalItem}).
    /// The default returns {@code true}.
    ///
    /// @param player the player viewing the workbench; may be null on the client if no local player
    ///               is logged in.
    /// @param stack  the item stack being rendered.
    ///
    /// @return {@code true} to show the mana bar, {@code false} to hide it.
    default boolean showManaInWorkbench(Player player, ItemStack stack) {
        return true;
    }

    /// Deducts the given amount of mana from the stack, clamping the result to zero.
    ///
    /// Creative-mode players bypass the deduction entirely. Used by wand cast logic, armor-based
    /// artifact effects, and the forfeit penalty handler. The default implementation calls
    /// {@code getMana}, subtracts, clamps, and writes back via {@code setMana}.
    ///
    /// @param stack    the item stack whose mana to drain.
    /// @param mana     the amount of mana to consume.
    /// @param wielder  the entity that owns this stack; may be null if the context has no wielder
    ///                 (e.g. an item entity).
    default void consumeMana(ItemStack stack, int mana, @Nullable LivingEntity wielder) {
        if (wielder instanceof Player && ((Player) wielder).isCreative()) return;
        setMana(stack, Math.max(getMana(stack) - mana, 0));
    }

    /// Adds the given amount of mana to the stack, clamping to the item's capacity.
    ///
    /// Used by condenser upgrades, mana flasks, artifact ring effects, force arrows returning
    /// unused mana, and the workbench crystal recharge logic. Callers typically guard with
    /// {@code isManaFull} before invoking to avoid unnecessary writes. The default implementation
    /// reads current mana, adds, clamps to capacity, and writes back via {@code setMana}.
    ///
    /// @param stack the item stack to recharge.
    /// @param mana  the amount of mana to add.
    default void rechargeMana(ItemStack stack, int mana) {
        setMana(stack, Math.min(getMana(stack) + mana, getManaCapacity(stack)));
    }

    /// Returns true if the stack's mana equals its capacity.
    ///
    /// Used as a guard before condenser ticks, mana flask activation, siphon upgrades, and
    /// workbench recharging to skip items that are already topped off. The default compares
    /// {@code getMana} against {@code getManaCapacity}.
    ///
    /// @param stack the item stack to check.
    ///
    /// @return {@code true} if mana is at capacity.
    default boolean isManaFull(ItemStack stack) {
        return getMana(stack) == getManaCapacity(stack);
    }

    /// Returns true if the stack has no mana remaining.
    ///
    /// Used by {@code EntityUtil.doAllArmorPiecesHaveMana()} to determine whether wizard armor
    /// set bonuses should be disabled. The default compares {@code getMana} against zero.
    ///
    /// @param stack the item stack to check.
    ///
    /// @return {@code true} if mana is zero.
    default boolean isManaEmpty(ItemStack stack) {
        return getMana(stack) == 0;
    }

    /// Returns the fraction of mana remaining, as a float in [0.0, 1.0].
    ///
    /// Used by the mana flask to find the least-full IManaItem for prioritized recharging.
    /// The default divides {@code getMana} by {@code getManaCapacity}. If capacity is zero,
    /// the result is {@code Float.NaN} and callers should guard against this.
    ///
    /// @param stack the item stack to query.
    ///
    /// @return the fullness ratio from 0.0 (empty) to 1.0 (full).
    default float getFullness(ItemStack stack) {
        return (float) getMana(stack) / getManaCapacity(stack);
    }
}
