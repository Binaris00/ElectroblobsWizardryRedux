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

    private int lifetime = -1;
    private int maxLifetime = -1;
    private boolean summoned = false;

    private final ItemStack stack;

    public ConjureDataHolder(ItemStack stack) {
        this.stack = stack;
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

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("lifetime", this.lifetime);
        tag.putInt("max_lifetime", this.maxLifetime);
        tag.putBoolean("is_summoned", this.summoned);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.lifetime = tag.getInt("lifetime");
        this.maxLifetime = tag.getInt("max_lifetime");
        this.summoned = tag.getBoolean("is_summoned");
    }

    @Override
    public void lifetimeDecrement() {
        if(this.lifetime > 0) this.lifetime--;
    }

    @Override
    public int getLifetime() {
        return this.lifetime;
    }

    @Override
    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    @Override
    public int getMaxLifetime() {
        return this.maxLifetime;
    }

    @Override
    public void setMaxLifetime(int maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    @Override
    public boolean isSummoned() {
        return this.summoned;
    }

    @Override
    public void setSummoned(boolean summoned) {
        this.summoned = summoned;
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
