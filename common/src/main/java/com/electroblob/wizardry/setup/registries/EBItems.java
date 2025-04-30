package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.Tier;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import com.electroblob.wizardry.content.item.*;
import com.electroblob.wizardry.setup.datagen.DataGenProcessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Supplier;


/**
 * The registration of all wizardry items, sorted by category for helping with creative tabs <br><br>
 * Sorted by:
 * <ul>
 *     <li>Bombs</li>
 *     <li>General Items</li>
 *     <li>Flasks</li>
 *     <li>Spectral Dust</li>
 *     <li>Wands</li>
 *     <li>Crystals</li>
 *     <li>Wand Upgrades</li>
 *     <li>Wizard Armor</li>
 *     <li>Artifacts</li>
 *     <li>Conjured (Spectral) Spell Cast Items</li>
 * </ul>
 */
@SuppressWarnings("unused")
public final class EBItems {
    static final LinkedList<DeferredObject<? extends Item>> ARMORS = new LinkedList<>();
    static final LinkedList<DeferredObject<? extends Item>> LEGGINGS = new LinkedList<>();
    static final LinkedList<DeferredObject<? extends Item>> WANDS = new LinkedList<>();
    static final LinkedList<DeferredObject<? extends Item>> ARTIFACTS = new LinkedList<>();
    static Map<String, DeferredObject<? extends Item>> ITEMS = new HashMap<>();

    private EBItems() {}

    //Bombs
    public static final DeferredObject<Item> FIREBOMB = item("firebomb");
    public static final DeferredObject<Item> POISON_BOMB = item("poison_bomb");
    public static final DeferredObject<Item> SMOKE_BOMB = item("smoke_bomb");
    public static final DeferredObject<Item> SPARK_BOMB = item("spark_bomb");

    //General Items
    public static final DeferredObject<Item> ARCANE_TOME = item("arcane_tome");
    public static final DeferredObject<Item> BLANK_SCROLL = item("blank_scroll");
    public static final DeferredObject<Item> RUINED_SPELL_BOOK = item("ruined_spell_book");
    public static final DeferredObject<Item> SCROLL = item("scroll", () -> new ScrollItem(new Item.Properties().stacksTo(16)));
    public static final DeferredObject<Item> SPELL_BOOK = item("spell_book", () -> new SpellBookItem(new Item.Properties().stacksTo(16)));
    public static final DeferredObject<Item> WIZARD_HANDBOOK = item("wizard_handbook");

    public static final DeferredObject<Item> ASTRAL_DIAMOND = item("astral_diamond");
    public static final DeferredObject<Item> CRYSTAL_SILVER_PLATING = armorUpgrade("crystal_silver_plating");
    public static final DeferredObject<Item> ETHEREAL_CRYSTAL_WEAVE = armorUpgrade("ethereal_crystal_weave");
    public static final DeferredObject<Item> IDENTIFICATION_SCROLL = item("identification_scroll");
    public static final DeferredObject<Item> MAGIC_SILK = item("magic_silk");
    public static final DeferredObject<Item> PURIFYING_ELIXIR = item("purifying_elixir");
    public static final DeferredObject<Item> RESPLENDENT_THREAD = armorUpgrade("resplendent_thread");

    //Flasks
    public static final DeferredObject<Item> SMALL_MANA_FLASK = item("mana_flask_small");
    public static final DeferredObject<Item> MEDIUM_MANA_FLASK = item("mana_flask_medium");
    public static final DeferredObject<Item> LARGE_MANA_FLASK = item("mana_flask_large");

    //Spectral Dust
    public static final DeferredObject<Item> SPECTRAL_DUST = item("spectral_dust");
    public static final DeferredObject<Item> SPECTRAL_DUST_EARTH = item("spectral_dust_earth");
    public static final DeferredObject<Item> SPECTRAL_DUST_FIRE = item("spectral_dust_fire");
    public static final DeferredObject<Item> SPECTRAL_DUST_HEALING = item("spectral_dust_healing");
    public static final DeferredObject<Item> SPECTRAL_DUST_ICE = item("spectral_dust_ice");
    public static final DeferredObject<Item> SPECTRAL_DUST_LIGHTNING = item("spectral_dust_lightning");
    public static final DeferredObject<Item> SPECTRAL_DUST_NECROMANCY = item("spectral_dust_necromancy");
    public static final DeferredObject<Item> SPECTRAL_DUST_SORCERY = item("spectral_dust_sorcery");

    //Wands
    public static final DeferredObject<Item> NOVICE_WAND = wand("wand_novice", Tiers.NOVICE, null);
    public static final DeferredObject<Item> APPRENTICE_WAND = wand("wand_apprentice", Tiers.APPRENTICE, null);
    public static final DeferredObject<Item> ADVANCED_WAND = wand("wand_advanced", Tiers.ADVANCED,null);
    public static final DeferredObject<Item> MASTER_WAND = wand("wand_master", Tiers.MASTER, null);

    public static final DeferredObject<Item> NOVICE_EARTH_WAND = wand("wand_novice_earth", Tiers.NOVICE, Elements.EARTH); // Earth Wands
    public static final DeferredObject<Item> APPRENTICE_EARTH_WAND = wand("wand_apprentice_earth", Tiers.APPRENTICE, Elements.EARTH);
    public static final DeferredObject<Item> ADVANCED_EARTH_WAND = wand("wand_advanced_earth", Tiers.ADVANCED, Elements.EARTH);
    public static final DeferredObject<Item> MASTER_EARTH_WAND = wand("wand_master_earth", Tiers.MASTER, Elements.EARTH);

    public static final DeferredObject<Item> NOVICE_FIRE_WAND = wand("wand_novice_fire", Tiers.NOVICE, Elements.FIRE); // Fire Wands
    public static final DeferredObject<Item> APPRENTICE_FIRE_WAND = wand("wand_apprentice_fire", Tiers.APPRENTICE, Elements.FIRE);
    public static final DeferredObject<Item> ADVANCED_FIRE_WAND = wand("wand_advanced_fire", Tiers.ADVANCED, Elements.FIRE);
    public static final DeferredObject<Item> MASTER_FIRE_WAND = wand("wand_master_fire", Tiers.MASTER, Elements.FIRE);

