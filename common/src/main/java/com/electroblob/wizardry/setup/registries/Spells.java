package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.api.common.spell.NoneSpell;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.common.content.entity.projectile.*;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import com.electroblob.wizardry.common.content.spell.abstr.ArrowSpell;
import com.electroblob.wizardry.common.content.spell.abstr.BuffSpell;
import com.electroblob.wizardry.common.content.spell.abstr.ProjectileSpell;
import com.electroblob.wizardry.common.content.spell.earth.Leap;
import com.electroblob.wizardry.common.content.spell.earth.Poison;
import com.electroblob.wizardry.common.content.spell.fire.FlameRay;
import com.electroblob.wizardry.common.content.spell.fire.Ignite;
import com.electroblob.wizardry.common.content.spell.healing.*;
import com.electroblob.wizardry.common.content.spell.ice.Freeze;
import com.electroblob.wizardry.common.content.spell.ice.FrostRay;
import com.electroblob.wizardry.common.content.spell.lightning.ZapSpell;
import com.electroblob.wizardry.common.content.spell.magic.ForceArrowSpell;
import com.electroblob.wizardry.common.content.spell.misc.ExampleSpell;
import com.electroblob.wizardry.common.content.spell.necromancy.LifeDrain;
import com.electroblob.wizardry.common.content.spell.necromancy.Wither;
import com.electroblob.wizardry.common.content.spell.necromancy.WitherSkullSpell;
import net.minecraft.world.effect.MobEffects;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.electroblob.wizardry.setup.registries.Spells.Register.*;

public final class Spells {

    public static final Spell NONE;
    public static final Spell EXAMPLE;
    public static final Spell ZAP;
    public static final Spell MAGIC_MISSILE;
    public static final Spell SMOKE_BOMB;
    public static final Spell POISON_BOMB;
    public static final Spell FIRE_BOMB;
    public static final Spell THUNDERBOLT;
    public static final Spell DART;
    public static final Spell LEAP;
    public static final Spell FORCE_ARROW;
    public static final Spell WITHER_SKULL;
    public static final Spell ICE_LANCE;
    public static final Spell FIREBOLT;
    public static final Spell ICE_BALL;
    public static final Spell ICE_SHARD;
    public static final Spell ICE_CHARGE;
    public static final Spell MAGIC_FIREBALL;
    public static final Spell HOMING_SPARK;
    public static final Spell LIGHTNING_ARROW;
    public static final Spell FIRE_RESISTANCE;
    public static final Spell NIGHT_VISION;
    public static final Spell FONT_OF_VITALITY;
    public static final Spell INVISIBILITY;
    public static final Spell WATER_BREATHING;
    public static final Spell HEAL;
    public static final Spell SATIETY;
    public static final Spell REMOVE_CURSE;
    public static final Spell CURE_EFFECTS;
    public static final Spell AGILITY;
//    public static final Spell FIRE_SKIN;
//    public static final Spell STATIC_AURA;
//    public static final Spell GREATER_WARD;
    public static final Spell FLAME_RAY;
    public static final Spell IGNITE;
    public static final Spell FREEZE;
    public static final Spell LIFE_DRAIN;
    public static final Spell FROST_RAY;
    public static final Spell WITHER;
    public static final Spell POISON;
    public static final Spell HEAL_ALLY;

