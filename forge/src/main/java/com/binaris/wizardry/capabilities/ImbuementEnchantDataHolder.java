package com.binaris.wizardry.capabilities;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.data.ImbuementEnchantData;
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

    private CompoundTag tag = new CompoundTag();

    public ImbuementEnchantDataHolder() {
    }

    @Override
    public void addImbuement(Enchantment enchant, long expireTime) {
        ResourceLocation enchantKey = ForgeRegistries.ENCHANTMENTS.getKey(enchant);
        if (enchantKey == null) return;
        String enchantId = enchantKey.toString();

        if (!tag.contains(enchantId)) tag.putLong(enchantId, expireTime);
    }

    @Override
    public Map<ResourceLocation, Long> getImbuements() {
        Map<ResourceLocation, Long> result = new HashMap<>();

        tag.getAllKeys().forEach(key -> {
            try {
                ResourceLocation enchantId = ResourceLocation.tryParse(key);
                long expireTime = tag.getLong(key);
                result.put(enchantId, expireTime);
            } catch (Exception e) {
                // Ignore invalid keys
            }
        });

        return result;
    }

    @Override
    public void removeImbuement(Enchantment enchant) {
        ResourceLocation enchantKey = ForgeRegistries.ENCHANTMENTS.getKey(enchant);
        if (enchantKey == null) return;
        String enchantId = enchantKey.toString();

        if (tag.contains(enchantId))
            tag.remove(enchantId);
    }

    @Override
    public boolean isImbuement(Enchantment enchant) {
        ResourceLocation enchantId = ForgeRegistries.ENCHANTMENTS.getKey(enchant);
        if (enchantId == null) return false;
        return tag.contains(enchantId.toString());
    }

    @Override
    public long getExpirationTime(Enchantment enchantment) {
        ResourceLocation enchantId = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
        if (enchantId == null) return -1;
        if (tag.contains(enchantId.toString()))
            return tag.getLong(enchantId.toString());
        return -1;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("imbuements", this.tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("imbuements")) {
            this.tag = tag.getCompound("imbuements");
        } else {
            this.tag = new CompoundTag();
        }
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final LazyOptional<ImbuementEnchantDataHolder> dataHolder;

        @SuppressWarnings("unused")
        public Provider(ItemStack stack) {
            this.dataHolder = LazyOptional.of(ImbuementEnchantDataHolder::new);
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