    public static final DeferredObject<Item> NOVICE_HEALING_WAND = wand("wand_novice_healing", Tiers.NOVICE, Elements.HEALING); // Healing Wands
    public static final DeferredObject<Item> APPRENTICE_HEALING_WAND = wand("wand_apprentice_healing", Tiers.APPRENTICE, Elements.HEALING);
    public static final DeferredObject<Item> ADVANCED_HEALING_WAND = wand("wand_advanced_healing", Tiers.ADVANCED, Elements.HEALING);
    public static final DeferredObject<Item> MASTER_HEALING_WAND = wand("wand_master_healing", Tiers.MASTER, Elements.HEALING);

    public static final DeferredObject<Item> NOVICE_ICE_WAND = wand("wand_novice_ice", Tiers.NOVICE , Elements.ICE); // Ice Wands
    public static final DeferredObject<Item> APPRENTICE_ICE_WAND = wand("wand_apprentice_ice", Tiers.APPRENTICE, Elements.ICE);
    public static final DeferredObject<Item> ADVANCED_ICE_WAND = wand("wand_advanced_ice", Tiers.ADVANCED, Elements.ICE);
    public static final DeferredObject<Item> MASTER_ICE_WAND = wand("wand_master_ice", Tiers.MASTER, Elements.ICE);

    public static final DeferredObject<Item> NOVICE_LIGHTNING_WAND = wand("wand_novice_lightning", Tiers.NOVICE, Elements.LIGHTNING); // Lightning Wands
    public static final DeferredObject<Item> APPRENTICE_LIGHTNING_WAND = wand("wand_apprentice_lightning", Tiers.APPRENTICE, Elements.LIGHTNING);
    public static final DeferredObject<Item> ADVANCED_LIGHTNING_WAND = wand("wand_advanced_lightning", Tiers.ADVANCED, Elements.LIGHTNING);
    public static final DeferredObject<Item> MASTER_LIGHTNING_WAND = wand("wand_master_lightning", Tiers.MASTER, Elements.LIGHTNING);

    public static final DeferredObject<Item> NOVICE_NECROMANCY_WAND = wand("wand_novice_necromancy", Tiers.NOVICE, Elements.NECROMANCY); // Necromancy Wands
    public static final DeferredObject<Item> APPRENTICE_NECROMANCY_WAND = wand("wand_apprentice_necromancy", Tiers.APPRENTICE, Elements.NECROMANCY);
    public static final DeferredObject<Item> ADVANCED_NECROMANCY_WAND = wand("wand_advanced_necromancy", Tiers.ADVANCED, Elements.NECROMANCY);
    public static final DeferredObject<Item> MASTER_NECROMANCY_WAND = wand("wand_master_necromancy", Tiers.MASTER, Elements.NECROMANCY);

    public static final DeferredObject<Item> NOVICE_SORCERY_WAND = wand("wand_novice_sorcery", Tiers.NOVICE, Elements.SORCERY); // Sorcery Wands
    public static final DeferredObject<Item> APPRENTICE_SORCERY_WAND = wand("wand_apprentice_sorcery", Tiers.APPRENTICE, Elements.SORCERY);
    public static final DeferredObject<Item> ADVANCED_SORCERY_WAND = wand("wand_advanced_sorcery", Tiers.ADVANCED, Elements.SORCERY);
    public static final DeferredObject<Item> MASTER_SORCERY_WAND = wand("wand_master_sorcery", Tiers.MASTER, Elements.SORCERY);

    //Crystals
    public static final DeferredObject<Item> MAGIC_CRYSTAL_SHARD = item("magic_crystal_shard");
    public static final DeferredObject<Item> MAGIC_CRYSTAL = item("magic_crystal");
    public static final DeferredObject<Item> MAGIC_CRYSTAL_EARTH = item("magic_crystal_earth");
    public static final DeferredObject<Item> MAGIC_CRYSTAL_FIRE = item("magic_crystal_fire");
    public static final DeferredObject<Item> MAGIC_CRYSTAL_HEALING = item("magic_crystal_healing");
    public static final DeferredObject<Item> MAGIC_CRYSTAL_ICE = item("magic_crystal_ice");
    public static final DeferredObject<Item> MAGIC_CRYSTAL_LIGHTNING = item("magic_crystal_lightning");
    public static final DeferredObject<Item> MAGIC_CRYSTAL_NECROMANCY = item("magic_crystal_necromancy");
    public static final DeferredObject<Item> MAGIC_CRYSTAL_SORCERY = item("magic_crystal_sorcery");
    public static final DeferredObject<Item> MAGIC_CRYSTAL_GRAND = item("magic_crystal_grand");

    //Wand Upgrades
    public static final DeferredObject<Item> ATTUNEMENT_UPGRADE = item("upgrade_attunement");
    public static final DeferredObject<Item> BLAST_UPGRADE = item("upgrade_blast");
    public static final DeferredObject<Item> CONDENSER_UPGRADE = item("upgrade_condenser");
    public static final DeferredObject<Item> COOLDOWN_UPGRADE = item("upgrade_cooldown");
    public static final DeferredObject<Item> DURATION_UPGRADE = item("upgrade_duration");
    public static final DeferredObject<Item> MELEE_UPGRADE = item("upgrade_melee");
    public static final DeferredObject<Item> RANGE_UPGRADE = item("upgrade_range");
    public static final DeferredObject<Item> SIPHON_UPGRADE = item("upgrade_siphon");
    public static final DeferredObject<Item> STORAGE_UPGRADE = item("upgrade_storage");

    // Wizard Armors
    public static final DeferredObject<Item> WIZARD_HAT = armor("wizard_hat", WizardArmorType.WIZARD, ArmorItem.Type.HELMET, null);
    public static final DeferredObject<Item> WIZARD_ROBE = armor("wizard_robe", WizardArmorType.WIZARD, ArmorItem.Type.CHESTPLATE, null);
    public static final DeferredObject<Item> WIZARD_LEGGINGS = armor("wizard_leggings", WizardArmorType.WIZARD, ArmorItem.Type.LEGGINGS, null);
    public static final DeferredObject<Item> WIZARD_BOOTS = armor("wizard_boots", WizardArmorType.WIZARD, ArmorItem.Type.BOOTS, null);

