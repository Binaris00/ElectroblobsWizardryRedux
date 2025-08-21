package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class EBTags {
    private EBTags(){}

    public static final TagKey<Item> MAGIC_CRYSTAL_ITEM = createItemTag("magic_crystal");
    public static final TagKey<Item> GILDED_WOOD_ITEM = createItemTag("gilded_wood");
    public static final TagKey<Block> GILDED_WOOD_BLOCK = createBlockTag("gilded_wood");

    public static TagKey<Block> createBlockTag(String name){
        return TagKey.create(Registries.BLOCK, WizardryMainMod.location(name));
    }

    public static TagKey<Item> createItemTag(String name){
        return TagKey.create(Registries.ITEM, WizardryMainMod.location(name));
    }
}
