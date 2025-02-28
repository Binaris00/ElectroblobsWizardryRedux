package com.electroblob.wizardry.common.content.spell;

import com.electroblob.wizardry.api.common.spell.SpellProperty;

public final class DefaultProperties {

    public static final SpellProperty<Float> DAMAGE = SpellProperty.floatProperty("damage");
    public static final SpellProperty<Float> DIRECT_DAMAGE = SpellProperty.floatProperty("direct_damage");
    public static final SpellProperty<Float> SPLASH_DAMAGE = SpellProperty.floatProperty("splash_damage");
    public static final SpellProperty<Float> RANGE = SpellProperty.floatProperty("range", 5.0F);
    public static final SpellProperty<Integer> COOLDOWN = SpellProperty.intProperty("cooldown");
    public static final SpellProperty<Integer> COST = SpellProperty.intProperty("cost");
    public static final SpellProperty<Integer> CHARGEUP = SpellProperty.intProperty("chargeup");
    public static final SpellProperty<Integer> EFFECT_RADIUS = SpellProperty.intProperty("effect_radius");
    public static final SpellProperty<Integer> EFFECT_DURATION = SpellProperty.intProperty("effect_duration");
    public static final SpellProperty<Integer> EFFECT_STRENGTH = SpellProperty.intProperty("effect_strength");
    public static final SpellProperty<Float> KNOCKBACK = SpellProperty.floatProperty("knockback");
    public static final SpellProperty<Float> SPEED = SpellProperty.floatProperty("speed");
    public static final SpellProperty<Integer> SEEKING_STRENGTH = SpellProperty.intProperty("seeking_strength");

    private DefaultProperties() {}
}