    public static final DeferredObject<Item> WIZARD_HAT_EARTH = armor("wizard_hat_earth", WizardArmorType.WIZARD, ArmorItem.Type.HELMET, Elements.EARTH);
    public static final DeferredObject<Item> WIZARD_ROBE_EARTH = armor("wizard_robe_earth", WizardArmorType.WIZARD, ArmorItem.Type.CHESTPLATE, Elements.EARTH);
    public static final DeferredObject<Item> WIZARD_LEGGINGS_EARTH = armor("wizard_leggings_earth", WizardArmorType.WIZARD, ArmorItem.Type.LEGGINGS, Elements.EARTH);
    public static final DeferredObject<Item> WIZARD_BOOTS_EARTH = armor("wizard_boots_earth", WizardArmorType.WIZARD, ArmorItem.Type.BOOTS, Elements.EARTH);

    public static final DeferredObject<Item> WIZARD_HAT_FIRE = armor("wizard_hat_fire", WizardArmorType.WIZARD, ArmorItem.Type.HELMET, Elements.FIRE);
    public static final DeferredObject<Item> WIZARD_ROBE_FIRE = armor("wizard_robe_fire", WizardArmorType.WIZARD, ArmorItem.Type.CHESTPLATE, Elements.FIRE);
    public static final DeferredObject<Item> WIZARD_LEGGINGS_FIRE = armor("wizard_leggings_fire", WizardArmorType.WIZARD, ArmorItem.Type.LEGGINGS, Elements.FIRE);
    public static final DeferredObject<Item> WIZARD_BOOTS_FIRE = armor("wizard_boots_fire", WizardArmorType.WIZARD, ArmorItem.Type.BOOTS, Elements.FIRE);

    public static final DeferredObject<Item> WIZARD_HAT_HEALING = armor("wizard_hat_healing", WizardArmorType.WIZARD, ArmorItem.Type.HELMET, Elements.HEALING);
    public static final DeferredObject<Item> WIZARD_ROBE_HEALING = armor("wizard_robe_healing", WizardArmorType.WIZARD, ArmorItem.Type.CHESTPLATE, Elements.HEALING);
    public static final DeferredObject<Item> WIZARD_LEGGINGS_HEALING = armor("wizard_leggings_healing", WizardArmorType.WIZARD, ArmorItem.Type.LEGGINGS, Elements.HEALING);
    public static final DeferredObject<Item> WIZARD_BOOTS_HEALING = armor("wizard_boots_healing", WizardArmorType.WIZARD, ArmorItem.Type.BOOTS, Elements.HEALING);

    public static final DeferredObject<Item> WIZARD_HAT_ICE = armor("wizard_hat_ice", WizardArmorType.WIZARD, ArmorItem.Type.HELMET, Elements.ICE);
    public static final DeferredObject<Item> WIZARD_ROBE_ICE = armor("wizard_robe_ice", WizardArmorType.WIZARD, ArmorItem.Type.CHESTPLATE, Elements.ICE);
    public static final DeferredObject<Item> WIZARD_LEGGINGS_ICE = armor("wizard_leggings_ice", WizardArmorType.WIZARD, ArmorItem.Type.LEGGINGS, Elements.ICE);
    public static final DeferredObject<Item> WIZARD_BOOTS_ICE = armor("wizard_boots_ice", WizardArmorType.WIZARD, ArmorItem.Type.BOOTS, Elements.ICE);

    public static final DeferredObject<Item> WIZARD_HAT_LIGHTNING = armor("wizard_hat_lightning", WizardArmorType.WIZARD, ArmorItem.Type.HELMET, Elements.LIGHTNING);
    public static final DeferredObject<Item> WIZARD_ROBE_LIGHTNING = armor("wizard_robe_lightning", WizardArmorType.WIZARD, ArmorItem.Type.CHESTPLATE, Elements.LIGHTNING);
    public static final DeferredObject<Item> WIZARD_LEGGINGS_LIGHTNING = armor("wizard_leggings_lightning", WizardArmorType.WIZARD, ArmorItem.Type.LEGGINGS, Elements.LIGHTNING);
    public static final DeferredObject<Item> WIZARD_BOOTS_LIGHTNING = armor("wizard_boots_lightning", WizardArmorType.WIZARD, ArmorItem.Type.BOOTS, Elements.LIGHTNING);

    public static final DeferredObject<Item> WIZARD_HAT_NECROMANCY = armor("wizard_hat_necromancy", WizardArmorType.WIZARD, ArmorItem.Type.HELMET, Elements.NECROMANCY);
    public static final DeferredObject<Item> WIZARD_ROBE_NECROMANCY = armor("wizard_robe_necromancy", WizardArmorType.WIZARD, ArmorItem.Type.CHESTPLATE, Elements.NECROMANCY);
    public static final DeferredObject<Item> WIZARD_LEGGINGS_NECROMANCY = armor("wizard_leggings_necromancy", WizardArmorType.WIZARD, ArmorItem.Type.LEGGINGS, Elements.NECROMANCY);
    public static final DeferredObject<Item> WIZARD_BOOTS_NECROMANCY = armor("wizard_boots_necromancy", WizardArmorType.WIZARD, ArmorItem.Type.BOOTS, Elements.NECROMANCY);

    public static final DeferredObject<Item> WIZARD_HAT_SORCERY = armor("wizard_hat_sorcery", WizardArmorType.WIZARD, ArmorItem.Type.HELMET, Elements.SORCERY);
    public static final DeferredObject<Item> WIZARD_ROBE_SORCERY = armor("wizard_robe_sorcery", WizardArmorType.WIZARD, ArmorItem.Type.CHESTPLATE, Elements.SORCERY);
    public static final DeferredObject<Item> WIZARD_LEGGINGS_SORCERY = armor("wizard_leggings_sorcery", WizardArmorType.WIZARD, ArmorItem.Type.LEGGINGS, Elements.SORCERY);
    public static final DeferredObject<Item> WIZARD_BOOTS_SORCERY = armor("wizard_boots_sorcery", WizardArmorType.WIZARD, ArmorItem.Type.BOOTS, Elements.SORCERY);

    public static final DeferredObject<Item> SAGE_HAT = armor("sage_hat", WizardArmorType.SAGE, ArmorItem.Type.HELMET, null);
    public static final DeferredObject<Item> SAGE_ROBE = armor("sage_robe", WizardArmorType.SAGE, ArmorItem.Type.CHESTPLATE, null);
    public static final DeferredObject<Item> SAGE_LEGGINGS = armor("sage_leggings", WizardArmorType.SAGE, ArmorItem.Type.LEGGINGS, null);
    public static final DeferredObject<Item> SAGE_BOOTS = armor("sage_boots", WizardArmorType.SAGE, ArmorItem.Type.BOOTS, null);

