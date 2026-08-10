package com.binaris.wizardry.api.content.spell;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public interface SpellType {
    ResourceLocation getLocation();

    default String getDisplayName() {
        return Util.makeDescriptionId("spelltype", getLocation());
    }
}