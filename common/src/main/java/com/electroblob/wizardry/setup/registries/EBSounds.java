package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class EBSounds {
    static Map<String, DeferredObject<SoundEvent>> SOUNDS = new HashMap<>();
    public static final DeferredObject<SoundEvent> BLOCK_ARCANE_WORKBENCH_SPELLBIND = sound("block.arcane_workbench.bind_spell");
    public static final DeferredObject<SoundEvent> BLOCK_PEDESTAL_ACTIVATE = sound("block.pedestal.activate");
    public static final DeferredObject<SoundEvent> BLOCK_PEDESTAL_CONQUER = sound("block.pedestal.conquer");
    public static final DeferredObject<SoundEvent> BLOCK_LECTERN_LOCATE_SPELL = sound("block.lectern.locate_spell");
    public static final DeferredObject<SoundEvent> BLOCK_RECEPTACLE_IGNITE = sound("block.receptacle.ignite");
    public static final DeferredObject<SoundEvent> BLOCK_IMBUEMENT_ALTAR_IMBUE = sound("block.imbuement_altar.imbue");
    public static final DeferredObject<SoundEvent> ITEM_WAND_SWITCH_SPELL = sound("item.wand.switch_spell");
    public static final DeferredObject<SoundEvent> ITEM_WAND_LEVELUP = sound("item.wand.levelup");
    public static final DeferredObject<SoundEvent> ITEM_WAND_MELEE = sound("item.wand.melee");
    public static final DeferredObject<SoundEvent> ITEM_WAND_CHARGEUP = sound("item.wand.chargeup");
    public static final DeferredObject<SoundEvent> ITEM_ARMOUR_EQUIP_SILK = sound("item.armour.equip_silk");
    public static final DeferredObject<SoundEvent> ITEM_ARMOUR_EQUIP_SAGE = sound("item.armour.equip_sage");
    public static final DeferredObject<SoundEvent> ITEM_ARMOUR_EQUIP_BATTLEMAGE = sound("item.armour.equip_battlemage");
    public static final DeferredObject<SoundEvent> ITEM_ARMOUR_EQUIP_WARLOCK = sound("item.armour.equip_warlock");
    public static final DeferredObject<SoundEvent> ITEM_PURIFYING_ELIXIR_DRINK = sound("item.purifying_elixir.drink");
    public static final DeferredObject<SoundEvent> ITEM_MANA_FLASK_USE = sound("item.mana_flask.use");
    public static final DeferredObject<SoundEvent> ITEM_MANA_FLASK_RECHARGE = sound("item.mana_flask.recharge");
    public static final DeferredObject<SoundEvent> ITEM_FLAMECATCHER_SHOOT = sound("item.flamecatcher.shoot");
    public static final DeferredObject<SoundEvent> ITEM_FLAMECATCHER_FLAME = sound("item.flamecatcher.flame");
    public static final DeferredObject<SoundEvent> ENTITY_BLACK_HOLE_AMBIENT = sound("entity.black_hole.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_BLACK_HOLE_VANISH = sound("entity.black_hole.vanish");
    public static final DeferredObject<SoundEvent> ENTITY_BLACK_HOLE_BREAK_BLOCK = sound("entity.black_hole.break_block");
    public static final DeferredObject<SoundEvent> ENTITY_BLIZZARD_AMBIENT = sound("entity.blizzard.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_BOULDER_ROLL = sound("entity.boulder.roll");
    public static final DeferredObject<SoundEvent> ENTITY_BOULDER_LAND = sound("entity.boulder.land");
    public static final DeferredObject<SoundEvent> ENTITY_BOULDER_HIT = sound("entity.boulder.hit");
    public static final DeferredObject<SoundEvent> ENTITY_BOULDER_BREAK_BLOCK = sound("entity.boulder.break_block");
    public static final DeferredObject<SoundEvent> ENTITY_BUBBLE_POP = sound("entity.bubble.pop");
    public static final DeferredObject<SoundEvent> ENTITY_DECAY_AMBIENT = sound("entity.decay.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_ENTRAPMENT_AMBIENT = sound("entity.entrapment.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_ENTRAPMENT_VANISH = sound("entity.entrapment.vanish");
    public static final DeferredObject<SoundEvent> ENTITY_FIRE_RING_AMBIENT = sound("entity.fire_ring.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_FIRE_SIGIL_TRIGGER = sound("entity.fire_sigil.trigger");
    public static final DeferredObject<SoundEvent> ENTITY_FORCEFIELD_AMBIENT = sound("entity.forcefield.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_FORCEFIELD_DEFLECT = sound("entity.forcefield.deflect");
    public static final DeferredObject<SoundEvent> ENTITY_FROST_SIGIL_TRIGGER = sound("entity.frost_sigil.trigger");
    public static final DeferredObject<SoundEvent> ENTITY_HAMMER_ATTACK = sound("entity.hammer.attack");
    public static final DeferredObject<SoundEvent> ENTITY_HAMMER_EXPLODE = sound("entity.hammer.explode");
    public static final DeferredObject<SoundEvent> ENTITY_HAMMER_THROW = sound("entity.hammer.throw");
    public static final DeferredObject<SoundEvent> ENTITY_HAMMER_LAND = sound("entity.hammer.land");
    public static final DeferredObject<SoundEvent> ENTITY_HEAL_AURA_AMBIENT = sound("entity.heal_aura.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_BARRIER_DEFLECT = sound("entity.ice_barrier.deflect");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_BARRIER_EXTEND = sound("entity.ice_barrier.extend");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_SPIKE_EXTEND = sound("entity.ice_spike.extend");
    public static final DeferredObject<SoundEvent> ENTITY_LIGHTNING_SIGIL_TRIGGER = sound("entity.lightning_sigil.trigger");
    public static final DeferredObject<SoundEvent> ENTITY_METEOR_FALLING = sound("entity.meteor.falling");
    public static final DeferredObject<SoundEvent> ENTITY_RADIANT_TOTEM_AMBIENT = sound("entity.radiant_totem.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_RADIANT_TOTEM_VANISH = sound("entity.radiant_totem.vanish");
    public static final DeferredObject<SoundEvent> ENTITY_SHIELD_DEFLECT = sound("entity.shield.deflect");
    public static final DeferredObject<SoundEvent> ENTITY_TORNADO_AMBIENT = sound("entity.tornado.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_WITHERING_TOTEM_AMBIENT = sound("entity.withering_totem.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_WITHERING_TOTEM_EXPLODE = sound("entity.withering_totem.explode");
    public static final DeferredObject<SoundEvent> ENTITY_ZOMBIE_SPAWNER_SPAWN = sound("entity.zombie_spawner.spawn");
    public static final DeferredObject<SoundEvent> ENTITY_EVIL_WIZARD_AMBIENT = sound("entity.evil_wizard.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_EVIL_WIZARD_HURT = sound("entity.evil_wizard.hurt");
    public static final DeferredObject<SoundEvent> ENTITY_EVIL_WIZARD_DEATH = sound("entity.evil_wizard.death");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_GIANT_ATTACK = sound("entity.ice_giant.attack");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_GIANT_DESPAWN = sound("entity.ice_giant.despawn");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_WRAITH_AMBIENT = sound("entity.ice_wraith.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_MAGIC_SLIME_ATTACK = sound("entity.magic_slime.attack");
    public static final DeferredObject<SoundEvent> ENTITY_MAGIC_SLIME_EXPLODE = sound("entity.magic_slime.explode");
    public static final DeferredObject<SoundEvent> ENTITY_MAGIC_SLIME_SPLAT = sound("entity.magic_slime.splat");
    public static final DeferredObject<SoundEvent> ENTITY_PHOENIX_AMBIENT = sound("entity.phoenix.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_PHOENIX_BURN = sound("entity.phoenix.burn");
    public static final DeferredObject<SoundEvent> ENTITY_PHOENIX_FLAP = sound("entity.phoenix.flap");
    public static final DeferredObject<SoundEvent> ENTITY_PHOENIX_HURT = sound("entity.phoenix.hurt");
    public static final DeferredObject<SoundEvent> ENTITY_PHOENIX_DEATH = sound("entity.phoenix.death");
    public static final DeferredObject<SoundEvent> ENTITY_REMNANT_AMBIENT = sound("entity.remnant.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_REMNANT_HURT = sound("entity.remnant.hurt");
    public static final DeferredObject<SoundEvent> ENTITY_REMNANT_DEATH = sound("entity.remnant.death");
    public static final DeferredObject<SoundEvent> ENTITY_SHADOW_WRAITH_AMBIENT = sound("entity.shadow_wraith.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_SHADOW_WRAITH_NOISE = sound("entity.shadow_wraith.noise");
    public static final DeferredObject<SoundEvent> ENTITY_SHADOW_WRAITH_HURT = sound("entity.shadow_wraith.hurt");
    public static final DeferredObject<SoundEvent> ENTITY_SHADOW_WRAITH_DEATH = sound("entity.shadow_wraith.death");
    public static final DeferredObject<SoundEvent> ENTITY_SPIRIT_HORSE_VANISH = sound("entity.spirit_horse.vanish");
    public static final DeferredObject<SoundEvent> ENTITY_SPIRIT_WOLF_VANISH = sound("entity.spirit_wolf.vanish");
    public static final DeferredObject<SoundEvent> ENTITY_STORM_ELEMENTAL_AMBIENT = sound("entity.storm_elemental.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_STORM_ELEMENTAL_BURN = sound("entity.storm_elemental.burn");
    public static final DeferredObject<SoundEvent> ENTITY_STORM_ELEMENTAL_WIND = sound("entity.storm_elemental.wind");
    public static final DeferredObject<SoundEvent> ENTITY_STORM_ELEMENTAL_HURT = sound("entity.storm_elemental.hurt");
    public static final DeferredObject<SoundEvent> ENTITY_STORM_ELEMENTAL_DEATH = sound("entity.storm_elemental.death");
    public static final DeferredObject<SoundEvent> ENTITY_STORMCLOUD_THUNDER = sound("entity.stormcloud.thunder");
    public static final DeferredObject<SoundEvent> ENTITY_STORMCLOUD_ATTACK = sound("entity.stormcloud.attack");
    public static final DeferredObject<SoundEvent> ENTITY_WIZARD_YES = sound("entity.wizard.yes");
    public static final DeferredObject<SoundEvent> ENTITY_WIZARD_NO = sound("entity.wizard.no");
    public static final DeferredObject<SoundEvent> ENTITY_WIZARD_AMBIENT = sound("entity.wizard.ambient");
    public static final DeferredObject<SoundEvent> ENTITY_WIZARD_TRADING = sound("entity.wizard.trading");
    public static final DeferredObject<SoundEvent> ENTITY_WIZARD_HURT = sound("entity.wizard.hurt");
    public static final DeferredObject<SoundEvent> ENTITY_WIZARD_DEATH = sound("entity.wizard.death");
    public static final DeferredObject<SoundEvent> ENTITY_WIZARD_HOHOHO = sound("entity.wizard.hohoho");
    public static final DeferredObject<SoundEvent> ENTITY_DARKNESS_ORB_HIT = sound("entity.darkness_orb.hit");
    public static final DeferredObject<SoundEvent> ENTITY_DART_HIT = sound("entity.dart.hit");
    public static final DeferredObject<SoundEvent> ENTITY_DART_HIT_BLOCK = sound("entity.dart.hit_block");
    public static final DeferredObject<SoundEvent> ENTITY_FIREBOLT_HIT = sound("entity.firebolt.hit");
    public static final DeferredObject<SoundEvent> ENTITY_FIREBOMB_THROW = sound("entity.firebomb.throw");
    public static final DeferredObject<SoundEvent> ENTITY_FIREBOMB_SMASH = sound("entity.firebomb.smash");
    public static final DeferredObject<SoundEvent> ENTITY_FIREBOMB_FIRE = sound("entity.firebomb.fire");
    public static final DeferredObject<SoundEvent> ENTITY_FLAMECATCHER_ARROW_HIT = sound("entity.flamecatcher_arrow.hit");
    public static final DeferredObject<SoundEvent> ENTITY_FORCE_ARROW_HIT = sound("entity.force_arrow.hit");
    public static final DeferredObject<SoundEvent> ENTITY_FORCE_ORB_HIT = sound("entity.force_orb.hit");
    public static final DeferredObject<SoundEvent> ENTITY_FORCE_ORB_HIT_BLOCK = sound("entity.force_orb.hit_block");
    public static final DeferredObject<SoundEvent> ENTITY_ICEBALL_HIT = sound("entity.iceball.hit");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_CHARGE_SMASH = sound("entity.ice_charge.smash");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_CHARGE_ICE = sound("entity.ice_charge.ice");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_LANCE_SMASH = sound("entity.ice_lance.smash");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_LANCE_HIT = sound("entity.ice_lance.hit");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_SHARD_SMASH = sound("entity.ice_shard.smash");
    public static final DeferredObject<SoundEvent> ENTITY_ICE_SHARD_HIT = sound("entity.ice_shard.hit");
    public static final DeferredObject<SoundEvent> ENTITY_LIGHTNING_ARROW_HIT = sound("entity.lightning_arrow.hit");
    public static final DeferredObject<SoundEvent> ENTITY_LIGHTNING_DISC_HIT = sound("entity.lightning_disc.hit");
    public static final DeferredObject<SoundEvent> ENTITY_MAGIC_MISSILE_HIT = sound("entity.magic_missile.hit");
    public static final DeferredObject<SoundEvent> ENTITY_POISON_BOMB_THROW = sound("entity.poison_bomb.throw");
    public static final DeferredObject<SoundEvent> ENTITY_POISON_BOMB_SMASH = sound("entity.poison_bomb.smash");
    public static final DeferredObject<SoundEvent> ENTITY_POISON_BOMB_POISON = sound("entity.poison_bomb.poison");
    public static final DeferredObject<SoundEvent> ENTITY_SMOKE_BOMB_THROW = sound("entity.smoke_bomb.throw");
    public static final DeferredObject<SoundEvent> ENTITY_SMOKE_BOMB_SMASH = sound("entity.smoke_bomb.smash");
    public static final DeferredObject<SoundEvent> ENTITY_SMOKE_BOMB_SMOKE = sound("entity.smoke_bomb.smoke");
    public static final DeferredObject<SoundEvent> ENTITY_HOMING_SPARK_HIT = sound("entity.homing_spark.hit");
    public static final DeferredObject<SoundEvent> ENTITY_SPARK_BOMB_THROW = sound("entity.spark_bomb.throw");
    public static final DeferredObject<SoundEvent> ENTITY_SPARK_BOMB_HIT = sound("entity.spark_bomb.hit");
    public static final DeferredObject<SoundEvent> ENTITY_SPARK_BOMB_HIT_BLOCK = sound("entity.spark_bomb.hit_block");
    public static final DeferredObject<SoundEvent> ENTITY_SPARK_BOMB_CHAIN = sound("entity.spark_bomb.chain");
    public static final DeferredObject<SoundEvent> ENTITY_THUNDERBOLT_HIT = sound("entity.thunderbolt.hit");
    public static final DeferredObject<SoundEvent> SPELL_STATIC_AURA_RETALIATE = sound("spell.static_aura.retaliate");
    public static final DeferredObject<SoundEvent> SPELL_CURSE_OF_SOULBINDING_RETALIATE = sound("spell.curse_of_soulbinding.retaliate");
    public static final DeferredObject<SoundEvent> SPELL_TRANSPORTATION_TRAVEL = sound("spell.transportation.travel");
    public static final DeferredObject<SoundEvent> MISC_DISCOVER_SPELL = sound("misc.discover_spell");
    public static final DeferredObject<SoundEvent> MISC_BOOK_OPEN = sound("misc.book_open");
    public static final DeferredObject<SoundEvent> MISC_PAGE_TURN = sound("misc.page_turn");
    public static final DeferredObject<SoundEvent> MISC_FREEZE = sound("misc.freeze");
    public static final DeferredObject<SoundEvent> MISC_SPELL_FAIL = sound("misc.spell_fail");
    private EBSounds() {
    }

    // ======= Registry =======
    public static void register(RegisterFunction<SoundEvent> function) {
        SOUNDS.forEach(((id, sound) ->
                function.register(BuiltInRegistries.SOUND_EVENT, WizardryMainMod.location(id), sound.get())));
    }

    // ======= Helpers =======
    @Nullable
    public static DeferredObject<SoundEvent> getSound(String name) {
        return SOUNDS.get(name);
    }

    static DeferredObject<SoundEvent> sound(String name) {
        SoundEvent sound = SoundEvent.createVariableRangeEvent(WizardryMainMod.location(name));
        DeferredObject<SoundEvent> deferredSound = new DeferredObject<>(() -> sound);
        SOUNDS.put(name, deferredSound);
        return deferredSound;
    }
}