    public static final DeferredObject<Item> SAGE_HAT_EARTH = armor("sage_hat_earth", WizardArmorType.SAGE, ArmorItem.Type.HELMET, Elements.EARTH);
    public static final DeferredObject<Item> SAGE_ROBE_EARTH = armor("sage_robe_earth", WizardArmorType.SAGE, ArmorItem.Type.CHESTPLATE, Elements.EARTH);
    public static final DeferredObject<Item> SAGE_LEGGINGS_EARTH = armor("sage_leggings_earth", WizardArmorType.SAGE, ArmorItem.Type.LEGGINGS, Elements.EARTH);
    public static final DeferredObject<Item> SAGE_BOOTS_EARTH = armor("sage_boots_earth", WizardArmorType.SAGE, ArmorItem.Type.BOOTS, Elements.EARTH);

    public static final DeferredObject<Item> SAGE_HAT_FIRE = armor("sage_hat_fire", WizardArmorType.SAGE, ArmorItem.Type.HELMET, Elements.FIRE);
    public static final DeferredObject<Item> SAGE_ROBE_FIRE = armor("sage_robe_fire", WizardArmorType.SAGE, ArmorItem.Type.CHESTPLATE, Elements.FIRE);
    public static final DeferredObject<Item> SAGE_LEGGINGS_FIRE = armor("sage_leggings_fire", WizardArmorType.SAGE, ArmorItem.Type.LEGGINGS, Elements.FIRE);
    public static final DeferredObject<Item> SAGE_BOOTS_FIRE = armor("sage_boots_fire", WizardArmorType.SAGE, ArmorItem.Type.BOOTS, Elements.FIRE);

    public static final DeferredObject<Item> SAGE_HAT_HEALING = armor("sage_hat_healing", WizardArmorType.SAGE, ArmorItem.Type.HELMET, Elements.HEALING);
    public static final DeferredObject<Item> SAGE_ROBE_HEALING = armor("sage_robe_healing", WizardArmorType.SAGE, ArmorItem.Type.CHESTPLATE, Elements.HEALING);
    public static final DeferredObject<Item> SAGE_LEGGINGS_HEALING = armor("sage_leggings_healing", WizardArmorType.SAGE, ArmorItem.Type.LEGGINGS, Elements.HEALING);
    public static final DeferredObject<Item> SAGE_BOOTS_HEALING = armor("sage_boots_healing", WizardArmorType.SAGE, ArmorItem.Type.BOOTS, Elements.HEALING);

    public static final DeferredObject<Item> SAGE_HAT_ICE = armor("sage_hat_ice", WizardArmorType.SAGE, ArmorItem.Type.HELMET, Elements.ICE);
    public static final DeferredObject<Item> SAGE_ROBE_ICE = armor("sage_robe_ice", WizardArmorType.SAGE, ArmorItem.Type.CHESTPLATE, Elements.ICE);
    public static final DeferredObject<Item> SAGE_LEGGINGS_ICE = armor("sage_leggings_ice", WizardArmorType.SAGE, ArmorItem.Type.LEGGINGS, Elements.ICE);
    public static final DeferredObject<Item> SAGE_BOOTS_ICE = armor("sage_boots_ice", WizardArmorType.SAGE, ArmorItem.Type.BOOTS, Elements.ICE);

    public static final DeferredObject<Item> SAGE_HAT_LIGHTNING = armor("sage_hat_lightning", WizardArmorType.SAGE, ArmorItem.Type.HELMET, Elements.LIGHTNING);
    public static final DeferredObject<Item> SAGE_ROBE_LIGHTNING = armor("sage_robe_lightning", WizardArmorType.SAGE, ArmorItem.Type.CHESTPLATE, Elements.LIGHTNING);
    public static final DeferredObject<Item> SAGE_LEGGINGS_LIGHTNING = armor("sage_leggings_lightning", WizardArmorType.SAGE, ArmorItem.Type.LEGGINGS, Elements.LIGHTNING);
    public static final DeferredObject<Item> SAGE_BOOTS_LIGHTNING = armor("sage_boots_lightning", WizardArmorType.SAGE, ArmorItem.Type.BOOTS, Elements.LIGHTNING);

    public static final DeferredObject<Item> SAGE_HAT_NECROMANCY = armor("sage_hat_necromancy", WizardArmorType.SAGE, ArmorItem.Type.HELMET, Elements.NECROMANCY);
    public static final DeferredObject<Item> SAGE_ROBE_NECROMANCY = armor("sage_robe_necromancy", WizardArmorType.SAGE, ArmorItem.Type.CHESTPLATE, Elements.NECROMANCY);
    public static final DeferredObject<Item> SAGE_LEGGINGS_NECROMANCY = armor("sage_leggings_necromancy", WizardArmorType.SAGE, ArmorItem.Type.LEGGINGS, Elements.NECROMANCY);
    public static final DeferredObject<Item> SAGE_BOOTS_NECROMANCY = armor("sage_boots_necromancy", WizardArmorType.SAGE, ArmorItem.Type.BOOTS, Elements.NECROMANCY);

    public static final DeferredObject<Item> SAGE_HAT_SORCERY = armor("sage_hat_sorcery", WizardArmorType.SAGE, ArmorItem.Type.HELMET, Elements.SORCERY);
    public static final DeferredObject<Item> SAGE_ROBE_SORCERY = armor("sage_robe_sorcery", WizardArmorType.SAGE, ArmorItem.Type.CHESTPLATE, Elements.SORCERY);
    public static final DeferredObject<Item> SAGE_LEGGINGS_SORCERY = armor("sage_leggings_sorcery", WizardArmorType.SAGE, ArmorItem.Type.LEGGINGS, Elements.SORCERY);
    public static final DeferredObject<Item> SAGE_BOOTS_SORCERY = armor("sage_boots_sorcery", WizardArmorType.SAGE, ArmorItem.Type.BOOTS, Elements.SORCERY);

    //Warlock Armors
    public static final DeferredObject<Item> WARLOCK_HOOD = armor("warlock_hood", WizardArmorType.WARLOCK, ArmorItem.Type.HELMET, null);
    public static final DeferredObject<Item> WARLOCK_ROBE = armor("warlock_robe", WizardArmorType.WARLOCK, ArmorItem.Type.CHESTPLATE, null);
    public static final DeferredObject<Item> WARLOCK_LEGGINGS = armor("warlock_leggings", WizardArmorType.WARLOCK, ArmorItem.Type.LEGGINGS, null);
    public static final DeferredObject<Item> WARLOCK_BOOTS = armor("warlock_boots", WizardArmorType.WARLOCK, ArmorItem.Type.BOOTS, null);

