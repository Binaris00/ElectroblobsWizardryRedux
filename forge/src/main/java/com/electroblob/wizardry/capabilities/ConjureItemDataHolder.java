package com.electroblob.wizardry.capabilities;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.ConjureItemData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConjureItemDataHolder implements INBTSerializable<CompoundTag> {
    public static final Capability<ConjureItemDataHolder> CONJURE_ITEM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    ConjureItemData conjureItemData;
    ItemStack provider;

    public ConjureItemDataHolder(ItemStack stack) {
        this.provider = stack;
        this.conjureItemData = new ConjureItemData(provider);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        conjureItemData.serializeNBT(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.conjureItemData = conjureItemData.deserializeNBT(tag);
    }

    public ConjureItemData getConjureItemData() {
        return conjureItemData;
    }

    // ====================================================
    // Capability stuff
    // ====================================================
    public static void attachCapability(AttachCapabilitiesEvent<ItemStack> e) {
        if(ConjureItemData.applyItem(e.getObject().getItem())){
            e.addCapability(WizardryMainMod.location("conjure_item"), new ConjureItemDataHolder.Provider(e.getObject()));
        }
    }

    public static ConjureItemDataHolder get(ItemStack stack) {
        return stack.getCapability(CONJURE_ITEM_CAPABILITY).orElse(null);
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        private final LazyOptional<ConjureItemDataHolder> data;

        public Provider(ItemStack stack) {
            this.data = LazyOptional.of(() -> new ConjureItemDataHolder(stack));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
            return CONJURE_ITEM_CAPABILITY.orEmpty(capability, data.cast());
        }

        @Override
        public CompoundTag serializeNBT() {
            return data.orElseThrow(NullPointerException::new).serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            data.orElseThrow(NullPointerException::new).deserializeNBT(tag);
        }
    }
}
