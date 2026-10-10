package com.binaris.wizardry.client.compat.tag;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.stream.Collectors;

public record ItemTag(TagKey<Item> tag) {

    public static ItemTag of(TagKey<Item> tag) {
        return new ItemTag(tag);
    }

    public static List<ItemTag> of(List<TagKey<Item>> itemTags) {
        return itemTags.stream().map(ItemTag::new).collect(Collectors.toList());
    }
}