    public static final DeferredObject<Item> WARLOCK_HOOD_EARTH = armor("warlock_hood_earth", WizardArmorType.WARLOCK, ArmorItem.Type.HELMET, Elements.EARTH); //Earth Warlock Armor
    public static final DeferredObject<Item> WARLOCK_ROBE_EARTH = armor("warlock_robe_earth", WizardArmorType.WARLOCK, ArmorItem.Type.CHESTPLATE, Elements.EARTH);
    public static final DeferredObject<Item> WARLOCK_LEGGINGS_EARTH = armor("warlock_leggings_earth", WizardArmorType.WARLOCK, ArmorItem.Type.LEGGINGS, Elements.EARTH);
    public static final DeferredObject<Item> WARLOCK_BOOTS_EARTH = armor("warlock_boots_earth", WizardArmorType.WARLOCK, ArmorItem.Type.BOOTS, Elements.EARTH);

    public static final DeferredObject<Item> WARLOCK_HOOD_FIRE = armor("warlock_hood_fire", WizardArmorType.WARLOCK, ArmorItem.Type.BOOTS, Elements.EARTH); //Fire Warlock Armor
    public static final DeferredObject<Item> WARLOCK_ROBE_FIRE = armor("warlock_robe_fire", WizardArmorType.WARLOCK, ArmorItem.Type.BOOTS, Elements.EARTH);
    public static final DeferredObject<Item> WARLOCK_LEGGINGS_FIRE = armor("warlock_leggings_fire", WizardArmorType.WARLOCK, ArmorItem.Type.BOOTS, Elements.EARTH);
    public static final DeferredObject<Item> WARLOCK_BOOTS_FIRE = armor("warlock_boots_fire", WizardArmorType.WARLOCK, ArmorItem.Type.BOOTS, Elements.EARTH);

    public static final DeferredObject<Item> WARLOCK_HOOD_HEALING = armor("warlock_hood_healing", WizardArmorType.WARLOCK, ArmorItem.Type.HELMET, Elements.HEALING); //Healing Warlock Armor
    public static final DeferredObject<Item> WARLOCK_ROBE_HEALING = armor("warlock_robe_healing", WizardArmorType.WARLOCK, ArmorItem.Type.CHESTPLATE, Elements.HEALING);
    public static final DeferredObject<Item> WARLOCK_LEGGINGS_HEALING = armor("warlock_leggings_healing", WizardArmorType.WARLOCK, ArmorItem.Type.LEGGINGS, Elements.HEALING);
    public static final DeferredObject<Item> WARLOCK_BOOTS_HEALING = armor("warlock_boots_healing", WizardArmorType.WARLOCK, ArmorItem.Type.BOOTS, Elements.HEALING);

    public static final DeferredObject<Item> WARLOCK_HOOD_ICE = armor("warlock_hood_ice", WizardArmorType.WARLOCK, ArmorItem.Type.HELMET, Elements.ICE); //Ice Warlock Armor
    public static final DeferredObject<Item> WARLOCK_ROBE_ICE = armor("warlock_robe_ice", WizardArmorType.WARLOCK, ArmorItem.Type.CHESTPLATE, Elements.ICE);
    public static final DeferredObject<Item> WARLOCK_LEGGINGS_ICE = armor("warlock_leggings_ice", WizardArmorType.WARLOCK, ArmorItem.Type.LEGGINGS, Elements.ICE);
    public static final DeferredObject<Item> WARLOCK_BOOTS_ICE = armor("warlock_boots_ice", WizardArmorType.WARLOCK, ArmorItem.Type.BOOTS, Elements.ICE);

    public static final DeferredObject<Item> WARLOCK_HOOD_LIGHTNING = armor("warlock_hood_lightning", WizardArmorType.WARLOCK, ArmorItem.Type.HELMET, Elements.LIGHTNING); //Lightning Warlock Armor
    public static final DeferredObject<Item> WARLOCK_ROBE_LIGHTNING = armor("warlock_robe_lightning", WizardArmorType.WARLOCK, ArmorItem.Type.CHESTPLATE, Elements.LIGHTNING);
    public static final DeferredObject<Item> WARLOCK_LEGGINGS_LIGHTNING = armor("warlock_leggings_lightning", WizardArmorType.WARLOCK, ArmorItem.Type.LEGGINGS, Elements.LIGHTNING);
    public static final DeferredObject<Item> WARLOCK_BOOTS_LIGHTNING = armor("warlock_boots_lightning", WizardArmorType.WARLOCK, ArmorItem.Type.BOOTS, Elements.LIGHTNING);

    public static final DeferredObject<Item> WARLOCK_HOOD_NECROMANCY = armor("warlock_hood_necromancy", WizardArmorType.WARLOCK, ArmorItem.Type.HELMET, Elements.NECROMANCY); //Necromancy Warlock Armor
    public static final DeferredObject<Item> WARLOCK_ROBE_NECROMANCY = armor("warlock_robe_necromancy", WizardArmorType.WARLOCK, ArmorItem.Type.CHESTPLATE, Elements.NECROMANCY);
    public static final DeferredObject<Item> WARLOCK_LEGGINGS_NECROMANCY = armor("warlock_leggings_necromancy", WizardArmorType.WARLOCK, ArmorItem.Type.LEGGINGS, Elements.NECROMANCY);
    public static final DeferredObject<Item> WARLOCK_BOOTS_NECROMANCY = armor("warlock_boots_necromancy", WizardArmorType.WARLOCK, ArmorItem.Type.BOOTS, Elements.NECROMANCY);

    public static final DeferredObject<Item> WARLOCK_HOOD_SORCERY = armor("warlock_hood_sorcery", WizardArmorType.WARLOCK, ArmorItem.Type.HELMET, Elements.SORCERY); //Sorcery Warlock Armor
    public static final DeferredObject<Item> WARLOCK_ROBE_SORCERY = armor("warlock_robe_sorcery", WizardArmorType.WARLOCK, ArmorItem.Type.CHESTPLATE, Elements.SORCERY);
    public static final DeferredObject<Item> WARLOCK_LEGGINGS_SORCERY = armor("warlock_leggings_sorcery", WizardArmorType.WARLOCK, ArmorItem.Type.LEGGINGS, Elements.SORCERY);
    public static final DeferredObject<Item> WARLOCK_BOOTS_SORCERY = armor("warlock_boots_sorcery", WizardArmorType.WARLOCK, ArmorItem.Type.BOOTS, Elements.SORCERY);

