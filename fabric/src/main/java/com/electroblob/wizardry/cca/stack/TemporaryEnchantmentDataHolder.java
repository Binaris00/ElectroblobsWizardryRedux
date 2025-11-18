package com.electroblob.wizardry.cca.stack;

import com.electroblob.wizardry.api.content.data.TemporaryEnchantmentData;
import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * Fabric implementation for tracking temporary enchantments on items using Cardinal Components API.
 * Similar to {@link ConjureDataHolder} but for temporary enchantments.
 */
public class TemporaryEnchantmentDataHolder implements Component, TemporaryEnchantmentData {
    private final ItemStack stack;

    public TemporaryEnchantmentDataHolder(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        stack.setTag(tag);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.merge(stack.getOrCreateTag());
    }

    @Override
    public long getExpireTime() {
        if (!this.stack.getOrCreateTag().contains("temp_enchant_expire_time"))
            this.stack.getOrCreateTag().putLong("temp_enchant_expire_time", -1L);
        return this.stack.getOrCreateTag().getLong("temp_enchant_expire_time");
    }

    @Override
    public void setExpireTime(long expireTime) {
        this.stack.getOrCreateTag().putLong("temp_enchant_expire_time", expireTime);
    }

    @Override
    public int getDuration() {
        if (!this.stack.getOrCreateTag().contains("temp_enchant_duration"))
            this.stack.getOrCreateTag().putInt("temp_enchant_duration", 0);
        return this.stack.getOrCreateTag().getInt("temp_enchant_duration");
    }

    @Override
    public void setDuration(int duration) {
        this.stack.getOrCreateTag().putInt("temp_enchant_duration", duration);
    }

    @Override
    public boolean hasTemporaryEnchantment() {
        if (!this.stack.getOrCreateTag().contains("has_temp_enchant"))
            this.stack.getOrCreateTag().putBoolean("has_temp_enchant", false);
        return this.stack.getOrCreateTag().getBoolean("has_temp_enchant");
    }

    @Override
    public void setHasTemporaryEnchantment(boolean hasTemporary) {
        this.stack.getOrCreateTag().putBoolean("has_temp_enchant", hasTemporary);
    }
}
