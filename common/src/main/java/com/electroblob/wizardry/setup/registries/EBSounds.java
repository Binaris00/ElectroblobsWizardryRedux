package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.setup.SpellSoundManager;
import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.common.DeferredObject;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public final class EBSounds {
    private EBSounds(){}

    public static final DeferredObject<SoundEvent> BLOCK_ARCANE_WORKBENCH_SPELLBIND = Register.sound("block.arcane_workbench.bind_spell");
    public static final DeferredObject<SoundEvent> BLOCK_PEDESTAL_ACTIVATE = Register.sound("block.pedestal.activate");
    public static final DeferredObject<SoundEvent> BLOCK_PEDESTAL_CONQUER = Register.sound("block.pedestal.conquer");
    public static final DeferredObject<SoundEvent> BLOCK_LECTERN_LOCATE_SPELL = Register.sound("block.lectern.locate_spell");
    public static final DeferredObject<SoundEvent> BLOCK_RECEPTACLE_IGNITE = Register.sound("block.receptacle.ignite");
    public static final DeferredObject<SoundEvent> BLOCK_IMBUEMENT_ALTAR_IMBUE = Register.sound("block.imbuement_altar.imbue");

    public static final DeferredObject<SoundEvent> ITEM_WAND_SWITCH_SPELL = Register.sound("item.wand.switch_spell");
    public static final DeferredObject<SoundEvent> ITEM_WAND_LEVELUP = Register.sound("item.wand.levelup");
    public static final DeferredObject<SoundEvent> ITEM_WAND_MELEE = Register.sound("item.wand.melee");
    public static final DeferredObject<SoundEvent> ITEM_WAND_CHARGEUP = Register.sound("item.wand.chargeup");
    public static final DeferredObject<SoundEvent> ITEM_ARMOUR_EQUIP_SILK = Register.sound("item.armour.equip_silk");
    public static final DeferredObject<SoundEvent> ITEM_ARMOUR_EQUIP_SAGE = Register.sound("item.armour.equip_sage");
    public static final DeferredObject<SoundEvent> ITEM_ARMOUR_EQUIP_BATTLEMAGE = Register.sound("item.armour.equip_battlemage");
    public static final DeferredObject<SoundEvent> ITEM_ARMOUR_EQUIP_WARLOCK = Register.sound("item.armour.equip_warlock");
    public static final DeferredObject<SoundEvent> ITEM_PURIFYING_ELIXIR_DRINK = Register.sound("item.purifying_elixir.drink");
    public static final DeferredObject<SoundEvent> ITEM_MANA_FLASK_USE = Register.sound("item.mana_flask.use");
    public static final DeferredObject<SoundEvent> ITEM_MANA_FLASK_RECHARGE = Register.sound("item.mana_flask.recharge");
    public static final DeferredObject<SoundEvent> ITEM_FLAMECATCHER_SHOOT = Register.sound("item.flamecatcher.shoot");
    public static final DeferredObject<SoundEvent> ITEM_FLAMECATCHER_FLAME = Register.sound("item.flamecatcher.flame");

    public static final DeferredObject<SoundEvent> ENTITY_BLACK_HOLE_AMBIENT = Register.sound("entity.black_hole.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_BLACK_HOLE_VANISH = Register.sound("entity.black_hole.vanish");
    public static final DeferredObject<SoundEvent> ENTITY_BLACK_HOLE_BREAK_BLOCK = Register.sound("entity.black_hole.break_block");
    public static final DeferredObject<SoundEvent> ENTITY_BLIZZARD_AMBIENT = Register.sound("entity.blizzard.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_BOULDER_ROLL = Register.sound("entity.boulder.roll");
    public static final DeferredObject<SoundEvent> ENTITY_BOULDER_LAND = Register.sound("entity.boulder.land");
    public static final DeferredObject<SoundEvent> ENTITY_BOULDER_HIT = Register.sound("entity.boulder.hit");
    public static final DeferredObject<SoundEvent> ENTITY_BOULDER_BREAK_BLOCK = Register.sound("entity.boulder.break_block");
    public static final DeferredObject<SoundEvent> ENTITY_BUBBLE_POP = Register.sound("entity.bubble.pop");
    public static final DeferredObject<SoundEvent> ENTITY_DECAY_AMBIENT = Register.sound("entity.decay.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_ENTRAPMENT_AMBIENT = Register.sound("entity.entrapment.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_ENTRAPMENT_VANISH = Register.sound("entity.entrapment.vanish");
    public static final DeferredObject<SoundEvent> ENTITY_FIRE_RING_AMBIENT = Register.sound("entity.fire_ring.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_FIRE_SIGIL_TRIGGER = Register.sound("entity.fire_sigil.trigger");
    public static final DeferredObject<SoundEvent> ENTITY_FORCEFIELD_AMBIENT = Register.sound("entity.forcefield.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_FORCEFIELD_DEFLECT = Register.sound("entity.forcefield.deflect");
    public static final DeferredObject<SoundEvent> ENTITY_FROST_SIGIL_TRIGGER = Register.sound("entity.frost_sigil.trigger");
    public static final DeferredObject<SoundEvent> ENTITY_HAMMER_ATTACK = Register.sound("entity.hammer.attack");
    public static final DeferredObject<SoundEvent> ENTITY_HAMMER_EXPLODE = Register.sound("entity.hammer.explode");
    public static final DeferredObject<SoundEvent> ENTITY_HAMMER_THROW = Register.sound("entity.hammer.throw");
    public static final DeferredObject<SoundEvent> ENTITY_HAMMER_LAND = Register.sound("entity.hammer.land");
    public static final DeferredObject<SoundEvent> ENTITY_HEAL_AURA_AMBIENT = Register.sound("entity.heal_aura.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_BARRIER_DEFLECT = Register.sound("entity.ice_barrier.deflect");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_BARRIER_EXTEND = Register.sound("entity.ice_barrier.extend");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_SPIKE_EXTEND = Register.sound("entity.ice_spike.extend");
    public static final DeferredObject<SoundEvent> ENTITY_LIGHTNING_SIGIL_TRIGGER = Register.sound("entity.lightning_sigil.trigger");
    public static final DeferredObject<SoundEvent> ENTITY_METEOR_FALLING = Register.sound("entity.meteor.falling");
    public static final DeferredObject<SoundEvent> ENTITY_RADIANT_TOTEM_AMBIENT = Register.sound("entity.radiant_totem.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_RADIANT_TOTEM_VANISH = Register.sound("entity.radiant_totem.vanish");
    public static final DeferredObject<SoundEvent> ENTITY_SHIELD_DEFLECT = Register.sound("entity.shield.deflect");
    public static final DeferredObject<SoundEvent> ENTITY_TORNADO_AMBIENT = Register.sound("entity.tornado.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_WITHERING_TOTEM_AMBIENT = Register.sound("entity.withering_totem.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_WITHERING_TOTEM_EXPLODE = Register.sound("entity.withering_totem.explode");
    public static final DeferredObject<SoundEvent> ENTITY_ZOMBIE_SPAWNER_SPAWN = Register.sound("entity.zombie_spawner.spawn");

    public static final DeferredObject<SoundEvent> ENTITY_EVIL_WIZARD_AMBIENT = Register.sound("entity.evil_wizard.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_EVIL_WIZARD_HURT = Register.sound("entity.evil_wizard.hurt");
    public static final DeferredObject<SoundEvent> ENTITY_EVIL_WIZARD_DEATH = Register.sound("entity.evil_wizard.death");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_GIANT_ATTACK = Register.sound("entity.ice_giant.attack");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_GIANT_DESPAWN = Register.sound("entity.ice_giant.despawn");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_WRAITH_AMBIENT = Register.sound("entity.ice_wraith.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_MAGIC_SLIME_ATTACK = Register.sound("entity.magic_slime.attack");
    public static final DeferredObject<SoundEvent> ENTITY_MAGIC_SLIME_EXPLODE = Register.sound("entity.magic_slime.explode");
    public static final DeferredObject<SoundEvent> ENTITY_MAGIC_SLIME_SPLAT = Register.sound("entity.magic_slime.splat");
    public static final DeferredObject<SoundEvent> ENTITY_PHOENIX_AMBIENT = Register.sound("entity.phoenix.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_PHOENIX_BURN = Register.sound("entity.phoenix.burn");
    public static final DeferredObject<SoundEvent> ENTITY_PHOENIX_FLAP = Register.sound("entity.phoenix.flap");
    public static final DeferredObject<SoundEvent> ENTITY_PHOENIX_HURT = Register.sound("entity.phoenix.hurt");
    public static final DeferredObject<SoundEvent> ENTITY_PHOENIX_DEATH = Register.sound("entity.phoenix.death");
    public static final DeferredObject<SoundEvent> ENTITY_REMNANT_AMBIENT = Register.sound("entity.remnant.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_REMNANT_HURT = Register.sound("entity.remnant.hurt");
    public static final DeferredObject<SoundEvent> ENTITY_REMNANT_DEATH = Register.sound("entity.remnant.death");
    public static final DeferredObject<SoundEvent> ENTITY_SHADOW_WRAITH_AMBIENT = Register.sound("entity.shadow_wraith.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_SHADOW_WRAITH_NOISE = Register.sound("entity.shadow_wraith.noise");
    public static final DeferredObject<SoundEvent> ENTITY_SHADOW_WRAITH_HURT = Register.sound("entity.shadow_wraith.hurt");
    public static final DeferredObject<SoundEvent> ENTITY_SHADOW_WRAITH_DEATH = Register.sound("entity.shadow_wraith.death");
    public static final DeferredObject<SoundEvent> ENTITY_SPIRIT_HORSE_VANISH = Register.sound("entity.spirit_horse.vanish");
    public static final DeferredObject<SoundEvent> ENTITY_SPIRIT_WOLF_VANISH = Register.sound("entity.spirit_wolf.vanish");
    public static final DeferredObject<SoundEvent> ENTITY_STORM_ELEMENTAL_AMBIENT = Register.sound("entity.storm_elemental.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_STORM_ELEMENTAL_BURN = Register.sound("entity.storm_elemental.burn");
    public static final DeferredObject<SoundEvent> ENTITY_STORM_ELEMENTAL_WIND = Register.sound("entity.storm_elemental.wind");
    public static final DeferredObject<SoundEvent> ENTITY_STORM_ELEMENTAL_HURT = Register.sound("entity.storm_elemental.hurt");
    public static final DeferredObject<SoundEvent> ENTITY_STORM_ELEMENTAL_DEATH = Register.sound("entity.storm_elemental.death");
    public static final DeferredObject<SoundEvent> ENTITY_STORMCLOUD_THUNDER = Register.sound("entity.stormcloud.thunder");
    public static final DeferredObject<SoundEvent> ENTITY_STORMCLOUD_ATTACK = Register.sound("entity.stormcloud.attack");
    public static final DeferredObject<SoundEvent> ENTITY_WIZARD_YES = Register.sound("entity.wizard.yes");
    public static final DeferredObject<SoundEvent> ENTITY_WIZARD_NO = Register.sound("entity.wizard.no");
    public static final DeferredObject<SoundEvent> ENTITY_WIZARD_AMBIENT = Register.sound("entity.wizard.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_WIZARD_TRADING = Register.sound("entity.wizard.trading");
    public static final DeferredObject<SoundEvent> ENTITY_WIZARD_HURT = Register.sound("entity.wizard.hurt");
    public static final DeferredObject<SoundEvent> ENTITY_WIZARD_DEATH = Register.sound("entity.wizard.death");
    public static final DeferredObject<SoundEvent> ENTITY_WIZARD_HOHOHO = Register.sound("entity.wizard.hohoho");

    public static final DeferredObject<SoundEvent> ENTITY_DARKNESS_ORB_HIT = Register.sound("entity.darkness_orb.hit");
    public static final DeferredObject<SoundEvent> ENTITY_DART_HIT = Register.sound("entity.dart.hit");
    public static final DeferredObject<SoundEvent> ENTITY_DART_HIT_BLOCK = Register.sound("entity.dart.hit_block");
    public static final DeferredObject<SoundEvent> ENTITY_FIREBOLT_HIT = Register.sound("entity.firebolt.hit");
    public static final DeferredObject<SoundEvent> ENTITY_FIREBOMB_THROW = Register.sound("entity.firebomb.throw");
    public static final DeferredObject<SoundEvent> ENTITY_FIREBOMB_SMASH = Register.sound("entity.firebomb.smash");
    public static final DeferredObject<SoundEvent> ENTITY_FIREBOMB_FIRE = Register.sound("entity.firebomb.fire");
    public static final DeferredObject<SoundEvent> ENTITY_FLAMECATCHER_ARROW_HIT = Register.sound("entity.flamecatcher_arrow.hit");
    public static final DeferredObject<SoundEvent> ENTITY_FORCE_ARROW_HIT = Register.sound("entity.force_arrow.hit");
    public static final DeferredObject<SoundEvent> ENTITY_FORCE_ORB_HIT = Register.sound("entity.force_orb.hit");
    public static final DeferredObject<SoundEvent> ENTITY_FORCE_ORB_HIT_BLOCK = Register.sound("entity.force_orb.hit_block");
    public static final DeferredObject<SoundEvent> ENTITY_ICEBALL_HIT = Register.sound("entity.iceball.hit");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_CHARGE_SMASH = Register.sound("entity.ice_charge.smash");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_CHARGE_ICE = Register.sound("entity.ice_charge.ice");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_LANCE_SMASH = Register.sound("entity.ice_lance.smash");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_LANCE_HIT = Register.sound("entity.ice_lance.hit");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_SHARD_SMASH = Register.sound("entity.ice_shard.smash");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_SHARD_HIT = Register.sound("entity.ice_shard.hit");
    public static final DeferredObject<SoundEvent> ENTITY_LIGHTNING_ARROW_HIT = Register.sound("entity.lightning_arrow.hit");
    public static final DeferredObject<SoundEvent> ENTITY_LIGHTNING_DISC_HIT = Register.sound("entity.lightning_disc.hit");
    public static final DeferredObject<SoundEvent> ENTITY_MAGIC_MISSILE_HIT = Register.sound("entity.magic_missile.hit");
    public static final DeferredObject<SoundEvent> ENTITY_POISON_BOMB_THROW = Register.sound("entity.poison_bomb.throw");
    public static final DeferredObject<SoundEvent> ENTITY_POISON_BOMB_SMASH = Register.sound("entity.poison_bomb.smash");
    public static final DeferredObject<SoundEvent> ENTITY_POISON_BOMB_POISON = Register.sound("entity.poison_bomb.poison");
    public static final DeferredObject<SoundEvent> ENTITY_SMOKE_BOMB_THROW = Register.sound("entity.smoke_bomb.throw");
    public static final DeferredObject<SoundEvent> ENTITY_SMOKE_BOMB_SMASH = Register.sound("entity.smoke_bomb.smash");
    public static final DeferredObject<SoundEvent> ENTITY_SMOKE_BOMB_SMOKE = Register.sound("entity.smoke_bomb.smoke");
    public static final DeferredObject<SoundEvent> ENTITY_HOMING_SPARK_HIT = Register.sound("entity.homing_spark.hit");
    public static final DeferredObject<SoundEvent> ENTITY_SPARK_BOMB_THROW = Register.sound("entity.spark_bomb.throw");
    public static final DeferredObject<SoundEvent> ENTITY_SPARK_BOMB_HIT = Register.sound("entity.spark_bomb.hit");
    public static final DeferredObject<SoundEvent> ENTITY_SPARK_BOMB_HIT_BLOCK = Register.sound("entity.spark_bomb.hit_block");
    public static final DeferredObject<SoundEvent> ENTITY_SPARK_BOMB_CHAIN = Register.sound("entity.spark_bomb.chain");
    public static final DeferredObject<SoundEvent> ENTITY_THUNDERBOLT_HIT = Register.sound("entity.thunderbolt.hit");

    public static final DeferredObject<SoundEvent> SPELL_STATIC_AURA_RETALIATE = Register.sound("spell.static_aura.retaliate");
    public static final DeferredObject<SoundEvent> SPELL_CURSE_OF_SOULBINDING_RETALIATE = Register.sound("spell.curse_of_soulbinding.retaliate");
    public static final DeferredObject<SoundEvent> SPELL_TRANSPORTATION_TRAVEL = Register.sound("spell.transportation.travel");

    public static final DeferredObject<SoundEvent> MISC_DISCOVER_SPELL = Register.sound("misc.discover_spell");
    public static final DeferredObject<SoundEvent> MISC_BOOK_OPEN = Register.sound("misc.book_open");
    public static final DeferredObject<SoundEvent> MISC_PAGE_TURN = Register.sound("misc.page_turn");
    public static final DeferredObject<SoundEvent> MISC_FREEZE = Register.sound("misc.freeze");
    public static final DeferredObject<SoundEvent> MISC_SPELL_FAIL = Register.sound("misc.spell_fail");


    static void handleRegistration(Consumer<Set<Map.Entry<String, DeferredObject<SoundEvent>>>> handler) {
        // Registering spell sounds first
        SpellSoundManager.registerSpellSounds();
        handler.accept(Collections.unmodifiableSet(Register.SOUNDS.entrySet()));
    }

    @Nullable
    public static DeferredObject<SoundEvent> getSound(String name){
        return Register.SOUNDS.get(name);
    }

    static class Register{
        static Map<String, DeferredObject<SoundEvent>> SOUNDS = new HashMap<>();

        static DeferredObject<SoundEvent> sound(String name){
            SoundEvent sound = SoundEvent.createVariableRangeEvent(WizardryMainMod.location(name));
            DeferredObject<SoundEvent> deferredSound = new DeferredObject<>(() -> sound);
            SOUNDS.put(name, deferredSound);
            return deferredSound;
        }
    }
}