    //Battle Mage Armors
    public static final DeferredObject<Item> BATTLEMAGE_HELMET = armor("battlemage_helmet", WizardArmorType.WARLOCK, ArmorItem.Type.HELMET, null);
    public static final DeferredObject<Item> BATTLEMAGE_CHESTPLATE = armor("battlemage_chestplate", WizardArmorType.WARLOCK, ArmorItem.Type.CHESTPLATE, null);
    public static final DeferredObject<Item> BATTLEMAGE_LEGGINGS = armor("battlemage_leggings", WizardArmorType.WARLOCK, ArmorItem.Type.LEGGINGS, null);
    public static final DeferredObject<Item> BATTLEMAGE_BOOTS = armor("battlemage_boots", WizardArmorType.WARLOCK, ArmorItem.Type.BOOTS, null);

    public static final DeferredObject<Item> BATTLEMAGE_HELMET_EARTH = armor("battlemage_helmet_earth", WizardArmorType.WARLOCK, ArmorItem.Type.HELMET, Elements.EARTH);
    public static final DeferredObject<Item> BATTLEMAGE_CHESTPLATE_EARTH = armor("battlemage_chestplate_earth", WizardArmorType.WARLOCK, ArmorItem.Type.CHESTPLATE, Elements.EARTH);
    public static final DeferredObject<Item> BATTLEMAGE_LEGGINGS_EARTH = armor("battlemage_leggings_earth", WizardArmorType.WARLOCK, ArmorItem.Type.LEGGINGS, Elements.EARTH);
    public static final DeferredObject<Item> BATTLEMAGE_BOOTS_EARTH = armor("battlemage_boots_earth", WizardArmorType.WARLOCK, ArmorItem.Type.BOOTS, Elements.EARTH);

    public static final DeferredObject<Item> BATTLEMAGE_HELMET_FIRE = armor("battlemage_helmet_fire", WizardArmorType.WARLOCK, ArmorItem.Type.HELMET, Elements.FIRE);
    public static final DeferredObject<Item> BATTLEMAGE_CHESTPLATE_FIRE = armor("battlemage_chestplate_fire", WizardArmorType.WARLOCK, ArmorItem.Type.CHESTPLATE, Elements.FIRE);
    public static final DeferredObject<Item> BATTLEMAGE_LEGGINGS_FIRE = armor("battlemage_leggings_fire", WizardArmorType.WARLOCK, ArmorItem.Type.LEGGINGS, Elements.FIRE);
    public static final DeferredObject<Item> BATTLEMAGE_BOOTS_FIRE = armor("battlemage_boots_fire", WizardArmorType.WARLOCK, ArmorItem.Type.BOOTS, Elements.FIRE);

    public static final DeferredObject<Item> BATTLEMAGE_HELMET_HEALING = armor("battlemage_helmet_healing", WizardArmorType.WARLOCK, ArmorItem.Type.HELMET, Elements.HEALING);
    public static final DeferredObject<Item> BATTLEMAGE_CHESTPLATE_HEALING = armor("battlemage_chestplate_healing", WizardArmorType.WARLOCK, ArmorItem.Type.CHESTPLATE, Elements.HEALING);
    public static final DeferredObject<Item> BATTLEMAGE_LEGGINGS_HEALING = armor("battlemage_leggings_healing", WizardArmorType.WARLOCK, ArmorItem.Type.LEGGINGS, Elements.HEALING);
    public static final DeferredObject<Item> BATTLEMAGE_BOOTS_HEALING = armor("battlemage_boots_healing", WizardArmorType.WARLOCK, ArmorItem.Type.BOOTS, Elements.HEALING);

    public static final DeferredObject<Item> BATTLEMAGE_HELMET_ICE = armor("battlemage_helmet_ice", WizardArmorType.WARLOCK, ArmorItem.Type.HELMET, Elements.ICE);
    public static final DeferredObject<Item> BATTLEMAGE_CHESTPLATE_ICE = armor("battlemage_chestplate_ice", WizardArmorType.WARLOCK, ArmorItem.Type.CHESTPLATE, Elements.ICE);
    public static final DeferredObject<Item> BATTLEMAGE_LEGGINGS_ICE = armor("battlemage_leggings_ice", WizardArmorType.WARLOCK, ArmorItem.Type.LEGGINGS, Elements.ICE);
    public static final DeferredObject<Item> BATTLEMAGE_BOOTS_ICE = armor("battlemage_boots_ice", WizardArmorType.WARLOCK, ArmorItem.Type.BOOTS, Elements.ICE);

    public static final DeferredObject<Item> BATTLEMAGE_HELMET_LIGHTNING = armor("battlemage_helmet_lightning", WizardArmorType.WARLOCK, ArmorItem.Type.HELMET, Elements.LIGHTNING);
    public static final DeferredObject<Item> BATTLEMAGE_CHESTPLATE_LIGHTNING = armor("battlemage_chestplate_lightning", WizardArmorType.WARLOCK, ArmorItem.Type.CHESTPLATE, Elements.LIGHTNING);
    public static final DeferredObject<Item> BATTLEMAGE_LEGGINGS_LIGHTNING = armor("battlemage_leggings_lightning", WizardArmorType.WARLOCK, ArmorItem.Type.LEGGINGS, Elements.LIGHTNING);
    public static final DeferredObject<Item> BATTLEMAGE_BOOTS_LIGHTNING = armor("battlemage_boots_lightning", WizardArmorType.WARLOCK, ArmorItem.Type.BOOTS, Elements.LIGHTNING);

    public static final DeferredObject<Item> BATTLEMAGE_HELMET_NECROMANCY = armor("battlemage_helmet_necromancy", WizardArmorType.WARLOCK, ArmorItem.Type.HELMET, Elements.NECROMANCY);
    public static final DeferredObject<Item> BATTLEMAGE_CHESTPLATE_NECROMANCY = armor("battlemage_chestplate_necromancy", WizardArmorType.WARLOCK, ArmorItem.Type.CHESTPLATE, Elements.NECROMANCY);
    public static final DeferredObject<Item> BATTLEMAGE_LEGGINGS_NECROMANCY = armor("battlemage_leggings_necromancy", WizardArmorType.WARLOCK, ArmorItem.Type.LEGGINGS, Elements.NECROMANCY);
    public static final DeferredObject<Item> BATTLEMAGE_BOOTS_NECROMANCY = armor("battlemage_boots_necromancy", WizardArmorType.WARLOCK, ArmorItem.Type.BOOTS, Elements.NECROMANCY);

