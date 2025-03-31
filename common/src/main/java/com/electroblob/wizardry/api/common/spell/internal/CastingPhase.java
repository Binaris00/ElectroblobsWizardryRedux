package com.electroblob.wizardry.api.common.spell.internal;

public enum CastingPhase {
    NONE("none"),
    PREPARE("prepare"),
    PERFORM("perform"),
    CONCLUDE("conclude");
    private final String name;

    CastingPhase(String pName){
        this.name = pName;
    }
    public String getName(){
        return this.name;
    }

    public boolean isCasting() {
        return !this.equals(NONE);
    }

    public static CastingPhase fromName(String name){
        if(name.equalsIgnoreCase("prepare")) {
            return PREPARE;
        }else if(name.equalsIgnoreCase("perform")) {
            return PERFORM;
        }else if(name.equalsIgnoreCase("conclude")) {
            return CONCLUDE;
        }else {
            return NONE;
        }
    }

}
