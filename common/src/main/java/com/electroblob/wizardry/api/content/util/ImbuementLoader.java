package com.electroblob.wizardry.api.content.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ImbuementLoader {
    private final Item item;
    private final Enchantment imbuement;
    private int timeLimit;

    public ImbuementLoader(Item item, Enchantment imbuement, int timeLimit) {
        this.item = item;
        this.imbuement = imbuement;
        this.timeLimit = timeLimit;
    }

    public boolean hasReachedLimit(){
        timeLimit -= 1;
        return timeLimit <= 0;
    }

    public CompoundTag serializeNbt(CompoundTag tag){
        tag.put("item", StringTag.valueOf(BuiltInRegistries.ITEM.getKey(item).toString()));
        tag.put("imbuement", StringTag.valueOf(imbuement.getDescriptionId()));
        tag.putInt("timeLimit", timeLimit);
        return tag;
    }

    public static ImbuementLoader deserializeNbt(CompoundTag tag){
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(tag.getString("item")));
        Enchantment imbuement = BuiltInRegistries.ENCHANTMENT.get(ResourceLocation.tryParse(tag.getString("imbuement")));
        int timeLimit = tag.getInt("timeLimit");
        return new ImbuementLoader(item, imbuement, timeLimit);
    }

    public Item getItem() {
        return item;
    }

    public Enchantment getImbuement() {
        return imbuement;
    }

    public int getTimeLimit() {
        return timeLimit;
    }
}
