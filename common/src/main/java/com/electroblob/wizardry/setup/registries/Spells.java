package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.spell.NoneSpell;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import com.electroblob.wizardry.content.entity.construct.*;
import com.electroblob.wizardry.content.entity.projectile.*;
import com.electroblob.wizardry.content.spell.BlockWithSurprise;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.*;
import com.electroblob.wizardry.content.spell.earth.*;
import com.electroblob.wizardry.content.spell.fire.*;
import com.electroblob.wizardry.content.spell.healing.*;
import com.electroblob.wizardry.content.spell.ice.*;
import com.electroblob.wizardry.content.spell.lightning.BlindingFlash;
import com.electroblob.wizardry.content.spell.lightning.Charge;
import com.electroblob.wizardry.content.spell.lightning.InvokeWeather;
import com.electroblob.wizardry.content.spell.lightning.LightningPulse;
import com.electroblob.wizardry.content.spell.magic.ForceArrowSpell;
import com.electroblob.wizardry.content.spell.necromancy.*;
import com.electroblob.wizardry.content.spell.sorcery.*;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.WitherSkeleton;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public final class Spells {
    public static Map<String, Spell> SPELLS = new HashMap<>();
    private Spells() {}

    public static final Spell NONE;
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
    public static final Spell GROWTH_AURA;
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
    public static final Spell BLIZZARD;
    public static final Spell CURSE_OF_ENFEEBLEMENT;
    public static final Spell CURSE_OF_UNDEATH;
    public static final Spell FIRE_SIGIL;
    public static final Spell FROST_SIGIL;
    public static final Spell LIGHTNING_SIGIL;
    public static final Spell ICE_SPICKES;
    public static final Spell INVIGORATING_PRESENCE;
    public static final Spell RING_OF_FIRE;
    public static final Spell HEALING_AURA;
    //public static final Spell FROST_BARRIER;
    public static final Spell COMBUSTION_RUNE;
    public static final Spell BUBBLE;
    public static final Spell ENTRAPMENT;
    public static final Spell HAILSTORM;
    public static final Spell DECAY;
    public static final Spell FLAME_WEAPON;
    public static final Spell CHARGE;
    public static final Spell CURSE_OF_SOULBINDING;
    public static final Spell BLOCK_SURPRISE;
    public static final Spell GREATER_TELEKINESIS;
    public static final Spell SUMMON_RICH;
    public static final Spell LIGHTNING_PULSE;

    static {
        NONE = spell("none", NoneSpell::new);

        MAGIC_MISSILE = spell("magic_missile", () -> new ArrowSpell<>(MagicMissileEntity::new).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.NOVICE, Elements.MAGIC, SpellType.PROJECTILE, SpellAction.POINT, 5, 0, 5)
                        .add(DefaultProperties.RANGE, 18f)
                        .add(DefaultProperties.DAMAGE, 3f)
                        .build()
        ));

        SMOKE_BOMB = spell("smoke_bomb", () -> new ProjectileSpell<>(SmokeBombEntity::new).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.NOVICE, Elements.FIRE, SpellType.PROJECTILE, SpellAction.POINT, 10, 0, 20)
                        .add(DefaultProperties.RANGE, 10f)
                        .add(DefaultProperties.EFFECT_RADIUS, 2)
                        .add(DefaultProperties.EFFECT_DURATION, 120)
                        .build()
        ));

        POISON_BOMB = spell("poison_bomb", () -> new ProjectileSpell<>(PoisonBombEntity::new).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.APPRENTICE, Elements.EARTH, SpellType.PROJECTILE, SpellAction.POINT, 15, 0, 25)
                        .add(DefaultProperties.RANGE, 10f)
                        .add(DefaultProperties.DAMAGE, 5f)
                        .add(DefaultProperties.EFFECT_RADIUS, 3)
                        .add(DefaultProperties.EFFECT_STRENGTH, 1)
                        .add(DefaultProperties.EFFECT_DURATION, 150)
                        .build()
        ));


        FIRE_BOMB = spell("fire_bomb", () -> new ProjectileSpell<>(FireBombEntity::new).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.APPRENTICE, Elements.FIRE, SpellType.PROJECTILE, SpellAction.POINT, 15, 0, 25)
                        .add(DefaultProperties.RANGE, 10f)
                        .add(DefaultProperties.DIRECT_DAMAGE, 5f)
                        .add(DefaultProperties.SPLASH_DAMAGE, 3f)
                        .add(DefaultProperties.EFFECT_RADIUS, 3)
                        .add(DefaultProperties.EFFECT_DURATION, 7)
                        .build()
        ));

        THUNDERBOLT = spell("thunderbolt", () -> new ProjectileSpell<>(ThunderboltEntity::new).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.NOVICE, Elements.LIGHTNING, SpellType.PROJECTILE, SpellAction.POINT, 10, 0, 15)
                        .add(DefaultProperties.RANGE, 12f)
                        .add(DefaultProperties.DAMAGE, 3f)
                        .add(DefaultProperties.KNOCKBACK, 0.2f)
                        .build()
        ));


        DART = spell("dart", () -> new ArrowSpell<>(DartEntity::new).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.NOVICE, Elements.EARTH, SpellType.PROJECTILE, SpellAction.POINT, 5, 0, 10)
                        .add(DefaultProperties.RANGE, 15f)
                        .add(DefaultProperties.DAMAGE, 4F)
                        .add(DefaultProperties.EFFECT_DURATION, 200)
                        .add(DefaultProperties.EFFECT_STRENGTH, 1)
                        .build()
        ));

        LEAP = spell("leap", Leap::new);

        FORCE_ARROW = spell("force_arrow", ForceArrowSpell::new);

        WITHER_SKULL = spell("wither_skull", WitherSkullSpell::new);

        ICE_LANCE = spell("ice_lance", () -> new ArrowSpell<>(IceLanceEntity::new).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.ADVANCED, Elements.ICE, SpellType.PROJECTILE, SpellAction.POINT, 20, 0, 20)
                        .add(DefaultProperties.RANGE, 15f)
                        .add(DefaultProperties.DAMAGE, 10f)
                        .add(DefaultProperties.EFFECT_DURATION, 300)
                        .add(DefaultProperties.EFFECT_STRENGTH, 0)
                        .build()
        ));

        FIREBOLT = spell("firebolt", () -> new ProjectileSpell<>(FireBoltEntity::new).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.APPRENTICE, Elements.FIRE, SpellType.PROJECTILE, SpellAction.POINT, 10, 0, 10)
                        .add(DefaultProperties.RANGE, 15f)
                        .add(DefaultProperties.DAMAGE, 5f)
                        .add(DefaultProperties.EFFECT_DURATION, 5)
                        .build()
        ));

        ICE_BALL = spell("ice_ball", () -> new ProjectileSpell<>(IceBall::new).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.APPRENTICE, Elements.ICE, SpellType.PROJECTILE, SpellAction.POINT, 10, 0, 15)
                        .add(DefaultProperties.RANGE, 20f)
                        .add(DefaultProperties.DAMAGE, 5f)
                        .add(DefaultProperties.EFFECT_DURATION, 100)
                        .add(DefaultProperties.EFFECT_STRENGTH, 0)
                        .build()
        ));

        ICE_SHARD = spell("ice_shard", () -> new ArrowSpell<>(IceShardEntity::new).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.APPRENTICE, Elements.ICE, SpellType.PROJECTILE, SpellAction.POINT, 10, 0, 10)
                        .add(DefaultProperties.RANGE, 15f)
                        .add(DefaultProperties.DAMAGE, 6f)
                        .add(DefaultProperties.EFFECT_DURATION, 200)
                        .add(DefaultProperties.EFFECT_STRENGTH, 0)
                        .build()
        ));

        ICE_CHARGE = spell("ice_charge", () -> new ProjectileSpell<>(IceChargeEntity::new).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.APPRENTICE, Elements.ICE, SpellType.PROJECTILE, SpellAction.POINT, 10, 0, 10)
                        .add(DefaultProperties.RANGE, 20f)
                        .add(DefaultProperties.DAMAGE, 4f)
                        .add(DefaultProperties.EFFECT_DURATION, 100)
                        .add(DefaultProperties.EFFECT_STRENGTH, 1)
                        .add(IceChargeEntity.ICE_SHARDS, 10)
                        .build()
        ));

        MAGIC_FIREBALL = spell("magic_fireball", () -> new ProjectileSpell<>(MagicFireballEntity::new).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.APPRENTICE, Elements.FIRE, SpellType.PROJECTILE, SpellAction.POINT, 10, 0, 15)
                        .add(DefaultProperties.RANGE, 20f)
                        .add(DefaultProperties.DAMAGE, 5f)
                        .add(DefaultProperties.EFFECT_DURATION, 5)
                        .build()
        ));

        HOMING_SPARK = spell("homing_spark", () -> new ProjectileSpell<>(SparkEntity::new).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.APPRENTICE, Elements.LIGHTNING, SpellType.PROJECTILE, SpellAction.POINT, 10, 0, 20)
                        .add(DefaultProperties.RANGE, 25f)
                        .add(DefaultProperties.DAMAGE, 6f)
                        .add(DefaultProperties.SEEKING_STRENGTH, 5)
                        .build()
        ));

        LIGHTNING_ARROW = spell("lightning_arrow", () -> new ArrowSpell<>(LightningArrow::new).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.APPRENTICE, Elements.LIGHTNING, SpellType.PROJECTILE, SpellAction.POINT, 15, 0, 20)
                        .add(DefaultProperties.RANGE, 25f)
                        .add(DefaultProperties.DAMAGE, 7f)
                        .build()
        ));

        FORCE_ORB = spell("force_orb", () -> new ProjectileSpell<>(ForceOrbEntity::new)
                .soundValues(0.5f, 0.4f, 0.2f).assignProperties(SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.ADVANCED, Elements.SORCERY, SpellType.PROJECTILE, SpellAction.POINT, 20, 0, 20)
                        .add(DefaultProperties.RANGE, 10F)
                        .add(DefaultProperties.DAMAGE, 4F)
                        .add(DefaultProperties.BLAST_RADIUS, 4F)
                        .build()));


        DARKNESS_ORB = spell("darkness_orb", () -> new ProjectileSpell<>(DarknessOrbEntity::new)
                .soundValues(0.5f, 0.4f, 0.2f).assignProperties(SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.ADVANCED, Elements.NECROMANCY, SpellType.PROJECTILE, SpellAction.POINT, 20, 0, 20)
                        .add(DefaultProperties.RANGE, 30F)
                        .add(DefaultProperties.DAMAGE, 8F)
                        .add(DefaultProperties.EFFECT_DURATION, 150)
                        .add(DefaultProperties.EFFECT_STRENGTH, 1).build()));

        FIRE_RESISTANCE = spell("fire_resistance", () -> new BuffSpell(1, 0.5f, 0, () -> MobEffects.FIRE_RESISTANCE).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.ADVANCED, Elements.FIRE, SpellType.DEFENCE, SpellAction.POINT_UP, 20, 15, 80)
                        .add(BuffSpell.getEffectDurationProperty(MobEffects.FIRE_RESISTANCE), 600)
                        .add(BuffSpell.getEffectStrengthProperty(MobEffects.FIRE_RESISTANCE), 0)
                        .build()
        ));

        NIGHT_VISION = spell("night_vision", () -> new BuffSpell(0, 0.4f, 0.7f, () -> MobEffects.NIGHT_VISION).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.APPRENTICE, Elements.EARTH, SpellType.BUFF, SpellAction.POINT_UP, 20, 0, 40)
                        .add(BuffSpell.getEffectDurationProperty(MobEffects.NIGHT_VISION), 900)
                        .add(BuffSpell.getEffectStrengthProperty(MobEffects.NIGHT_VISION), 1)
                        .build()
        ));

        FONT_OF_VITALITY = spell("font_of_vitality", () -> new BuffSpell(1, 0.8f, 0.3f, () -> MobEffects.ABSORPTION, () -> MobEffects.REGENERATION).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.MASTER, Elements.HEALING, SpellType.DEFENCE, SpellAction.POINT_UP, 75, 20, 300)
                        .add(BuffSpell.getEffectDurationProperty(MobEffects.ABSORPTION), 1200)
                        .add(BuffSpell.getEffectStrengthProperty(MobEffects.ABSORPTION), 1)
                        .add(BuffSpell.getEffectDurationProperty(MobEffects.REGENERATION), 300)
                        .add(BuffSpell.getEffectStrengthProperty(MobEffects.REGENERATION), 1)
                        .build()
        ));

        INVISIBILITY = spell("invisibility", () -> new BuffSpell(0, 0.5f, 0.5f, () -> MobEffects.INVISIBILITY).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.ADVANCED, Elements.SORCERY, SpellType.BUFF, SpellAction.POINT_UP, 35, 15, 200)
                        .add(BuffSpell.getEffectDurationProperty(MobEffects.INVISIBILITY), 600)
                        .add(BuffSpell.getEffectStrengthProperty(MobEffects.INVISIBILITY), 0)
                        .build()
        ));

        WATER_BREATHING = spell("water_breathing", () -> new BuffSpell(0.3f, 0.3f, 1, () -> MobEffects.WATER_BREATHING).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.ADVANCED, Elements.EARTH, SpellType.BUFF, SpellAction.POINT_UP, 30, 15, 250)
                        .add(BuffSpell.getEffectDurationProperty(MobEffects.WATER_BREATHING), 1200)
                        .add(BuffSpell.getEffectStrengthProperty(MobEffects.WATER_BREATHING), 0)
                        .build()
        ));

        AGILITY = spell("agility", () -> new BuffSpell(0.4f, 1.0f, 0.8f, () -> MobEffects.MOVEMENT_SPEED,
                () -> MobEffects.JUMP).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.APPRENTICE, Elements.SORCERY, SpellType.BUFF, SpellAction.POINT_UP, 20, 0, 40)
                        .add(BuffSpell.getEffectDurationProperty(MobEffects.MOVEMENT_SPEED), 600)
                        .add(BuffSpell.getEffectStrengthProperty(MobEffects.MOVEMENT_SPEED), 1)
                        .add(BuffSpell.getEffectDurationProperty(MobEffects.JUMP), 600)
                        .add(BuffSpell.getEffectStrengthProperty(MobEffects.JUMP), 1)
                        .build()
        ));

        FIRE_SKIN = spell("fire_skin", () -> new BuffSpell(1, 0.3f, 0, EBMobEffects.FIRE_SKIN).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.ADVANCED, Elements.FIRE, SpellType.DEFENCE, SpellAction.POINT_UP, 40, 0, 250)
                        .add(BuffSpell.getEffectDurationProperty(EBMobEffects.FIRE_SKIN.get()), 600)
                        .add(BuffSpell.getEffectStrengthProperty(EBMobEffects.FIRE_SKIN.get()), 0)
                        .add(DefaultProperties.EFFECT_DURATION, 5)
                        .build()
        ));


        STATIC_AURA = spell("static_aura", () -> new BuffSpell(0, 0.5f, 0.7f, EBMobEffects.STATIC_AURA).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.ADVANCED, Elements.LIGHTNING, SpellType.DEFENCE, SpellAction.POINT_UP, 40, 0, 250)
                        .add(BuffSpell.getEffectDurationProperty(EBMobEffects.STATIC_AURA.get()), 600)
                        .add(BuffSpell.getEffectStrengthProperty(EBMobEffects.STATIC_AURA.get()), 0)
                        .add(DefaultProperties.DAMAGE, 4F)
                        .build()
        ));

        GREATER_WARD = spell("greater_ward", () -> new BuffSpell(0.75f, 0.6f, 0.8f, EBMobEffects.WARD).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.ADVANCED, Elements.HEALING, SpellType.BUFF, SpellAction.POINT_UP, 20, 10, 65)
                        .add(BuffSpell.getEffectDurationProperty(EBMobEffects.WARD.get()), 600)
                        .add(BuffSpell.getEffectStrengthProperty(EBMobEffects.WARD.get()), 1)
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

        POCKET_FURNACE = spell("pocket_furnace", PocketFurnace::new);

        ENRAGE = spell("enrage", Enrage::new);

        FIRESTORM = spell("firestorm", Firestorm::new);

        GROUP_HEAL = spell("group_heal", GroupHeal::new);

        BLINDING_FLASH = spell("blind_flash", BlindingFlash::new);

        DETONATE = spell("detonate", Detonate::new);

        INVOKE_WEATHER = spell("invoke_weather", InvokeWeather::new);

        OAKFLESH = spell("oakflesh", () -> new BuffSpell(0.6f, 0.5f, 0.4f, EBMobEffects.OAKFLESH).soundValues(0.7f, 1.2f, 0.4f)
                .assignProperties(SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.ADVANCED, Elements.HEALING, SpellType.DEFENCE, SpellAction.POINT_UP, 20, 0, 50)
                        .add(BuffSpell.getEffectDurationProperty(EBMobEffects.OAKFLESH.get()), 600)
                        .add(BuffSpell.getEffectStrengthProperty(EBMobEffects.OAKFLESH.get()), 0)
                        .build()));

        PERMAFROST = spell("permafrost", Permafrost::new);

        GROWTH_AURA = spell("growth_aura", GrowthAura::new);

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

        BLIZZARD = spell("blizzard", () -> new ConstructRangedSpell<>(BlizzardConstruct::new, false)
                .assignProperties(SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.ADVANCED, Elements.ICE, SpellType.CONSTRUCT, SpellAction.POINT, 40, 10, 100)
                        .add(DefaultProperties.DURATION, 600)
                        .add(DefaultProperties.RANGE, 20F)
                        .add(DefaultProperties.EFFECT_RADIUS, 3)
                        .build()));

        CURSE_OF_ENFEEBLEMENT = spell("curse_of_enfeeblement", CurseOfEnfeeblement::new);

        CURSE_OF_UNDEATH = spell("curse_of_undeath", CurseOfUndeath::new);

        FIRE_SIGIL = spell("fire_sigil", () -> new ConstructRangedSpell<>(FireSigilConstruct::new, true).floor(true)
                .assignProperties(SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.APPRENTICE, Elements.FIRE, SpellType.CONSTRUCT, SpellAction.POINT, 20, 0, 40)
                        .add(DefaultProperties.RANGE, 10F)
                        .add(DefaultProperties.EFFECT_RADIUS, 1)
                        .add(DefaultProperties.DAMAGE, 6F)
                        .add(DefaultProperties.EFFECT_DURATION, 10)
                        .build()));

        FROST_SIGIL = spell("frost_sigil", () -> new ConstructRangedSpell<>(FrostSigilConstruct::new, true).floor(true).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.APPRENTICE, Elements.ICE, SpellType.CONSTRUCT, SpellAction.POINT, 20, 0, 40)
                        .add(DefaultProperties.RANGE, 10F)
                        .add(DefaultProperties.EFFECT_RADIUS, 1)
                        .add(DefaultProperties.DAMAGE, 8F)
                        .add(DefaultProperties.EFFECT_DURATION, 200)
                        .add(DefaultProperties.EFFECT_STRENGTH, 1)
                        .build()));

        LIGHTNING_SIGIL = spell("lightning_sigil", () -> new ConstructRangedSpell<>(LightningSigilConstruct::new, true).floor(true).assignProperties(SpellProperties.builder()
                .add(DefaultProperties.RANGE, 10F)
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.LIGHTNING, SpellType.CONSTRUCT, SpellAction.POINT, 20, 0, 40)
                .add(DefaultProperties.EFFECT_RADIUS, 1)
                .add(DefaultProperties.DAMAGE, 6F)
                .add(LightningSigilConstruct.SECOND_RANGE, 1)
                .add(DefaultProperties.MAX_TARGETS, 3)
                .build()));

        ICE_SPICKES = spell("ice_spikes", IceSpickes::new);

        INVIGORATING_PRESENCE = spell("invigorating_presence", InvigoratingPresence::new);

        RING_OF_FIRE = spell("ring_of_fire", () -> new ConstructSpell<>(FireRingConstruct::new, false).floor(true)
                .assignProperties(SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.ADVANCED, Elements.FIRE, SpellType.CONSTRUCT, SpellAction.POINT, 30, 10, 100)
                        .add(DefaultProperties.DURATION, 600)
                        .add(DefaultProperties.EFFECT_RADIUS, 3)
                        .add(DefaultProperties.DAMAGE, 1F)
                        .add(DefaultProperties.EFFECT_DURATION, 10)
                        .build()
                ));

        HEALING_AURA = spell("healing_aura", () -> new ConstructSpell<>(HealAuraConstruct::new, false).floor(true)
                .assignProperties(SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.ADVANCED, Elements.HEALING, SpellType.CONSTRUCT, SpellAction.POINT_DOWN, 35, 15, 150)
                        .add(DefaultProperties.DURATION, 600)
                        .add(DefaultProperties.EFFECT_RADIUS, 3)
                        .add(DefaultProperties.DAMAGE, 1F)
                        .add(DefaultProperties.HEALTH, 1F)
                        .build()));

        //FROST_BARRIER = spell("frost_barrier", FrostBarrier::new);

        COMBUSTION_RUNE = spell("combustion_rune", () -> new ConstructRangedSpell<>(CombustionRuneConstruct::new, true).floor(true)
                .assignProperties(SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.ADVANCED, Elements.FIRE, SpellType.CONSTRUCT, SpellAction.POINT, 30, 0, 50)
                        .add(DefaultProperties.RANGE, 10F)
                        .add(DefaultProperties.BLAST_RADIUS, 2F)
                        .build()));

        BUBBLE = spell("bubble", Bubble::new);

        ENTRAPMENT = spell("entrapment", Entrapment::new);

        HAILSTORM = spell("hailstorm", Hailstorm::new);

        DECAY = spell("decay", Decay::new);

        FLAME_WEAPON = spell("flaming_weapon", FlamingWeapon::new);

        CHARGE = spell("charge", Charge::new);

        CURSE_OF_SOULBINDING = spell("curse_of_soulbinding", CurseOfSoulbinding::new);

        GREATER_TELEKINESIS = spell("greater_telekinesis", BlockWithSurprise::new); // todo

        LIGHTNING_PULSE = spell("lightning_pulse", LightningPulse::new);


        BLOCK_SURPRISE = spell("block_surprise", BlockWithSurprise::new);

        SUMMON_RICH = spell("summon_rich", () -> new MinionSpell<>((l) -> new WitherSkeleton(EntityType.WITHER_SKELETON, l)).assignProperties(
                SpellProperties.builder()
                        .assignBaseProperties(SpellTiers.MASTER, Elements.NECROMANCY, SpellType.MINION, SpellAction.POINT, 100, 50, 600)
                        .add(DefaultProperties.MINION_COUNT, 1)
                        .add(DefaultProperties.MINION_LIFETIME, 1200)
                        .add(DefaultProperties.SUMMON_RADIUS, 5)
                        .add(DefaultProperties.SENSIBLE, true)
                        .build()
        ));
    }

    // ======= Registry =======
    public static void registerNull(RegisterFunction<Spell> function){
        register(null, function);
    }

    @SuppressWarnings("unchecked")
    public static void register(Registry<?> registry, RegisterFunction<Spell> function){
        // remove all the spells with the sensible property
        if(!Services.PLATFORM.isDevelopmentEnvironment()) {
            SPELLS.values().removeIf(spell -> spell.property(DefaultProperties.SENSIBLE));
        }

        SPELLS.forEach(((id, spell) ->
                function.register((Registry<Spell>) registry, WizardryMainMod.location(id), spell)));
    }


    // ====== helpers =======
    static Spell spell(String name, Supplier<Spell> spell) {
        var instantiatedSpell = spell.get();
        SPELLS.put(name, instantiatedSpell);
        return instantiatedSpell;
    }
}