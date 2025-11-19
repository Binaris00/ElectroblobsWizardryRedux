package com.electroblob.wizardry.capabilities;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.data.ImbuementEnchantData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ImbuementEnchantDataHolder implements INBTSerializable<CompoundTag>, ImbuementEnchantData {
    public static final ResourceLocation LOCATION = WizardryMainMod.location("imbuement_enchant");
    public static final Capability<ImbuementEnchantDataHolder> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    private final ItemStack stack;

    public ImbuementEnchantDataHolder(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void addImbuement(Enchantment enchant, long expireTime) {
        String enchantId = ForgeRegistries.ENCHANTMENTS.getKey(enchant).toString();

        if (!getOrCreateRootTag().contains(enchantId))
            getOrCreateRootTag().putLong(enchantId, expireTime);
    }

    @Override
    public Map<ResourceLocation, Long> getImbuements() {
        Map<ResourceLocation, Long> result = new HashMap<>();

        // If there is no root tag, this means there are no temporary enchantments, so we can return an empty map
        if (getRootTag() == null) return result;


        this.getRootTag().getAllKeys().forEach(key -> {
            try {
                ResourceLocation enchantId = ResourceLocation.tryParse(key);
                long expireTime = this.getRootTag().getLong(key);
                result.put(enchantId, expireTime);
            } catch (Exception e) {
                // Ignore invalid keys
            }
        });

        return result;
    }

    @Override
    public void removeImbuement(Enchantment enchant) {
        String enchantId = ForgeRegistries.ENCHANTMENTS.getKey(enchant).toString();

        if (getOrCreateRootTag().contains(enchantId)) getOrCreateRootTag().remove(enchantId);
    }

    @Override
    public boolean isImbuement(Enchantment enchant) {
        ResourceLocation enchantId = ForgeRegistries.ENCHANTMENTS.getKey(enchant);
        if (enchantId == null) return false;
        return this.getRootTag() != null && this.getRootTag().contains(enchantId.toString());
    }

    @Override
    public long getExpirationTime(Enchantment enchantment) {
        ResourceLocation enchantId = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
        if (enchantId == null) return -1;
        if (getOrCreateRootTag().contains(enchantId.toString())) return getOrCreateRootTag().getLong(enchantId.toString());
        return -1;
    }

    private CompoundTag getOrCreateRootTag() {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("imbuements")) {
            return tag.getCompound("imbuements");
        }

        CompoundTag imbueTag = new CompoundTag();
        tag.put("imbuements", imbueTag);
        return imbueTag;
    }

    private CompoundTag getRootTag() {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("imbuements")) {
            return tag.getCompound("imbuements");
        }
        return null;
    }

    @Override
    public CompoundTag serializeNBT() {
        return stack.getOrCreateTag();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        stack.setTag(tag);
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final LazyOptional<ImbuementEnchantDataHolder> dataHolder;

        public Provider(ItemStack stack) {
            this.dataHolder = LazyOptional.of(() -> new ImbuementEnchantDataHolder(stack));
        }

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction arg) {
            return ImbuementEnchantDataHolder.INSTANCE.orEmpty(capability, dataHolder.cast());
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
