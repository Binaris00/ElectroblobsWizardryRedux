package com.binaris.wizardry.api.content.spell;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.SpellTypeRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;

public enum SpellTypes implements SpellType {
    ATTACK("attack"),
    DEFENCE("defence"),
    UTILITY("utility"),
    MINION("minion"),
    BUFF("buff"),
    CONSTRUCT("construct"),
    PROJECTILE("projectile"),
    ALTERATION("alteration");

    private final String name;

    SpellTypes(String name) {
        this.name = name;
    }

    @Override
    public ResourceLocation getLocation() {
        // In addons you could use a different modid or just call SpellTypeRegistry.getLocation(this)
        return WizardryMainMod.location(name);
    }

    static {
        Arrays.stream(values()).forEachOrdered(type -> SpellTypeRegistry.register(type.getLocation(), type));
    }
}