    public static final DeferredObject<Item> BATTLEMAGE_HELMET_SORCERY = armor("battlemage_helmet_sorcery", WizardArmorType.WARLOCK, ArmorItem.Type.HELMET, Elements.SORCERY);
    public static final DeferredObject<Item> BATTLEMAGE_CHESTPLATE_SORCERY = armor("battlemage_chestplate_sorcery", WizardArmorType.WARLOCK, ArmorItem.Type.CHESTPLATE, Elements.SORCERY);
    public static final DeferredObject<Item> BATTLEMAGE_LEGGINGS_SORCERY = armor("battlemage_leggings_sorcery", WizardArmorType.WARLOCK, ArmorItem.Type.LEGGINGS, Elements.SORCERY);
    public static final DeferredObject<Item> BATTLEMAGE_BOOTS_SORCERY = armor("battlemage_boots_sorcery", WizardArmorType.WARLOCK, ArmorItem.Type.BOOTS, Elements.SORCERY);

    //Amulets
    public static final DeferredObject<Item> AMULET_ABSORPTION = artifact("amulet_absorption");
    public static final DeferredObject<Item> AMULET_ANCHORING = artifact("amulet_anchoring");
    public static final DeferredObject<Item> AMULET_ARCANE_DEFENCE = artifact("amulet_arcane_defence");
    public static final DeferredObject<Item> AMULET_AUTO_SHIELD = artifact("amulet_auto_shield");
    public static final DeferredObject<Item> AMULET_BANISHING = artifact("amulet_banishing");
    public static final DeferredObject<Item> AMULET_CHANNELING = artifact("amulet_channeling");
    public static final DeferredObject<Item> AMULET_FIRE_CLOAKING = artifact("amulet_fire_cloaking");
    public static final DeferredObject<Item> AMULET_FIRE_PROTECTION = artifact("amulet_fire_protection");
    public static final DeferredObject<Item> AMULET_FROST_WARDING = artifact("amulet_frost_warding");
    public static final DeferredObject<Item> AMULET_GLIDE = artifact("amulet_glide");
    public static final DeferredObject<Item> AMULET_ICE_IMMUNITY = artifact("amulet_ice_immunity");
    public static final DeferredObject<Item> AMULET_ICE_PROTECTION = artifact("amulet_ice_protection");
    public static final DeferredObject<Item> AMULET_LICH = artifact("amulet_lich");
    public static final DeferredObject<Item> AMULET_POTENTIAL = artifact("amulet_potential");
    public static final DeferredObject<Item> AMULET_RECOVERY = artifact("amulet_recovery");
    public static final DeferredObject<Item> AMULET_RESURRECTION = artifact("amulet_resurrection");
    public static final DeferredObject<Item> AMULET_TRANSIENCE = artifact("amulet_transience");
    public static final DeferredObject<Item> AMULET_WARDING = artifact("amulet_warding");
    public static final DeferredObject<Item> AMULET_WISDOM = artifact("amulet_wisdom");
    public static final DeferredObject<Item> AMULET_WITHER_IMMUNITY = artifact("amulet_wither_immunity");

    //Charms
    public static final DeferredObject<Item> CHARM_ABSEILING = artifact("charm_abseiling");
    public static final DeferredObject<Item> CHARM_AUTO_SMELT = artifact("charm_auto_smelt");
    public static final DeferredObject<Item> CHARM_BLACK_HOLE = artifact("charm_black_hole");
    public static final DeferredObject<Item> CHARM_EXPERIENCE_TOME = artifact("charm_experience_tome");
    public static final DeferredObject<Item> CHARM_FEEDING = artifact("charm_feeding");
    public static final DeferredObject<Item> CHARM_FLIGHT = artifact("charm_flight");
    public static final DeferredObject<Item> CHARM_GROWTH = artifact("charm_growth");
    public static final DeferredObject<Item> CHARM_HAGGLER = artifact("charm_haggler");
    public static final DeferredObject<Item> CHARM_HUNGER_CASTING = artifact("charm_hunger_casting");
    public static final DeferredObject<Item> CHARM_LAVA_WALKING = artifact("charm_lava_walking");
    public static final DeferredObject<Item> CHARM_LIGHT = artifact("charm_light");
    public static final DeferredObject<Item> CHARM_MINION_HEALTH = artifact("charm_minion_health");
    public static final DeferredObject<Item> CHARM_MINION_VARIANTS = artifact("charm_minion_variants");
    public static final DeferredObject<Item> CHARM_MOUNT_TELEPORTING = artifact("charm_mount_teleporting");
    public static final DeferredObject<Item> CHARM_MOVE_SPEED = artifact("charm_move_speed");
    public static final DeferredObject<Item> CHARM_SILK_TOUCH = artifact("charm_silk_touch");
    public static final DeferredObject<Item> CHARM_SIXTH_SENSE = artifact("charm_sixth_sense");
    public static final DeferredObject<Item> CHARM_SPELL_DISCOVERY = artifact("charm_spell_discovery");
    public static final DeferredObject<Item> CHARM_STOP_TIME = artifact("charm_stop_time");
    public static final DeferredObject<Item> CHARM_STORM = artifact("charm_storm");
    public static final DeferredObject<Item> CHARM_TRANSPORTATION = artifact("charm_transportation");
    public static final DeferredObject<Item> CHARM_UNDEAD_HELMETS = artifact("charm_undead_helmets");

