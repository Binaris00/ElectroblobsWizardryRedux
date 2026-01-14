package com.binaris.wizardry.core.integrations.accessories;

import com.binaris.wizardry.api.content.item.ArtefactItem;
import com.binaris.wizardry.core.IArtefactEffect;
import net.minecraft.world.item.Rarity;

/**
 * An artefact item that is also an accessory for the Accessories mod.
 * This class registers itself as an accessory when instantiated.
 */
public class AccessoriesArtefactItem extends ArtefactItem implements io.wispforest.accessories.api.Accessory {
    public AccessoriesArtefactItem(Rarity rarity) {
        super(rarity);
        io.wispforest.accessories.api.AccessoriesAPI.registerAccessory(this, this);
    }

    public AccessoriesArtefactItem(Rarity rarity, IArtefactEffect effect) {
        super(rarity, effect);
        io.wispforest.accessories.api.AccessoriesAPI.registerAccessory(this, this);
    }
}
