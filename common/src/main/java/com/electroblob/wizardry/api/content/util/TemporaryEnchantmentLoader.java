package com.electroblob.wizardry.api.content.util;

import com.electroblob.wizardry.api.EBLogger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Tracks temporary enchantments applied to items. Unlike {@link ImbuementLoader}, this system
 * works with ANY enchantment (vanilla or modded), not just custom Imbuement enchantments.
 * <p>
 * Each loader tracks:
 * <ul>
 *   <li>The item type that has the enchantment</li>
 *   <li>The enchantment and its level</li>
 *   <li>The time limit (in ticks) before it expires</li>
 *   <li>A unique UUID for tracking in the item's NBT</li>
 * </ul>
 */
public final class TemporaryEnchantmentLoader {
    private final @NotNull Item item;
    private final @NotNull Enchantment enchantment;
    private final int level;
    private final String uuid;
    private int timeLimit;

    public TemporaryEnchantmentLoader(@NotNull Item item, @NotNull Enchantment enchantment, int level, int timeLimit) {
        this.item = item;
        this.enchantment = enchantment;
        this.level = level;
        this.timeLimit = timeLimit;
        this.uuid = UUID.randomUUID().toString();
    }

    private TemporaryEnchantmentLoader(@NotNull Item item, @NotNull Enchantment enchantment, int level, int timeLimit, String uuid) {
        this.item = item;
        this.enchantment = enchantment;
        this.level = level;
        this.timeLimit = timeLimit;
        this.uuid = uuid;
    }

    public static TemporaryEnchantmentLoader deserializeNbt(CompoundTag tag) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(tag.getString("item")));
        Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(ResourceLocation.tryParse(tag.getString("enchantment")));
        int level = tag.getInt("level");
        int timeLimit = tag.getInt("timeLimit");
        String uuid = tag.contains("uuid") ? tag.getString("uuid") : UUID.randomUUID().toString();
        if (item == null || enchantment == null) {
            EBLogger.error("Failed to deserialize temporary enchantment loader: " + tag + " (item null: " + (item == null) + ", enchantment null: " + (enchantment == null) + ")");
        }
        return new TemporaryEnchantmentLoader(item, enchantment, level, timeLimit, uuid);
    }

    public static String getTagName(Enchantment enchantment) {
        return "temp_enchant_" + enchantment.getDescriptionId();
    }

    public boolean hasReachedLimit() {
        timeLimit -= 1;
        return timeLimit <= 0;
    }

    public CompoundTag serializeNbt(CompoundTag tag) {
        if (item == null || enchantment == null) {
            EBLogger.error("Failed to serialize temporary enchantment loader: " + tag + " (item null: " + (item == null) + ", enchantment null: " + (enchantment == null) + ")");
        }
        tag.put("item", StringTag.valueOf(BuiltInRegistries.ITEM.getKey(item).toString()));
        tag.put("enchantment", StringTag.valueOf(BuiltInRegistries.ENCHANTMENT.getKey(enchantment).toString()));
        tag.putInt("level", level);
        tag.putInt("timeLimit", timeLimit);
        tag.putString("uuid", uuid);
        return tag;
    }

    public @NotNull Item getItem() {
        return item;
    }

    public @NotNull Enchantment getEnchantment() {
        return enchantment;
    }

    public int getLevel() {
        return level;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TemporaryEnchantmentLoader)) return false;
        TemporaryEnchantmentLoader other = (TemporaryEnchantmentLoader) obj;
        return this.uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    /**
     * Checks if this loader is valid for the given item stack.
     * A loader is valid if:
     * - The item matches
     * - The enchantment is present on the item
     * - The item has the tracking tag for this specific loader
     */
    public boolean isValid(ItemStack stack) {
        return stack.getItem().equals(item) &&
                EnchantmentHelper.getEnchantments(stack).containsKey(enchantment) &&
                stack.getOrCreateTag().contains(getTagName(enchantment));
    }
}