    //Rings
    public static final DeferredObject<Item> RING_ARCANE_FROST = artifact("ring_arcane_frost");
    public static final DeferredObject<Item> RING_BATTLEMAGE = artifact("ring_battlemage");
    public static final DeferredObject<Item> RING_BLOCKWRANGLER = artifact("ring_blockwrangler");
    public static final DeferredObject<Item> RING_COMBUSTION = artifact("ring_combustion");
    public static final DeferredObject<Item> RING_CONDENSING = artifact("ring_condensing");
    public static final DeferredObject<Item> RING_CONJURER = artifact("ring_conjurer");
    public static final DeferredObject<Item> RING_DEFENDER = artifact("ring_defender");
    public static final DeferredObject<Item> RING_DISINTEGRATION = artifact("ring_disintegration");
    public static final DeferredObject<Item> RING_EARTH_BIOME = artifact("ring_earth_biome");
    public static final DeferredObject<Item> RING_EARTH_MELEE = artifact("ring_earth_melee");
    public static final DeferredObject<Item> RING_EVOKER = artifact("ring_evoker");
    public static final DeferredObject<Item> RING_EXTRACTION = artifact("ring_extraction");
    public static final DeferredObject<Item> RING_FIRE_BIOME = artifact("ring_fire_biome");
    public static final DeferredObject<Item> RING_FIRE_MELEE = artifact("ring_fire_melee");
    public static final DeferredObject<Item> RING_FULL_MOON = artifact("ring_full_moon");
    public static final DeferredObject<Item> RING_HAMMER = artifact("ring_hammer");
    public static final DeferredObject<Item> RING_ICE_BIOME = artifact("ring_ice_biome");
    public static final DeferredObject<Item> RING_ICE_MELEE = artifact("ring_ice_melee");
    public static final DeferredObject<Item> RING_INTERDICTION = artifact("ring_interdiction");
    public static final DeferredObject<Item> RING_LEECHING = artifact("ring_leeching");
    public static final DeferredObject<Item> RING_LIGHTNING_MELEE = artifact("ring_lightning_melee");
    public static final DeferredObject<Item> RING_MANA_RETURN = artifact("ring_mana_return");
    public static final DeferredObject<Item> RING_METEOR = artifact("ring_meteor");
    public static final DeferredObject<Item> RING_MIND_CONTROL = artifact("ring_mind_control");
    public static final DeferredObject<Item> RING_NECROMANCY_MELEE = artifact("ring_necromancy_melee");
    public static final DeferredObject<Item> RING_PALADIN = artifact("ring_paladin");
    public static final DeferredObject<Item> RING_POISON = artifact("ring_poison");
    public static final DeferredObject<Item> RING_SEEKING = artifact("ring_seeking");
    public static final DeferredObject<Item> RING_SHATTERING = artifact("ring_shattering");
    public static final DeferredObject<Item> RING_SIPHONING = artifact("ring_siphoning");
    public static final DeferredObject<Item> RING_SOULBINDING = artifact("ring_soulbinding");
    public static final DeferredObject<Item> RING_STORM = artifact("ring_storm");
    public static final DeferredObject<Item> RING_STORMCLOUD = artifact("ring_stormcloud");

    //Spectral Armor
    public static final DeferredObject<Item> SPECTRAL_HELMET = item("spectral_helmet");
    public static final DeferredObject<Item> SPECTRAL_CHESTPLATE = item("spectral_chestplate");
    public static final DeferredObject<Item> SPECTRAL_LEGGINGS = item("spectral_leggings");
    public static final DeferredObject<Item> SPECTRAL_BOOTS = item("spectral_boots");

    //Spectral Weapons
    public static final DeferredObject<Item> SPECTRAL_SWORD = item("spectral_sword");
    public static final DeferredObject<Item> SPECTRAL_BOW = item("spectral_bow", false);

    //Spectral Tools
    public static final DeferredObject<Item> SPECTRAL_PICKAXE = item("spectral_pickaxe");

    //Cast Items
    public static final DeferredObject<Item> FLAMECATCHER = item("flamecatcher", false);
    public static final DeferredObject<Item> FLAMING_AXE = item("flaming_axe");
    public static final DeferredObject<Item> FROST_AXE = item("frost_axe");
    public static final DeferredObject<Item> LIGHTNING_HAMMER = item("lightning_hammer", false);




    // ======= Registry =======
    public static void register(RegisterFunction<Item> function){
        ITEMS.forEach(((id, item) -> {
            function.register(BuiltInRegistries.ITEM, WizardryMainMod.location(id), item.get());
        }));
    }

    public static LinkedList<DeferredObject<? extends Item>> getArmors() {
        return ARMORS;
    }
    public static LinkedList<DeferredObject<? extends Item>> getLeggings() {
        return LEGGINGS;
    }

    // ======= Helpers =======
    static DeferredObject<Item> crystal(String name){
        return item(name, CrystalItem::new);
    }

    static DeferredObject<Item> armorUpgrade(String name){
        return item(name, () -> new ArmorUpgradeItem(new Item.Properties().stacksTo(16)));
    }

    static DeferredObject<Item> armor(String name, WizardArmorType wizardArmorType, ArmorItem.Type type, Element element){
        return armor(name, () -> new WizardArmorItem(wizardArmorType, type, element), type);
    }

    static <T extends Item> DeferredObject<T> armor(String name, Supplier<T> sup, ArmorItem.Type type){
        var registeredArmor = item(name, sup);
        if(type == ArmorItem.Type.LEGGINGS) LEGGINGS.add(registeredArmor);
        ARMORS.add(registeredArmor);
        return registeredArmor;
    }

    static DeferredObject<Item> wand(String name, Tier tier, Element element){
        return wand(name, () -> new WandItem(tier, element), true);
    }

    static <T extends Item> DeferredObject<T> wand(String name, Supplier<T> sup, boolean defaultModel){
        var registeredWand = item(name, sup, false);
        WANDS.add(registeredWand);
        if(defaultModel) DataGenProcessor.get().addWandItem(name, registeredWand);
        return registeredWand;
    }

    static DeferredObject<Item> artifact(String name){
        return artifact(name, () -> new Item(new Item.Properties()));
    }

    static <T extends Item> DeferredObject<T> artifact(String name, Supplier<T> sup){
        var registeredArtifact = item(name, sup);
        ARTIFACTS.add(registeredArtifact);
        return registeredArtifact;
    }

    static DeferredObject<Item> item(String name) {
        return item(name, () -> new Item(new Item.Properties()));
    }

    static DeferredObject<Item> item(String name, boolean defaultModel) {
        return item(name, () -> new Item(new Item.Properties()), defaultModel);
    }

    static <T extends Item> DeferredObject<T> item(String name, Supplier<T> itemSupplier) {
        return item(name, itemSupplier, true);
    }

    static <T extends Item> DeferredObject<T> item(String name, Supplier<T> itemSupplier, boolean defaultModel) {
        var ret = new DeferredObject<>(itemSupplier);
        ITEMS.put(name, ret);
        if(defaultModel) DataGenProcessor.get().addDefaultItem(name, ret);
        return ret;
    }
}
