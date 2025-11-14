package com.electroblob.wizardry.setup.datagen;

import com.electroblob.wizardry.api.content.DeferredObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

/**
 * <b>Electroblob's Wizardry Internal Use Only</b>
 * <br><br>
 * This is the main class that handles the basic data gen features for the mod
 * we use this to register all the just-one-file or small models with a generic behaviour.
 * <br><br>
 * Some of the things that are made here are: <i>items with just a simple texture, block items with a simple model and block states</i>
 * and also register the mods features that are frequently used but have the same behaviour among them, like wand items
 *
 */
public final class EBDataGenProcessor {
    private static final Map<String, DeferredObject<? extends Item>> DEFAULT_ITEMS = new HashMap<>();
    private static final Map<String, DeferredObject<? extends Item>> WAND_ITEMS = new HashMap<>();
    private static final Map<String, DeferredObject<Block>> DEFAULT_BLOCK_MODELS = new HashMap<>();
    private static final Map<String, DeferredObject<Block>> DEFAULT_BLOCK_DROP = new HashMap<>();

    private EBDataGenProcessor() {
    }

    /**
     * Default item with just a png as a model and texture
     */
    public static void addDefaultItem(String name, DeferredObject<? extends Item> item) {
        DEFAULT_ITEMS.put(name, item);
    }

    /**
     * Makes a regular wand item with the custom point model
     */
    public static void addWandItem(String name, DeferredObject<? extends Item> item) {
        WAND_ITEMS.put(name, item);
    }

    /**
     * Makes the block have a normal block item model and block state
     */
    public static void addDefaultBlockModel(String name, DeferredObject<Block> blockModel) {
        DEFAULT_BLOCK_MODELS.put(name, blockModel);
    }

    /**
     * Blocks that should drop itself
     */
    public static void addDefaultBlockDrop(String name, DeferredObject<Block> blockDrop) {
        DEFAULT_BLOCK_DROP.put(name, blockDrop);
    }

    public static Map<String, DeferredObject<? extends Item>> items() {
        return DEFAULT_ITEMS;
    }

    public static Map<String, DeferredObject<? extends Item>> wandItems() {
        return WAND_ITEMS;
    }

    public static Map<String, DeferredObject<Block>> defaultBlockModels() {
        return DEFAULT_BLOCK_MODELS;
    }

    public static Map<String, DeferredObject<Block>> defaultBlockDrops() {
        return DEFAULT_BLOCK_DROP;
    }
}
