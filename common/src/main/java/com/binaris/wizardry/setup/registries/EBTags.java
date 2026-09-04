package com.binaris.wizardry.setup.registries;

import com.binaris.wizardry.WizardryMainMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class EBTags {
    public static final TagKey<Item> MAGIC_CRYSTAL_ITEM = createItemTag("magic_crystal");
    public static final TagKey<Item> MAGIC_SHARD_ITEM = createItemTag("magic_crystal_shard");
    public static final TagKey<Item> NORMAL_MAGIC_CRYSTAL = createItemTag("normal_magic_crystal");
    public static final TagKey<Item> GRAND_MAGIC_CRYSTAL = createItemTag("grand_magic_crystal");
    public static final TagKey<Item> WAND_UPGRADES = createItemTag("wand_upgrade");
    public static final TagKey<Item> ARMOR_UPGRADE = createItemTag("armor_upgrade");
    public static final TagKey<Item> SPECTRAL_DUSTS = createItemTag("spectral_dust");
    public static final TagKey<Item> ARCANE_TOMES = createItemTag("arcane_tomes");
    public static final TagKey<Item> UPGRADE = createItemTag("upgrade");
    public static final TagKey<Item> SPECIAL_UPGRADES = createItemTag("special_upgrades");
    public static final TagKey<Item> WORKBENCH_ITEM = createItemTag("workbench_item");
    public static final TagKey<Item> MANA_ITEM = createItemTag("mana_item");
    public static final TagKey<Item> WAND = createItemTag("wand");
    public static final TagKey<Item> ARMOR = createItemTag("armor");
    public static final TagKey<Item> WIZARD_ARMOR = createItemTag("wizard_armor");
    public static final TagKey<Item> SAGE_ARMOR = createItemTag("sage_armor");
    public static final TagKey<Item> WARLOCK_ARMOR = createItemTag("warlock_armor");
    public static final TagKey<Item> BATTLEMAGE_ARMOR = createItemTag("battlemage_armor");
    public static final TagKey<Item> MANA_FLASK = createItemTag("mana_flask");

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
