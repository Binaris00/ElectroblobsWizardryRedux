package com.electroblob.wizardry.capabilities;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.data.ConjureData;
import com.electroblob.wizardry.content.spell.abstr.ConjureItemSpell;
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
import org.jetbrains.annotations.Nullable;

/**
 * Loading the conjure data with Forge, nothing too crazy over here, just using the capabilities to load-change the data
 * */
public class ConjureDataHolder implements INBTSerializable<CompoundTag>, ConjureData {
    public static final ResourceLocation LOCATION = WizardryMainMod.location("conjure");
    public static final Capability<ConjureDataHolder> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    private final ItemStack stack;

    public ConjureDataHolder(ItemStack stack) {
        this.stack = stack;
        init();
    }

    @Override
    public void tick() {
        if (!isSummoned()) return;

        if (this.getLifetime() <= 0) {
            this.stack.shrink(1);
            this.setSummoned(false);
            return;
        }

        lifetimeDecrement();
    }

    private void init() {
        if (!this.stack.getOrCreateTag().contains("lifetime")) this.stack.getOrCreateTag().putInt("lifetime", -1);
        if (!this.stack.getOrCreateTag().contains("max_lifetime")) this.stack.getOrCreateTag().putInt("max_lifetime", -1);
        if (!this.stack.getOrCreateTag().contains("is_summoned")) this.stack.getOrCreateTag().putBoolean("is_summoned", false);
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
    public void lifetimeDecrement() {
        int lifetime = getLifetime();
        if (lifetime > 0) this.stack.getOrCreateTag().putInt("lifetime", lifetime - 1);
    }

    @Override
    public int getLifetime() {
        return stack.getOrCreateTag().getInt("lifetime");
    }

    @Override
    public void setLifetime(int lifetime) {
        stack.getOrCreateTag().putInt("lifetime", lifetime);
    }

    @Override
    public int getMaxLifetime() {
        return stack.getOrCreateTag().getInt("max_lifetime");
    }

    @Override
    public void setMaxLifetime(int maxLifetime) {
        stack.getOrCreateTag().putInt("max_lifetime", maxLifetime);
    }

    @Override
    public boolean isSummoned() {
        return stack.getOrCreateTag().getBoolean("is_summoned");
    }

    @Override
    public void setSummoned(boolean summoned) {
        stack.getOrCreateTag().putBoolean("is_summoned", summoned);
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final LazyOptional<ConjureDataHolder> dataHolder;

        public Provider(ItemStack stack) {
            this.dataHolder = LazyOptional.of(() -> new ConjureDataHolder(stack));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
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
