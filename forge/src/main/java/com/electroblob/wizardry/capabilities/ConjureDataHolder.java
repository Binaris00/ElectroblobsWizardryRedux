package com.electroblob.wizardry.capabilities;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.data.ConjureData;
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
 * Loading the conjure data with Forge, nothing too crazy over here, just using the capabilities to load-change the data
 */
public class ConjureDataHolder implements INBTSerializable<CompoundTag>, ConjureData {
    public static final ResourceLocation LOCATION = WizardryMainMod.location("conjure");
    public static final Capability<ConjureDataHolder> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    private final ItemStack stack;

    public ConjureDataHolder(ItemStack stack) {
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
        if (!this.stack.getOrCreateTag().contains("expire_time"))
            this.stack.getOrCreateTag().putLong("expire_time", -1L);
        return this.stack.getOrCreateTag().getLong("expire_time");
    }

    @Override
    public void setExpireTime(long expireTime) {
        this.stack.getOrCreateTag().putLong("expire_time", expireTime);
    }

    @Override
    public int getDuration() {
        if (!this.stack.getOrCreateTag().contains("duration")) this.stack.getOrCreateTag().putInt("duration", 0);
        return this.stack.getOrCreateTag().getInt("duration");
    }

    @Override
    public void setDuration(int duration) {
        this.stack.getOrCreateTag().putInt("duration", duration);
    }

    @Override
    public boolean isSummoned() {
        if (!this.stack.getOrCreateTag().contains("is_summoned"))
            this.stack.getOrCreateTag().putBoolean("is_summoned", false);
        return this.stack.getOrCreateTag().getBoolean("is_summoned");
    }

    @Override
    public void setSummoned(boolean summoned) {
        this.stack.getOrCreateTag().putBoolean("is_summoned", summoned);
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final LazyOptional<ConjureDataHolder> dataHolder;

        public Provider(ItemStack stack) {
            this.dataHolder = LazyOptional.of(() -> new ConjureDataHolder(stack));
        }

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction arg) {
            return ConjureDataHolder.INSTANCE.orEmpty(capability, dataHolder.cast());
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
