package com.electroblob.wizardry.capabilities;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.ConjureItemData;
import com.electroblob.wizardry.api.MinionData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
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

public class ForgeConjureItemData implements INBTSerializable<CompoundTag> {
    public static final Capability<ForgeConjureItemData> CONJURE_ITEM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    ConjureItemData conjureItemData;
    ItemStack provider;

    public ForgeConjureItemData(ItemStack stack) {
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
            e.addCapability(WizardryMainMod.location("conjure_item"), new ForgeConjureItemData.Provider(e.getObject()));
        }
    }

    public static ForgeConjureItemData get(ItemStack stack) {
        return stack.getCapability(CONJURE_ITEM_CAPABILITY).orElse(null);
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        private final LazyOptional<ForgeConjureItemData> data;

        public Provider(ItemStack stack) {
            this.data = LazyOptional.of(() -> new ForgeConjureItemData(stack));
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
