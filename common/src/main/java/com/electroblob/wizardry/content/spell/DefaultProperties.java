package com.electroblob.wizardry.content.spell;

import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;

public final class DefaultProperties {
    public static final SpellProperty<Integer> COOLDOWN = SpellProperty.intProperty("cooldown", 5);
    public static final SpellProperty<Integer> COST = SpellProperty.intProperty("cost", 5);
    public static final SpellProperty<Integer> CHARGE = SpellProperty.intProperty("charge", 2);
    public static final SpellProperty<String> TIER = SpellProperty.stringProperty("tier");
    public static final SpellProperty<String> ELEMENT = SpellProperty.stringProperty("element");
    public static final SpellProperty<String> SPELL_ACTION = SpellProperty.stringProperty("spell_action");
    public static final SpellProperty<String> SPELL_TYPE = SpellProperty.stringProperty("spell_type");

    public static final SpellProperty<Integer> ENTITIES = SpellProperty.intProperty("entities");
    public static final SpellProperty<Float> DAMAGE = SpellProperty.floatProperty("damage");
    public static final SpellProperty<Integer> DURATION = SpellProperty.intProperty("duration");
    public static final SpellProperty<Float> BLAST_RADIUS = SpellProperty.floatProperty("blast_radius");
    public static final SpellProperty<Float> DIRECT_DAMAGE = SpellProperty.floatProperty("direct_damage");
    public static final SpellProperty<Float> SPLASH_DAMAGE = SpellProperty.floatProperty("splash_damage");
    public static final SpellProperty<Float> RANGE = SpellProperty.floatProperty("range", 5.0F);
    public static final SpellProperty<Integer> EFFECT_RADIUS = SpellProperty.intProperty("effect_radius");
    public static final SpellProperty<Integer> EFFECT_DURATION = SpellProperty.intProperty("effect_duration");
    public static final SpellProperty<Integer> EFFECT_STRENGTH = SpellProperty.intProperty("effect_strength");
    public static final SpellProperty<Integer> MINION_COUNT = SpellProperty.intProperty("minion_count");
    public static final SpellProperty<Integer> MINION_LIFETIME = SpellProperty.intProperty("minion_lifetime");
    public static final SpellProperty<Integer> ITEM_LIFETIME = SpellProperty.intProperty("item_lifetime");

    public static final SpellProperty<Integer> SUMMON_RADIUS = SpellProperty.intProperty("summon_radius");
    public static final SpellProperty<Float> KNOCKBACK = SpellProperty.floatProperty("knockback");
    public static final SpellProperty<Float> SPEED = SpellProperty.floatProperty("speed");
    public static final SpellProperty<Integer> SEEKING_STRENGTH = SpellProperty.intProperty("seeking_strength");
    public static final SpellProperty<Float> ACCELERATION = SpellProperty.floatProperty("acceleration");
    public static final SpellProperty<Integer> MAX_TARGETS = SpellProperty.intProperty("max_targets");
    public static final SpellProperty<Float> HEALTH = SpellProperty.floatProperty("health");

    // This is used by some spells to determine if they should be only for dev environment, only for ebw register logic
    // if you want this you should make the condition inside your register method
    public static final SpellProperty<Boolean> SENSIBLE = SpellProperty.booleanProperty("sensible", false);

    private DefaultProperties() {}
}
