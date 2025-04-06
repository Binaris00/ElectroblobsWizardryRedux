package com.electroblob.wizardry.api.content.spell.internal;

public class SpellModifiers {
    public static final String POTENCY = "potency";
    public static final String COST = "cost";
    public static final String CHARGEUP = "chargeup";
    public static final String PROGRESSION = "progression";

    public float get(String key){
        return 1;
    }

    public float getNone(String key){
        return 0;
    }
}
