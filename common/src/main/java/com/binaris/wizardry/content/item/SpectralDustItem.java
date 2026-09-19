package com.binaris.wizardry.content.item;

import com.binaris.wizardry.api.content.item.IElementValue;
import com.binaris.wizardry.api.content.spell.Element;
import net.minecraft.world.item.Item;

public class SpectralDustItem extends Item implements IElementValue {
    private final Element element;

    public SpectralDustItem(Element element) {
        super(new Properties().stacksTo(16));
        this.element = element;
    }

    @Override
    public Element getElement() {
        return element;
    }

    /// @deprecated Receptacle placement is now driven by imbuement altar recipes; see
    ///             {@link IElementValue#validForReceptacle()}.
    @Override
    @Deprecated(forRemoval = true)
    public boolean validForReceptacle() {
        return true;
    }
}
