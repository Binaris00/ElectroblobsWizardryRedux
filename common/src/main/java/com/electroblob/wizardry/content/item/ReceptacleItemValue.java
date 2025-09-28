package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.api.content.spell.Element;

/**
 * Implemented by items which can be placed in a {@link com.electroblob.wizardry.content.block.ReceptacleBlock} to determine
 * its element.
 */
public interface ReceptacleItemValue {
    Element getElement();
}
