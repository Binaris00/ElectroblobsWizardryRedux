package com.binaris.wizardry.api.content.item;

import com.binaris.wizardry.api.content.spell.Element;

/**
 * Interface for items that have an associated element value. This is typically used for items that can store or
 * manipulate elemental things, like wands, armor, and other magical items.
 */
public interface IElementValue {
    /**
     * Gets the element associated with this item.
     *
     * @return The element of the item.
     */
    Element getElement();

    /**
     * Determines if this item is valid for use in a receptacle.
     *
     * @return true if the item can be used in a receptacle, false otherwise.
     */
    boolean validForReceptacle();
}
