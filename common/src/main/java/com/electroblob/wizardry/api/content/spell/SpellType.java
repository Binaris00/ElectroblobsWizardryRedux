package com.electroblob.wizardry.api.content.spell;

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

    SpellType(String name){
        this.unlocalisedName = name;
    }

    public static SpellType fromName(String name){

        for(SpellType type : values()){
            if(type.unlocalisedName.equals(name) || type.unlocalisedName.equals(name.toLowerCase())) return type;
        }

        throw new IllegalArgumentException("No such spell type with unlocalised name: " + name);
    }

    public String getUnlocalisedName(){
        return unlocalisedName;
    }

    public String getDisplayName(){
        return "spelltype." + unlocalisedName;
    }
}
