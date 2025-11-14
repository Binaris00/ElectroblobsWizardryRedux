package com.electroblob.wizardry.api.content.spell;

import com.electroblob.wizardry.api.EBLogger;

public enum SpellType {
    ATTACK("attack"),
    DEFENCE("defence"),
    UTILITY("utility"),
    MINION("minion"),
    BUFF("buff"),
    CONSTRUCT("construct"),
    PROJECTILE("projectile"),
    ALTERATION("alteration");

    private final String unlocalisedName;

    SpellType(String name) {
        this.unlocalisedName = name;
    }

    public static SpellType fromName(String name) {
        for (SpellType type : values()) {
            if (type.unlocalisedName.equals(name) || type.unlocalisedName.equals(name.toLowerCase())) return type;
        }

        EBLogger.error("No such spell type with unlocalized name: '%s', maybe some spell is bad registered?!".formatted(name));
        return SpellType.UTILITY; //default
    }

    public String getUnlocalisedName() {
        return unlocalisedName;
    }

    public String getDisplayName() {
        return "spelltype." + unlocalisedName;
    }
}