    static {
        Register.init();

        NONE = spell("none", NoneSpell::new);
        EXAMPLE = spell("example", ExampleSpell::new);
        ZAP = spell("zap", ZapSpell::new);

        // range: 18
        // damage: 3
        MAGIC_MISSILE = spell("magic_missile", () -> new ArrowSpell<>(MagicMissile::new).assignProperties(
                SpellProperties.builder()
                        .add(DefaultProperties.RANGE, 18f)
                        .add(DefaultProperties.DAMAGE, 3f)
                        .build()
        ));

        // range: 10
        // effect radius: 3
        // effect duration: 120
        SMOKE_BOMB = spell("smoke_bomb", () -> new ProjectileSpell<>(SmokeBomb::new).assignProperties(
                SpellProperties.builder()
                        .add(DefaultProperties.RANGE, 10f)
                        .add(DefaultProperties.EFFECT_RADIUS, 2)
                        .add(DefaultProperties.EFFECT_DURATION, 120)
                        .build()
        ));

        POISON_BOMB = spell("poison_bomb", () -> new ProjectileSpell<>(PoisonBomb::new).assignProperties(
                SpellProperties.builder()
                        .add(DefaultProperties.RANGE, 10f)
                        .add(DefaultProperties.DAMAGE, 1f)
                        .add(DefaultProperties.EFFECT_STRENGTH, 1)
                        .add(DefaultProperties.EFFECT_DURATION, 200)
                        .build()
        ));

        FIRE_BOMB = spell("fire_bomb", () -> new ProjectileSpell<>(FireBomb::new).assignProperties(
                SpellProperties.builder()
                        .add(DefaultProperties.RANGE, 10f)
                        .add(DefaultProperties.DIRECT_DAMAGE, 5f)
                        .add(DefaultProperties.SPLASH_DAMAGE, 3f)
                        .add(DefaultProperties.EFFECT_RADIUS, 3)
                        .add(DefaultProperties.EFFECT_DURATION, 7)
                        .build()
        ));

        THUNDERBOLT = spell("thunderbolt", () -> new ProjectileSpell<>(Thunderbolt::new).assignProperties(
                SpellProperties.builder()
                        .add(DefaultProperties.RANGE, 12f)
                        .add(DefaultProperties.DAMAGE, 3f)
                        .add(DefaultProperties.KNOCKBACK, 0.2f)
                        .build()
        ));

        DART = spell("dart", () -> new ArrowSpell<>(Dart::new).assignProperties(
                SpellProperties.builder()
                        .add(DefaultProperties.RANGE, 18f)
                        .add(DefaultProperties.COOLDOWN, 10)
                        .add(DefaultProperties.COST, 5)
                        .build()
        ));

        // Horizontal speed: 0.3
        // Vertical speed: 0.65
        LEAP = spell("leap", Leap::new);

        FORCE_ARROW = spell("force_arrow", () -> new ForceArrowSpell().assignProperties(
                SpellProperties.builder()
                        .add(DefaultProperties.RANGE, 15f)
                        .add(DefaultProperties.DAMAGE, 5f)
                        .build()
        ));

        WITHER_SKULL = spell("wither_skull", WitherSkullSpell::new);

        ICE_LANCE = spell("ice_lance", () -> new ArrowSpell<>(IceLance::new).assignProperties(
                SpellProperties.builder()
                        .add(DefaultProperties.RANGE, 15f)
                        .add(DefaultProperties.DAMAGE, 15f)
                        .add(DefaultProperties.EFFECT_DURATION, 300)
                        .add(DefaultProperties.EFFECT_STRENGTH, 0)
                        .build()
        ));

        FIREBOLT = spell("firebolt", () -> new ProjectileSpell<>(FireBolt::new).assignProperties(
                SpellProperties.builder()
                        .add(DefaultProperties.RANGE, 15f)
                        .add(DefaultProperties.DAMAGE, 5f)
                        .add(DefaultProperties.EFFECT_DURATION, 5)
                        .build()
        ));

        ICE_BALL = spell("ice_ball", () -> new ProjectileSpell<>(IceBall::new).assignProperties(
                SpellProperties.builder()
                        .add(DefaultProperties.RANGE, 20f)
                        .add(DefaultProperties.DAMAGE, 5f)
                        .add(DefaultProperties.SPLASH_DAMAGE, 3f)
                        .add(DefaultProperties.EFFECT_DURATION, 100)
                        .add(DefaultProperties.EFFECT_STRENGTH, 0)
                        .build()
        ));

        ICE_SHARD = spell("ice_shard", () -> new ArrowSpell<>(IceShard::new).assignProperties(
                SpellProperties.builder()
                        .add(DefaultProperties.RANGE, 15f)
                        .add(DefaultProperties.DAMAGE, 6f)
                        .add(DefaultProperties.SPLASH_DAMAGE, 3f)
                        .add(DefaultProperties.EFFECT_DURATION, 100)
                        .add(DefaultProperties.EFFECT_STRENGTH, 0)
                        .build()
        ));

        ICE_CHARGE = spell("ice_charge", () -> new ProjectileSpell<>(IceCharge::new).assignProperties(
                SpellProperties.builder()
                        .add(DefaultProperties.RANGE, 20f)
                        .add(DefaultProperties.DAMAGE, 4f)
                        .add(DefaultProperties.EFFECT_DURATION, 120)
                        .add(DefaultProperties.EFFECT_STRENGTH, 1)
                        .build()
        ));

        MAGIC_FIREBALL = spell("magic_fireball", () -> new ProjectileSpell<>(MagicFireball::new).assignProperties(
                SpellProperties.builder()
                        .add(DefaultProperties.RANGE, 20f)
                        .add(DefaultProperties.DAMAGE, 5f)
                        .add(DefaultProperties.EFFECT_DURATION, 5)
                        .build()
        ));

        HOMING_SPARK = spell("homing_spark", () -> new ProjectileSpell<>(Spark::new).assignProperties(
                SpellProperties.builder()
                        .add(DefaultProperties.RANGE, 25f)
                        .add(DefaultProperties.DAMAGE, 6f)
                        .add(DefaultProperties.SEEKING_STRENGTH, 5)
                        .build()
        ));

        LIGHTNING_ARROW = spell("lightning_arrow", () -> new ArrowSpell<>(LightningArrow::new).assignProperties(
                SpellProperties.builder()
                        .add(DefaultProperties.RANGE, 25f)
                        .add(DefaultProperties.DAMAGE, 7f)
                        .build()
        ));

        FIRE_RESISTANCE = spell("fire_resistance", () -> new BuffSpell(1, 0.5f, 0, () -> MobEffects.FIRE_RESISTANCE).assignProperties(
                SpellProperties.builder()
                        .build()
        ));

        NIGHT_VISION = spell("night_vision", () -> new BuffSpell(0, 0.4f, 0.7f, () -> MobEffects.NIGHT_VISION).assignProperties(
                SpellProperties.builder()
                        .build()
        ));

        FONT_OF_VITALITY = spell("font_of_vitality", () -> new BuffSpell(1, 0.8f, 0.3f, () -> MobEffects.ABSORPTION, () -> MobEffects.REGENERATION).assignProperties(
                SpellProperties.builder()
                        .build()
        ));

        INVISIBILITY = spell("invisibility", () -> new BuffSpell(0, 0.5f, 0.5f, () -> MobEffects.INVISIBILITY).assignProperties(
                SpellProperties.builder()
                        .build()
        ));
        WATER_BREATHING = spell("water_breathing", () -> new BuffSpell(0.3f, 0.3f, 1, () -> MobEffects.WATER_BREATHING).assignProperties(
                SpellProperties.builder()
                        .build()
        ));

        AGILITY = spell("agility", () -> new BuffSpell( 0.4f, 1.0f, 0.8f, () -> MobEffects.MOVEMENT_SPEED).assignProperties(
                SpellProperties.builder()
                        .build()
        ));

//        FIRE_SKIN = spell("fire_skin", () -> new BuffSpell(1, 0.3f, 0, EBMobEffects.FIRE_SKIN::get).assignProperties(
//                SpellProperties.builder()
//                        .build()
//        ));
//
//        STATIC_AURA = spell("static_aura", () -> new BuffSpell(0, 0.5f, 0.7f, EBMobEffects.STATIC_AURA::get).assignProperties(
//                SpellProperties.builder()
//                        .build()
//        ));
//
//        GREATER_WARD = spell("greater_ward", () -> new BuffSpell(0.75f, 0.6f, 0.8f, EBMobEffects.WARD).assignProperties(
//                SpellProperties.builder()
//                        .build()
//        ));

        HEAL = spell("heal", Heal::new);

        SATIETY = spell("satiety", Satiety::new);

        REMOVE_CURSE = spell("remove_curse", RemoveCurse::new);

        CURE_EFFECTS = spell("cure_effects", CureEffects::new);

        // range: 10
        // burn duration: 10
        IGNITE = spell("ignite", Ignite::new);

        // range: 10
        // damage: 3
        // Burn duration: 10
        FLAME_RAY = spell("flame_ray", FlameRay::new);

        // range: 10
        // damage: 3
        // effect duration: 200
        // effect strength: 1
        FREEZE = spell("freeze", Freeze::new);

        // range: 10
        // damage: 2
        // heal factor: 0.35F
        LIFE_DRAIN = spell("life_drain", LifeDrain::new);

        FROST_RAY = spell("frost_ray", FrostRay::new);

        WITHER = spell("wither", Wither::new);

        POISON = spell("poison", Poison::new);

        HEAL_ALLY = spell("heal_ally", HealAlly::new);
    }

    static void handleRegistration(Consumer<Set<Map.Entry<String, Spell>>> handler) {
        handler.accept(Collections.unmodifiableSet(Register.SPELLS.entrySet()));
    }

    static class Register {

        static Map<String, Spell> SPELLS = new HashMap<>();
        
        static Spell spell(String name, Supplier<Spell> spell) {
            var instantiatedSpell = spell.get();
            SPELLS.put(name, instantiatedSpell);
            return instantiatedSpell;
        }
        
        private static void init() {}
    }

    static void load() {}

    private Spells() {};

}