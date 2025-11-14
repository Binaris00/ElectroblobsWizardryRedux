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

public final class ImbuementLoader {
    private final @NotNull Item item;
    private final @NotNull Enchantment imbuement;
    private final String uuid;
    private int timeLimit;

    public ImbuementLoader(@NotNull Item item, @NotNull Enchantment imbuement, int timeLimit) {
        this.item = item;
        this.imbuement = imbuement;
        this.timeLimit = timeLimit;
        this.uuid = UUID.randomUUID().toString();
    }

    private ImbuementLoader(@NotNull Item item, @NotNull Enchantment imbuement, int timeLimit, String uuid) {
        this.item = item;
        this.imbuement = imbuement;
        this.timeLimit = timeLimit;
        this.uuid = uuid;
    }

    public static ImbuementLoader deserializeNbt(CompoundTag tag) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(tag.getString("item")));
        Enchantment imbuement = BuiltInRegistries.ENCHANTMENT.get(ResourceLocation.tryParse(tag.getString("imbuement")));
        int timeLimit = tag.getInt("timeLimit");
        String uuid = tag.contains("uuid") ? tag.getString("uuid") : UUID.randomUUID().toString();
        if (item == null || imbuement == null) {
            EBLogger.error("Failed to deserialize imbuement loader: " + tag + (item == null) + " " + (imbuement == null));
        }
        return new ImbuementLoader(item, imbuement, timeLimit, uuid);
    }

    public static String getTagName(Enchantment enchantment) {
        return "imbuement_" + enchantment.getDescriptionId();
    }

    public boolean hasReachedLimit() {
        timeLimit -= 1;
        return timeLimit <= 0;
    }

    public CompoundTag serializeNbt(CompoundTag tag) {
        if (item == null || imbuement == null) {
            EBLogger.error("Failed to serialize imbuement loader: " + tag + (item == null) + " " + (imbuement == null));
        }
        tag.put("item", StringTag.valueOf(BuiltInRegistries.ITEM.getKey(item).toString()));
        tag.put("imbuement", StringTag.valueOf(BuiltInRegistries.ENCHANTMENT.getKey(imbuement).toString()));
        tag.putInt("timeLimit", timeLimit);
        tag.putString("uuid", uuid);
        return tag;
    }

    public @NotNull Item getItem() {
        return item;
    }

    public @NotNull Enchantment getImbuement() {
        return imbuement;
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
        if (obj instanceof ImbuementLoader) return true;
        ImbuementLoader other = (ImbuementLoader) obj;
        return this.uuid.equals(other.uuid);
    }

    public boolean isValid(ItemStack stack) {
        return stack.getItem().equals(item) &&
                EnchantmentHelper.getEnchantments(stack).containsKey(imbuement) &&
                stack.getOrCreateTag().contains(getTagName(imbuement));
    }
}
