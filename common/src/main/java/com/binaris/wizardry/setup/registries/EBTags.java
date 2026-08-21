package com.binaris.wizardry.setup.registries;

import com.binaris.wizardry.WizardryMainMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;

public final class EBTags {
    public static final TagKey<Item> MAGIC_CRYSTAL_ITEM = createItemTag("magic_crystal");
    public static final TagKey<Item> MAGIC_SHARD_ITEM = createItemTag("magic_crystal_shard");
    public static final TagKey<Item> WAND_UPGRADES = createItemTag("wand_upgrade");
    public static final TagKey<Item> ARMOR_UPGRADE = createItemTag("armor_upgrade");
    public static final TagKey<Item> SPECTRAL_DUSTS = createItemTag("spectral_dust");

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

    // Banner pattern tags: each stencil item references one of these; the loom shows every
    // banner pattern contained in the referenced tag.
    public static final TagKey<BannerPattern> EARTH_PATTERN_ITEM = createBannerPatternTag("pattern_item/earth");
    public static final TagKey<BannerPattern> FIRE_PATTERN_ITEM = createBannerPatternTag("pattern_item/fire");
    public static final TagKey<BannerPattern> HEALING_PATTERN_ITEM = createBannerPatternTag("pattern_item/healing");
    public static final TagKey<BannerPattern> ICE_PATTERN_ITEM = createBannerPatternTag("pattern_item/ice");
    public static final TagKey<BannerPattern> LIGHTNING_PATTERN_ITEM = createBannerPatternTag("pattern_item/lightning");
    public static final TagKey<BannerPattern> NECROMANCY_PATTERN_ITEM = createBannerPatternTag("pattern_item/necromancy");
    public static final TagKey<BannerPattern> SORCERY_PATTERN_ITEM = createBannerPatternTag("pattern_item/sorcery");

    private EBTags() {
    }

    public static TagKey<Block> createBlockTag(String name) {
        return TagKey.create(Registries.BLOCK, WizardryMainMod.location(name));
    }

    public static TagKey<BannerPattern> createBannerPatternTag(String name) {
        return TagKey.create(Registries.BANNER_PATTERN, WizardryMainMod.location(name));
    }

    public static TagKey<Item> createItemTag(String name) {
        return TagKey.create(Registries.ITEM, WizardryMainMod.location(name));
    }

    public static TagKey<Item> createItemTag(String namespace, String name) {
        return TagKey.create(Registries.ITEM, WizardryMainMod.location(namespace, name));
    }
}
