package com.electroblob.wizardry.setup.datagen;

import com.electroblob.wizardry.api.common.DeferredObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

public final class DataGenProcessor {

    // TODO: make datagen alive only when datagen is actually running
    private static boolean isDataGening;
    private Map<String, DeferredObject<? extends Item>> DEFAULT_ITEMS = new HashMap<>();
    private Map<String, DeferredObject<? extends Item>> WAND_ITEMS = new HashMap<>();
    private Map<String, DeferredObject<Block>> DEFAULT_BLOCK_MODELS = new HashMap<>();
    private Map<String, DeferredObject<Block>> DEFAULT_BLOCK_DROP = new HashMap<>();

    public void addDefaultItem(String name, DeferredObject<? extends Item> item) {
        DEFAULT_ITEMS.put(name, item);
    }

    public void addWandItem(String name, DeferredObject<? extends Item> item) {
        WAND_ITEMS.put(name, item);
    }
    /**
     * Makes the block model and item model for the block the default
     */
    public void addDefaultBlockModel(String name, DeferredObject<Block> blockModel) {
        DEFAULT_BLOCK_MODELS.put(name, blockModel);
    }
    /**
     * Block drop itself
     */
    public void addDefaultBlockDrop(String name, DeferredObject<Block> blockDrop) {
        DEFAULT_BLOCK_DROP.put(name, blockDrop);
    }

    public Map<String, DeferredObject<? extends Item>> items() {
        return DEFAULT_ITEMS;
    }

    public Map<String, DeferredObject<? extends Item>> wandItems() {
        return WAND_ITEMS;
    }
    public Map<String, DeferredObject<Block>> defaultBlockModels() {
        return DEFAULT_BLOCK_MODELS;
    }
    public Map<String, DeferredObject<Block>> defaultBlockDrops() {
        return DEFAULT_BLOCK_DROP;
    }

    private static class Instance {
        private static final DataGenProcessor INSTANCE = new DataGenProcessor();
    }

    public static DataGenProcessor get() {
        return Instance.INSTANCE;
    }

    public static boolean status() {
        return isDataGening;
    }

    public static void activate() {
        isDataGening = true;
    }

    private DataGenProcessor() {}
}
