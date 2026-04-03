package com.binaris.wizardry.api.content.spell;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.core.EBLogger;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public enum SpellType {
    ATTACK("attack"),
    DEFENCE("defence"),
    UTILITY("utility"),
    MINION("minion"),
    BUFF("buff"),
    CONSTRUCT("construct"),
    PROJECTILE("projectile"),
    ALTERATION("alteration");

    private final String modid;
    private final String name;

    SpellType(String name) {
        this.modid = WizardryMainMod.MOD_ID;
        this.name = name;
    }

    SpellType(String modid, String name) {
        this.modid = modid;
        this.name = name;
    }

    public static SpellType fromName(String name) {
        for (SpellType type : values()) {
            if (type.name.equals(name) || type.name.equals(name.toLowerCase())) return type;
        }

        EBLogger.error("No such spell type with unlocalized name: '%s', maybe some spell is bad registered?!".formatted(name));
        return SpellType.UTILITY; //default
    }

    public static @Nullable SpellType fromLocation(ResourceLocation location) {
        for (SpellType type : values()) {
            if (type.getLocation().equals(location)) return type;
        }
        return null;
    }

    public ResourceLocation getLocation() {
        return new ResourceLocation(modid, name);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return "spelltype." + name;
    }
}
