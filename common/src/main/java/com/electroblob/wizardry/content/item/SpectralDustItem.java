package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.api.content.spell.Element;
import net.minecraft.world.item.Item;

public class SpectralDustItem extends Item implements ReceptacleItemValue{
    private final Element element;

    public SpectralDustItem(Element element) {
        super(new Properties().stacksTo(16));
        this.element = element;
    }


    @Override
    public Element getElement() {
        return element;
    }
}
