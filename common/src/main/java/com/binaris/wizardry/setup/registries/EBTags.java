package com.binaris.wizardry.setup.registries;

import com.binaris.wizardry.WizardryMainMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class EBTags {
    public static final TagKey<Item> MAGIC_CRYSTAL_ITEM = createItemTag("magic_crystal");
    public static final TagKey<Item> GILDED_WOOD_ITEM = createItemTag("gilded_wood");
    public static final TagKey<Item> RING_ACCESSORIES = createItemTag("accessories", "ring");
    public static final TagKey<Item> CHARM_ACCESSORIES = createItemTag("accessories", "charm");
    public static final TagKey<Item> NECKLACE_ACCESSORIES = createItemTag("accessories", "necklace");
    public static final TagKey<Item> RING_CURIOS = createItemTag("curios", "ring");
    public static final TagKey<Item> CHARM_CURIOS = createItemTag("curios", "charm");
    public static final TagKey<Item> NECKLACE_CURIOS = createItemTag("curios", "necklace");
    public static final TagKey<Item> MAIN_HAND_RING_TRINKETS = createItemTag("trinkets", "hand/ring");
    public static final TagKey<Item> GLOVE_TRINKETS = createItemTag("trinkets", "hand/glove");
    public static final TagKey<Item> NECKLACE_TRINKETS = createItemTag("trinkets", "chest/necklace");

    public static final TagKey<Block> GILDED_WOOD_BLOCK = createBlockTag("gilded_wood");

    private EBTags() {
    }

    public static TagKey<Block> createBlockTag(String name) {
        return TagKey.create(Registries.BLOCK, WizardryMainMod.location(name));
    }

    public static TagKey<Item> createItemTag(String name) {
        return TagKey.create(Registries.ITEM, WizardryMainMod.location(name));
    }

    public static TagKey<Item> createItemTag(String namespace, String name) {
        return TagKey.create(Registries.ITEM, WizardryMainMod.location(namespace, name));
    }
}
