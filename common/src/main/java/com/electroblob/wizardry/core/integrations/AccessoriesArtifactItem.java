package com.electroblob.wizardry.core.integrations;

import com.electroblob.wizardry.content.item.ArtifactItem;
import net.minecraft.world.item.Rarity;

public class AccessoriesArtifactItem extends ArtifactItem implements io.wispforest.accessories.api.Accessory {
    public AccessoriesArtifactItem(Rarity rarity) {
        super(rarity);
        io.wispforest.accessories.api.AccessoriesAPI.registerAccessory(this, this);
    }
}
