package com.binaris.wizardry.core.integrations.accessories;

import com.binaris.wizardry.api.content.item.ArtifactItem;
import com.binaris.wizardry.core.IArtifactEffect;
import net.minecraft.world.item.Rarity;

/**
 * An artifact item that is also an accessory for the Accessories mod.
 * This class registers itself as an accessory when instantiated.
 */
public class AccessoriesArtifactItem extends ArtifactItem implements io.wispforest.accessories.api.Accessory {
    public AccessoriesArtifactItem(Rarity rarity) {
        super(rarity);
        io.wispforest.accessories.api.AccessoriesAPI.registerAccessory(this, this);
    }

    public AccessoriesArtifactItem(Rarity rarity, IArtifactEffect effect) {
        super(rarity, effect);
        io.wispforest.accessories.api.AccessoriesAPI.registerAccessory(this, this);
    }
}
