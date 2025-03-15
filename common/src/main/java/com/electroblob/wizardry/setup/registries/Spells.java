package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.api.common.spell.NoneSpell;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.common.content.entity.projectile.*;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import com.electroblob.wizardry.common.content.spell.abstr.ArrowSpell;
import com.electroblob.wizardry.common.content.spell.abstr.BuffSpell;
import com.electroblob.wizardry.common.content.spell.abstr.ProjectileSpell;
import com.electroblob.wizardry.common.content.spell.earth.*;
import com.electroblob.wizardry.common.content.spell.fire.*;
import com.electroblob.wizardry.common.content.spell.healing.*;
import com.electroblob.wizardry.common.content.spell.ice.Freeze;
import com.electroblob.wizardry.common.content.spell.ice.FrostRay;
import com.electroblob.wizardry.common.content.spell.ice.Permafrost;
import com.electroblob.wizardry.common.content.spell.lightning.BlindingFlash;
import com.electroblob.wizardry.common.content.spell.lightning.InvokeWeather;
import com.electroblob.wizardry.common.content.spell.lightning.ZapSpell;
import com.electroblob.wizardry.common.content.spell.magic.ForceArrowSpell;
import com.electroblob.wizardry.common.content.spell.misc.ExampleSpell;
import com.electroblob.wizardry.common.content.spell.necromancy.*;
import com.electroblob.wizardry.common.content.spell.sorcery.*;
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
    public static final Spell FIRE_SKIN;
    public static final Spell STATIC_AURA;
    public static final Spell GREATER_WARD;
    public static final Spell FLAME_RAY;
    public static final Spell IGNITE;
    public static final Spell FREEZE;
    public static final Spell LIFE_DRAIN;
    public static final Spell FROST_RAY;
    public static final Spell WITHER;
    public static final Spell POISON;
    public static final Spell HEAL_ALLY;
    public static final Spell EVADE;
    public static final Spell FANGS;
    public static final Spell DRAGON_FIREBALL;
    public static final Spell FIRE_BREATH;
    public static final Spell GREATER_HEAL;
    public static final Spell POCKET_FURNACE;
    public static final Spell ENRAGE;
    public static final Spell FIRESTORM;
    public static final Spell GROUP_HEAL;
    public static final Spell BLINDING_FLASH;
    public static final Spell DETONATE;
    public static final Spell INVOKE_WEATHER;
    public static final Spell OAKFLESH;
    public static final Spell PERMAFROST;
    public static final Spell GROWN_AURA;
    public static final Spell LEVITATION;
    public static final Spell WHIRLWIND;
    public static final Spell POCKET_WORKBENCH;
    public static final Spell SHULKER_BULLET;
    public static final Spell VANISHING_BOX;
    public static final Spell REPLENISH_HUNGER;
    public static final Spell DARKNESS_ORB;
    public static final Spell FLIGHT;
    public static final Spell BANISH;
    public static final Spell PHASE_STEP;
    public static final Spell METEOR;
    public static final Spell ARROW_RAIN;
    public static final Spell REVERSAL;
    public static final Spell PLAGUE_DARKNESS;
    public static final Spell FORCE_ORB;
    public static final Spell FOREST_CURSE;

    static {
        Register.init();

        NONE = spell("none", NoneSpell::new);
        EXAMPLE = spell("example", ExampleSpell::new);
        ZAP = spell("zap", ZapSpell::new);

        MAGIC_MISSILE = spell("magic_missile", () -> new ArrowSpell<>(MagicMissile::new).assignProperties(
                SpellProperties.builder()
                        .add(DefaultProperties.RANGE, 18f)
                        .add(DefaultProperties.DAMAGE, 3f)
                        .build()
        ));

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


        // TODO BIN SOUND .soundValues(0.5f, 0.4f, 0.2f)
        FORCE_ORB = spell("force_orb", () -> new ProjectileSpell<>(EntityForceOrb::new));

        // TODO BIN sound .soundValues(0.5f, 0.4f, 0.2f)
        DARKNESS_ORB = spell("darkness_orb", () -> new ProjectileSpell<>(DarknessOrb::new));

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

        FIRE_SKIN = spell("fire_skin", () -> new BuffSpell(1, 0.3f, 0, EBMobEffects.FIRE_SKIN::get).assignProperties(
                SpellProperties.builder()
                        .build()
        ));

        STATIC_AURA = spell("static_aura", () -> new BuffSpell(0, 0.5f, 0.7f, EBMobEffects.STATIC_AURA::get).assignProperties(
                SpellProperties.builder()
                        .build()
        ));

        GREATER_WARD = spell("greater_ward", () -> new BuffSpell(0.75f, 0.6f, 0.8f, EBMobEffects.WARD).assignProperties(
                SpellProperties.builder()
                        .build()
        ));

        HEAL = spell("heal", Heal::new);

        SATIETY = spell("satiety", Satiety::new);

        REMOVE_CURSE = spell("remove_curse", RemoveCurse::new);

        CURE_EFFECTS = spell("cure_effects", CureEffects::new);

        IGNITE = spell("ignite", Ignite::new);

        FLAME_RAY = spell("flame_ray", FlameRay::new);

        FREEZE = spell("freeze", Freeze::new);

        LIFE_DRAIN = spell("life_drain", LifeDrain::new);

        FROST_RAY = spell("frost_ray", FrostRay::new);

        WITHER = spell("wither", Wither::new);

        POISON = spell("poison", Poison::new);

        HEAL_ALLY = spell("heal_ally", HealAlly::new);

        EVADE = spell("evade", Evade::new);

        FANGS = spell("fangs", Fangs::new);

        DRAGON_FIREBALL = spell("dragon_fireball", DragonFireball::new);

        FIRE_BREATH = spell("fire_breath", FireBreath::new);

        GREATER_HEAL = spell("greater_heal", GreaterHeal::new);

        POCKET_FURNACE  = spell("pocket_furnace", PocketFurnace::new);

        ENRAGE = spell("enrage", Enrage::new);

        FIRESTORM = spell("firestorm", Firestorm::new);

        GROUP_HEAL = spell("group_heal", GroupHeal::new);

        BLINDING_FLASH = spell("blind_flash", BlindingFlash::new);

        DETONATE = spell("detonate", Detonate::new);

        INVOKE_WEATHER = spell("invoke_weather", InvokeWeather::new);

        // TODO BIN: Missing sounds...
        // .soundValues(0.7f, 1.2f, 0.4f)
        OAKFLESH = spell("oakflesh", () -> new BuffSpell(0.6f, 0.5f, 0.4f, EBMobEffects.OAKFLESH));

        PERMAFROST = spell("permafrost", Permafrost::new);

        GROWN_AURA = spell("grown_aura", GrownAura::new);

        LEVITATION = spell("levitation", Levitation::new);

        WHIRLWIND = spell("whirlwind", Whirlwind::new);

        REPLENISH_HUNGER = spell("replenish_hunger", ReplenishHunger::new);

        VANISHING_BOX = spell("vanishing_box", VanishingBox::new);

        SHULKER_BULLET = spell("shulker_bullet", ShulkerBullet::new);

        POCKET_WORKBENCH = spell("pocket_workbench", PocketWorkbench::new);

        FLIGHT = spell("flight", Flight::new);

        BANISH = spell("banish", Banish::new);

        PHASE_STEP = spell("phase_step", PhaseStep::new);

        METEOR = spell("meteor", Meteor::new);

        ARROW_RAIN = spell("arrow_rain", ArrowRain::new);

        REVERSAL = spell("reversal", Reversal::new);

        PLAGUE_DARKNESS = spell("plague_of_darkness", PlagueOfDarkness::new);

        FOREST_CURSE = spell("forest_curse", ForestsCurse::new);
    }

    static void handleRegistration(Consumer<Set<Map.Entry<String, Spell>>> handler) {
        handler.accept(Collections.unmodifiableSet(Register.SPELLS.entrySet()));
    }

    public static class Register {
        public static Map<String, Spell> SPELLS = new HashMap<>();
        
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