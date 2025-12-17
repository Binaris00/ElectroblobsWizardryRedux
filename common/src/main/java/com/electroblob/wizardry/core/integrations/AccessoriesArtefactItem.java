package com.electroblob.wizardry.core.integrations;

import com.electroblob.wizardry.api.content.item.ArtefactItem;
import com.electroblob.wizardry.core.IArtefactEffect;
import net.minecraft.world.item.Rarity;

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
