package com.electroblob.wizardry.content.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ArtifactItem extends Item {
    public ArtifactItem(Rarity rarity) {
        super(new Item.Properties().stacksTo(1).rarity(rarity));
    }
}
