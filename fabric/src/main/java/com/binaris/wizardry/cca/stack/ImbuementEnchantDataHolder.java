package com.binaris.wizardry.cca.stack;

import com.binaris.wizardry.api.content.data.ImbuementEnchantData;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.HashMap;
import java.util.Map;

public class ImbuementEnchantDataHolder extends ItemComponent implements ImbuementEnchantData {
    public ImbuementEnchantDataHolder(ItemStack stack) {
        super(stack);
    }

    @Override
    public void addImbuement(Enchantment enchant, long expireTime) {
        ResourceLocation enchantId = BuiltInRegistries.ENCHANTMENT.getKey(enchant);
        if (enchantId == null) throw new IllegalArgumentException("Enchantment must have a valid ResourceLocation ID");
        this.putLong(enchantId.toString(), expireTime);
    }

    @Override
    public Map<ResourceLocation, Long> getImbuements() {
        Map<ResourceLocation, Long> result = new HashMap<>();

        // If there is no root tag, this means there are no temporary enchantments, so we can return an empty map
        if (getRootTag() == null) return result;

        this.getRootTag().getAllKeys().forEach(key -> {
            try {
                ResourceLocation enchantId = ResourceLocation.tryParse(key);
                long expireTime = this.getLong(key);
                result.put(enchantId, expireTime);
            } catch (Exception e) {
                // Ignore invalid keys
            }
        });

        return result;
    }

    @Override
    public void removeImbuement(Enchantment enchant) {
        ResourceLocation enchantId = BuiltInRegistries.ENCHANTMENT.getKey(enchant);
        if (enchantId == null) return;

        this.remove(enchantId.toString());
    }

    @Override
    public boolean isImbuement(Enchantment enchant) {
        ResourceLocation enchantId = BuiltInRegistries.ENCHANTMENT.getKey(enchant);
        if (enchantId == null) return false;
        return this.hasTag(enchantId.toString());
    }

    @Override
    public long getExpirationTime(Enchantment enchantment) {
        ResourceLocation enchantId = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
        if (enchantId == null) return -1;
        if (this.hasTag(enchantId.toString())) return this.getLong(enchantId.toString());
        return -1;
    }
}