package com.binaris.wizardry.capabilities;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.data.ConjureData;
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

    private CompoundTag tag = new CompoundTag();

    public ConjureDataHolder() {
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (!this.tag.isEmpty()) tag.put("conjureData", this.tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("conjureData")) {
            this.tag = tag.getCompound("conjureData");
        } else {
            this.tag = new CompoundTag();
        }
    }

    @Override
    public long getExpireTime() {
        if (!this.tag.contains("expire_time"))
            this.tag.putLong("expire_time", -1L);
        return this.tag.getLong("expire_time");
    }

    @Override
    public void setExpireTime(long expireTime) {
        this.tag.putLong("expire_time", expireTime);
    }

    @Override
    public int getDuration() {
        if (!this.tag.contains("duration")) this.tag.putInt("duration", 0);
        return this.tag.getInt("duration");
    }

    @Override
    public void setDuration(int duration) {
        this.tag.putInt("duration", duration);
    }

    @Override
    public boolean isSummoned() {
        if (!this.tag.contains("is_summoned"))
            this.tag.putBoolean("is_summoned", false);
        return this.tag.getBoolean("is_summoned");
    }

    @Override
    public void setSummoned(boolean summoned) {
        this.tag.putBoolean("is_summoned", summoned);
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final LazyOptional<ConjureDataHolder> dataHolder;

        @SuppressWarnings("unused")
        public Provider(ItemStack stack) {
            this.dataHolder = LazyOptional.of(ConjureDataHolder::new);
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
