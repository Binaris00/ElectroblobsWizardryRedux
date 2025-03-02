package com.electroblob.wizardry.setup.registries.client;

@SuppressWarnings("unused")
public final class EBSounds {

//    private static final Registrar<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Wizardry.MOD_ID, Registries.SOUND_EVENT).getRegistrar();
//
//    public static final RegistrySupplier<SoundEvent> BLOCK_ARCANE_WORKBENCH_SPELLBIND = create("block.arcane_workbench.bind_spell");
//    public static final RegistrySupplier<SoundEvent> BLOCK_PEDESTAL_ACTIVATE = create("block.pedestal.activate");
//    public static final RegistrySupplier<SoundEvent> BLOCK_PEDESTAL_CONQUER = create("block.pedestal.conquer");
//    public static final RegistrySupplier<SoundEvent> BLOCK_LECTERN_LOCATE_SPELL = create("block.lectern.locate_spell");
//    public static final RegistrySupplier<SoundEvent> BLOCK_RECEPTACLE_IGNITE = create("block.receptacle.ignite");
//    public static final RegistrySupplier<SoundEvent> BLOCK_IMBUEMENT_ALTAR_IMBUE = create("block.imbuement_altar.imbue");
//
//    public static final RegistrySupplier<SoundEvent> ITEM_WAND_SWITCH_SPELL = create("item.wand.switch_spell");
//    public static final RegistrySupplier<SoundEvent> ITEM_WAND_LEVELUP = create("item.wand.levelup");
//    public static final RegistrySupplier<SoundEvent> ITEM_WAND_MELEE = create("item.wand.melee");
//    public static final RegistrySupplier<SoundEvent> ITEM_WAND_CHARGEUP = create("item.wand.chargeup");
//    public static final RegistrySupplier<SoundEvent> ITEM_ARMOUR_EQUIP_SILK = create("item.armour.equip_silk");
//    public static final RegistrySupplier<SoundEvent> ITEM_ARMOUR_EQUIP_SAGE = create("item.armour.equip_sage");
//    public static final RegistrySupplier<SoundEvent> ITEM_ARMOUR_EQUIP_BATTLEMAGE = create("item.armour.equip_battlemage");
//    public static final RegistrySupplier<SoundEvent> ITEM_ARMOUR_EQUIP_WARLOCK = create("item.armour.equip_warlock");
//    public static final RegistrySupplier<SoundEvent> ITEM_PURIFYING_ELIXIR_DRINK = create("item.purifying_elixir.drink");
//    public static final RegistrySupplier<SoundEvent> ITEM_MANA_FLASK_USE = create("item.mana_flask.use");
//    public static final RegistrySupplier<SoundEvent> ITEM_MANA_FLASK_RECHARGE = create("item.mana_flask.recharge");
//    public static final RegistrySupplier<SoundEvent> ITEM_FLAMECATCHER_SHOOT = create("item.flamecatcher.shoot");
//    public static final RegistrySupplier<SoundEvent> ITEM_FLAMECATCHER_FLAME = create("item.flamecatcher.flame");
//
//    public static final RegistrySupplier<SoundEvent> ENTITY_BLACK_HOLE_AMBIENT = create("entity.black_hole.ambient");
//    public static final RegistrySupplier<SoundEvent> ENTITY_BLACK_HOLE_VANISH = create("entity.black_hole.vanish");
//    public static final RegistrySupplier<SoundEvent> ENTITY_BLACK_HOLE_BREAK_BLOCK = create("entity.black_hole.break_block");
//    public static final RegistrySupplier<SoundEvent> ENTITY_BLIZZARD_AMBIENT = create("entity.blizzard.ambient");
//    public static final RegistrySupplier<SoundEvent> ENTITY_BOULDER_ROLL = create("entity.boulder.roll");
//    public static final RegistrySupplier<SoundEvent> ENTITY_BOULDER_LAND = create("entity.boulder.land");
//    public static final RegistrySupplier<SoundEvent> ENTITY_BOULDER_HIT = create("entity.boulder.hit");
//    public static final RegistrySupplier<SoundEvent> ENTITY_BOULDER_BREAK_BLOCK = create("entity.boulder.break_block");
//    public static final RegistrySupplier<SoundEvent> ENTITY_BUBBLE_POP = create("entity.bubble.pop");
//    public static final RegistrySupplier<SoundEvent> ENTITY_DECAY_AMBIENT = create("entity.decay.ambient");
//    public static final RegistrySupplier<SoundEvent> ENTITY_ENTRAPMENT_AMBIENT = create("entity.entrapment.ambient");
//    public static final RegistrySupplier<SoundEvent> ENTITY_ENTRAPMENT_VANISH = create("entity.entrapment.vanish");
//    public static final RegistrySupplier<SoundEvent> ENTITY_TORNADO_AMBIENT = create("entity.tornado.ambient");
//    public static final RegistrySupplier<SoundEvent> ENTITY_WIZARD_AMBIENT = create("entity.wizard.ambient");
//    public static final RegistrySupplier<SoundEvent> ENTITY_WIZARD_TRADING = create("entity.wizard.trading");
//    public static final RegistrySupplier<SoundEvent> ENTITY_WIZARD_HURT = create("entity.wizard.hurt");
//    public static final RegistrySupplier<SoundEvent> ENTITY_WIZARD_DEATH = create("entity.wizard.death");
//    public static final RegistrySupplier<SoundEvent> ENTITY_SMOKE_BOMB_THROW = create("entity.smoke_bomb.throw");
//    public static final RegistrySupplier<SoundEvent> ENTITY_SMOKE_BOMB_SMASH = create("entity.smoke_bomb.smash");
//    public static final RegistrySupplier<SoundEvent> ENTITY_SMOKE_BOMB_SMOKE = create("entity.smoke_bomb.smoke");
//    public static final RegistrySupplier<SoundEvent> ENTITY_FORCE_ARROW_HIT = create("entity.force_arrow.hit");
//    public static final RegistrySupplier<SoundEvent> ENTITY_THUNDERBOLT_HIT = create("entity.thunderbolt.hit");
//    public static final RegistrySupplier<SoundEvent> ENTITY_ICE_LANCE_SMASH = create("entity.ice_lance.smash");
//    public static final RegistrySupplier<SoundEvent> ENTITY_ICE_LANCE_HIT = create("entity.ice_lance.hit");
//    public static final RegistrySupplier<SoundEvent> ENTITY_POISON_BOMB_THROW = create("entity.poison_bomb.throw");
//    public static final RegistrySupplier<SoundEvent> ENTITY_POISON_BOMB_SMASH = create("entity.poison_bomb.smash");
//    public static final RegistrySupplier<SoundEvent> ENTITY_POISON_BOMB_POISON = create("entity.poison_bomb.poison");
//    public static final RegistrySupplier<SoundEvent> ENTITY_FIREBOMB_THROW = create("entity.firebomb.throw");
//    public static final RegistrySupplier<SoundEvent> ENTITY_FIREBOMB_SMASH = create("entity.firebomb.smash");
//    public static final RegistrySupplier<SoundEvent> ENTITY_FIREBOMB_FIRE = create("entity.firebomb.fire");
//    public static final RegistrySupplier<SoundEvent> ENTITY_FIREBOLT_HIT = create("entity.firebolt.hit");
//    public static final RegistrySupplier<SoundEvent> ENTITY_ICEBALL_HIT = create("entity.iceball.hit");
//    public static final RegistrySupplier<SoundEvent> ENTITY_ICE_SHARD_SMASH = create("entity.ice_shard.smash");
//    public static final RegistrySupplier<SoundEvent> ENTITY_ICE_SHARD_HIT = create("entity.ice_shard.hit");
//    public static final RegistrySupplier<SoundEvent> ENTITY_ICE_CHARGE_SMASH = create("entity.ice_charge.smash");
//    public static final RegistrySupplier<SoundEvent> ENTITY_ICE_CHARGE_ICE = create("entity.ice_charge.ice");
//    public static final RegistrySupplier<SoundEvent> ENTITY_LIGHTNING_ARROW_HIT = create("entity.lightning_arrow.hit");
//    public static final RegistrySupplier<SoundEvent> ENTITY_HOMING_SPARK_HIT = create("entity.homing_spark.hit");
//
//    public static final RegistrySupplier<SoundEvent> SPELL_STATIC_AURA_RETALIATE = create("spell.static_aura.retaliate");
//    public static final RegistrySupplier<SoundEvent> SPELL_CURSE_OF_SOULBINDING_RETALIATE = create("spell.curse_of_soulbinding.retaliate");
//    public static final RegistrySupplier<SoundEvent> SPELL_TRANSPORTATION_TRAVEL = create("spell.transportation.travel");
//    public static final RegistrySupplier<SoundEvent> SPELL_WITHER_SKULL = create("spell.wither_skull");
//
//    public static final RegistrySupplier<SoundEvent> MISC_DISCOVER_SPELL = create("misc.discover_spell");
//    public static final RegistrySupplier<SoundEvent> MISC_BOOK_OPEN = create("misc.book_open");
//    public static final RegistrySupplier<SoundEvent> MISC_PAGE_TURN = create("misc.page_turn");
//    public static final RegistrySupplier<SoundEvent> MISC_FREEZE = create("misc.freeze");
//    public static final RegistrySupplier<SoundEvent> MISC_SPELL_FAIL = create("misc.spell_fail");
//
//    private static RegistrySupplier<SoundEvent> create(String name) {
//        final ResourceLocation id = new ResourceLocation(Wizardry.MOD_ID, name);
//        return SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(id));
//    }
//
//    public static void init() {
//    }
//
//    private EBSounds() {
//    }
}

