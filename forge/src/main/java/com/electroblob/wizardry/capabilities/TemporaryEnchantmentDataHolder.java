package com.electroblob.wizardry.capabilities;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.data.TemporaryEnchantmentData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

/**
 * Forge implementation for tracking temporary enchantments on items using Capabilities.
 * Similar to {@link ConjureDataHolder} but for temporary enchantments.
 */
public class TemporaryEnchantmentDataHolder implements INBTSerializable<CompoundTag>, TemporaryEnchantmentData {
    public static final ResourceLocation LOCATION = WizardryMainMod.location("temporary_enchantment");
    public static final Capability<TemporaryEnchantmentDataHolder> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    private final ItemStack stack;

    public TemporaryEnchantmentDataHolder(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public CompoundTag serializeNBT() {
        return stack.getOrCreateTag();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        stack.setTag(tag);
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

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final LazyOptional<TemporaryEnchantmentDataHolder> dataHolder;

        public Provider(ItemStack stack) {
            this.dataHolder = LazyOptional.of(() -> new TemporaryEnchantmentDataHolder(stack));
        }

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction arg) {
            return TemporaryEnchantmentDataHolder.INSTANCE.orEmpty(capability, dataHolder.cast());
        }

        @Override
        public CompoundTag serializeNBT() {
            return dataHolder.orElseThrow(NullPointerException::new).serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag arg) {
            dataHolder.orElseThrow(NullPointerException::new).deserializeNBT(arg);
        }
    }
}
