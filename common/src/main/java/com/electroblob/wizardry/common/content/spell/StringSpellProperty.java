package com.electroblob.wizardry.common.content.spell;

import com.electroblob.wizardry.api.common.spell.SpellProperty;
import com.electroblob.wizardry.api.common.util.Util;
import net.minecraft.nbt.CompoundTag;

/**
 * This is a string property. It exists purely for example purposes to show how to add your
 * own custom spell property
 */
public class StringSpellProperty extends SpellProperty<String> {

    public static SpellProperty<String> stringProperty(String identifier) {
        return stringProperty(identifier, "");
    }

    public static SpellProperty<String> stringProperty(String identifier, String defaultValue) {
        StringSpellProperty property = new StringSpellProperty();
        property.initProperty("", identifier, defaultValue, null);
        return property;
    }

//    @Override
//    public CompoundTag serializeOn(CompoundTag tag) {
//        tag.put("string_", Util.compoundTagFrom(t -> t.putString(this.identifier, this.value)));
//        return tag;
//    }

    private StringSpellProperty() {}

}
