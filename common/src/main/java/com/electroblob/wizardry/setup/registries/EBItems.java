package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import com.electroblob.wizardry.content.item.*;
import com.electroblob.wizardry.content.item.artifact.*;
import com.electroblob.wizardry.content.spell.abstr.ConjureItemSpell;
import com.electroblob.wizardry.content.spell.necromancy.Banish;
import com.electroblob.wizardry.content.spell.sorcery.ImbueWeapon;
import com.electroblob.wizardry.core.IArtefactEffect;
import com.electroblob.wizardry.core.QuickArtefactEffect;
import com.electroblob.wizardry.core.integrations.EBAccessoriesIntegration;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.datagen.EBDataGenProcessor;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Supplier;

import static com.electroblob.wizardry.core.ArtifactUtils.handleLightningEffect;
import static com.electroblob.wizardry.core.ArtifactUtils.meleeRing;


/**
 * The registration of all wizardry items, sorted by category for helping with creative tabs <br><br>
 * Sorted by:
 * <ul>
 *     <li>General Items</li>
 *     <li>Flasks</li>
 *     <li>Spectral Dust</li>
 *     <li>Wands</li>
 *     <li>Crystals</li>
 *     <li>Wand Upgrades</li>
 *     <li>Wizard Armor</li>
 *     <li>Artifacts</li>
 *     <li>Conjured (Spectral) Spell Cast Items</li>
 *     <li>Bombs</li>
 * </ul>
 */
@SuppressWarnings("unused")
public final class EBItems {
    public static final Tier MAGICAL = new Tier() {
        @Override
        public int getUses() {
            return 100;
        }

        @Override
        public float getSpeed() {
            return 8.0F;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }

        @Override
        public int getLevel() {
            return 3;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public float getAttackDamageBonus() {
            return 4.0F;
        }
    };
    static final LinkedList<DeferredObject<? extends Item>> ARMORS = new LinkedList<>();
    static final LinkedList<DeferredObject<? extends Item>> LEGGINGS = new LinkedList<>();
    static final LinkedList<DeferredObject<? extends Item>> WANDS = new LinkedList<>();
    static final LinkedList<DeferredObject<? extends Item>> ARTIFACTS = new LinkedList<>();
    static final LinkedList<DeferredObject<? extends Item>> GENERAL_ITEMS = new LinkedList<>(); // For main item tab
    static final Map<String, DeferredObject<? extends Item>> ITEMS_REGISTER = new HashMap<>(); // For register function
    //General Items
    public static final DeferredObject<Item> ARCANE_TOME = item("arcane_tome", ArcaneTomeItem::new, true, true);
    public static final DeferredObject<Item> BLANK_SCROLL = item("blank_scroll", () -> new BlankScrollItem(new Item.Properties().stacksTo(64)), true, true);
    public static final DeferredObject<Item> RUINED_SPELL_BOOK = item("ruined_spell_book");
    public static final DeferredObject<Item> SCROLL = item("scroll", () -> new ScrollItem(new Item.Properties().stacksTo(16)));
    public static final DeferredObject<Item> SPELL_BOOK = item("spell_book", () -> new SpellBookItem(new Item.Properties().stacksTo(16)));
    public static final DeferredObject<Item> WIZARD_HANDBOOK = item("wizard_handbook");
    public static final DeferredObject<Item> RANDOM_SPELL_BOOK = item("random_spell_book", () -> new RandomSpellBookItem(new Item.Properties().stacksTo(1)), true, false);
    public static final DeferredObject<Item> ASTRAL_DIAMOND = item("astral_diamond");
    public static final DeferredObject<Item> CRYSTAL_SILVER_PLATING = armorUpgrade("crystal_silver_plating");
    public static final DeferredObject<Item> ETHEREAL_CRYSTAL_WEAVE = armorUpgrade("ethereal_crystal_weave");
    public static final DeferredObject<Item> RESPLENDENT_THREAD = armorUpgrade("resplendent_thread");
    public static final DeferredObject<Item> IDENTIFICATION_SCROLL = item("identification_scroll", () -> new IdentificationScrollItem(new Item.Properties().stacksTo(16)), true, true);
    public static final DeferredObject<Item> MAGIC_SILK = item("magic_silk");
    public static final DeferredObject<Item> PURIFYING_ELIXIR = item("purifying_elixir");
    //Flasks
    public static final DeferredObject<Item> SMALL_MANA_FLASK = item("mana_flask_small");
    public static final DeferredObject<Item> MEDIUM_MANA_FLASK = item("mana_flask_medium");
    public static final DeferredObject<Item> LARGE_MANA_FLASK = item("mana_flask_large");
    //Spectral Dust
    public static final DeferredObject<Item> SPECTRAL_DUST = item("spectral_dust", () -> new SpectralDustItem(Elements.MAGIC), true, true);
    public static final DeferredObject<Item> SPECTRAL_DUST_EARTH = item("spectral_dust_earth", () -> new SpectralDustItem(Elements.EARTH), true, true);
    public static final DeferredObject<Item> SPECTRAL_DUST_FIRE = item("spectral_dust_fire", () -> new SpectralDustItem(Elements.FIRE), true, true);
    public static final DeferredObject<Item> SPECTRAL_DUST_HEALING = item("spectral_dust_healing", () -> new SpectralDustItem(Elements.HEALING), true, true);
    public static final DeferredObject<Item> SPECTRAL_DUST_ICE = item("spectral_dust_ice", () -> new SpectralDustItem(Elements.ICE), true, true);
    public static final DeferredObject<Item> SPECTRAL_DUST_LIGHTNING = item("spectral_dust_lightning", () -> new SpectralDustItem(Elements.LIGHTNING), true, true);
    public static final DeferredObject<Item> SPECTRAL_DUST_NECROMANCY = item("spectral_dust_necromancy", () -> new SpectralDustItem(Elements.NECROMANCY), true, true);
    public static final DeferredObject<Item> SPECTRAL_DUST_SORCERY = item("spectral_dust_sorcery", () -> new SpectralDustItem(Elements.SORCERY), true, true);
    //Wands
    public static final DeferredObject<Item> NOVICE_WAND = wand("wand_novice", SpellTiers.NOVICE, null);
    public static final DeferredObject<Item> APPRENTICE_WAND = wand("wand_apprentice", SpellTiers.APPRENTICE, null);
    public static final DeferredObject<Item> ADVANCED_WAND = wand("wand_advanced", SpellTiers.ADVANCED, null);
    public static final DeferredObject<Item> MASTER_WAND = wand("wand_master", SpellTiers.MASTER, null);
    public static final DeferredObject<Item> NOVICE_EARTH_WAND = wand("wand_novice_earth", SpellTiers.NOVICE, Elements.EARTH); // Earth Wands
    public static final DeferredObject<Item> APPRENTICE_EARTH_WAND = wand("wand_apprentice_earth", SpellTiers.APPRENTICE, Elements.EARTH);
    public static final DeferredObject<Item> ADVANCED_EARTH_WAND = wand("wand_advanced_earth", SpellTiers.ADVANCED, Elements.EARTH);
    public static final DeferredObject<Item> MASTER_EARTH_WAND = wand("wand_master_earth", SpellTiers.MASTER, Elements.EARTH);
    public static final DeferredObject<Item> NOVICE_FIRE_WAND = wand("wand_novice_fire", SpellTiers.NOVICE, Elements.FIRE); // Fire Wands
    public static final DeferredObject<Item> APPRENTICE_FIRE_WAND = wand("wand_apprentice_fire", SpellTiers.APPRENTICE, Elements.FIRE);
    public static final DeferredObject<Item> ADVANCED_FIRE_WAND = wand("wand_advanced_fire", SpellTiers.ADVANCED, Elements.FIRE);
    public static final DeferredObject<Item> MASTER_FIRE_WAND = wand("wand_master_fire", SpellTiers.MASTER, Elements.FIRE);
    public static final DeferredObject<Item> NOVICE_HEALING_WAND = wand("wand_novice_healing", SpellTiers.NOVICE, Elements.HEALING); // Healing Wands
    public static final DeferredObject<Item> APPRENTICE_HEALING_WAND = wand("wand_apprentice_healing", SpellTiers.APPRENTICE, Elements.HEALING);
    public static final DeferredObject<Item> ADVANCED_HEALING_WAND = wand("wand_advanced_healing", SpellTiers.ADVANCED, Elements.HEALING);
    public static final DeferredObject<Item> MASTER_HEALING_WAND = wand("wand_master_healing", SpellTiers.MASTER, Elements.HEALING);
    public static final DeferredObject<Item> NOVICE_ICE_WAND = wand("wand_novice_ice", SpellTiers.NOVICE, Elements.ICE); // Ice Wands
    public static final DeferredObject<Item> APPRENTICE_ICE_WAND = wand("wand_apprentice_ice", SpellTiers.APPRENTICE, Elements.ICE);
    public static final DeferredObject<Item> ADVANCED_ICE_WAND = wand("wand_advanced_ice", SpellTiers.ADVANCED, Elements.ICE);
    public static final DeferredObject<Item> MASTER_ICE_WAND = wand("wand_master_ice", SpellTiers.MASTER, Elements.ICE);
    public static final DeferredObject<Item> NOVICE_LIGHTNING_WAND = wand("wand_novice_lightning", SpellTiers.NOVICE, Elements.LIGHTNING); // Lightning Wands
    public static final DeferredObject<Item> APPRENTICE_LIGHTNING_WAND = wand("wand_apprentice_lightning", SpellTiers.APPRENTICE, Elements.LIGHTNING);
    public static final DeferredObject<Item> ADVANCED_LIGHTNING_WAND = wand("wand_advanced_lightning", SpellTiers.ADVANCED, Elements.LIGHTNING);
    public static final DeferredObject<Item> MASTER_LIGHTNING_WAND = wand("wand_master_lightning", SpellTiers.MASTER, Elements.LIGHTNING);
    public static final DeferredObject<Item> NOVICE_NECROMANCY_WAND = wand("wand_novice_necromancy", SpellTiers.NOVICE, Elements.NECROMANCY); // Necromancy Wands
    public static final DeferredObject<Item> APPRENTICE_NECROMANCY_WAND = wand("wand_apprentice_necromancy", SpellTiers.APPRENTICE, Elements.NECROMANCY);
    public static final DeferredObject<Item> ADVANCED_NECROMANCY_WAND = wand("wand_advanced_necromancy", SpellTiers.ADVANCED, Elements.NECROMANCY);
    public static final DeferredObject<Item> MASTER_NECROMANCY_WAND = wand("wand_master_necromancy", SpellTiers.MASTER, Elements.NECROMANCY);
    public static final DeferredObject<Item> NOVICE_SORCERY_WAND = wand("wand_novice_sorcery", SpellTiers.NOVICE, Elements.SORCERY); // Sorcery Wands
    public static final DeferredObject<Item> APPRENTICE_SORCERY_WAND = wand("wand_apprentice_sorcery", SpellTiers.APPRENTICE, Elements.SORCERY);
    public static final DeferredObject<Item> ADVANCED_SORCERY_WAND = wand("wand_advanced_sorcery", SpellTiers.ADVANCED, Elements.SORCERY);
    public static final DeferredObject<Item> MASTER_SORCERY_WAND = wand("wand_master_sorcery", SpellTiers.MASTER, Elements.SORCERY);
    //Crystals
    public static final DeferredObject<Item> MAGIC_CRYSTAL_SHARD = crystal("magic_crystal_shard");
    public static final DeferredObject<Item> MAGIC_CRYSTAL = crystal("magic_crystal");
    public static final DeferredObject<Item> MAGIC_CRYSTAL_EARTH = crystal("magic_crystal_earth");
    public static final DeferredObject<Item> MAGIC_CRYSTAL_FIRE = crystal("magic_crystal_fire");
    public static final DeferredObject<Item> MAGIC_CRYSTAL_HEALING = crystal("magic_crystal_healing");
    public static final DeferredObject<Item> MAGIC_CRYSTAL_ICE = crystal("magic_crystal_ice");
    public static final DeferredObject<Item> MAGIC_CRYSTAL_LIGHTNING = crystal("magic_crystal_lightning");
    public static final DeferredObject<Item> MAGIC_CRYSTAL_NECROMANCY = crystal("magic_crystal_necromancy");
    public static final DeferredObject<Item> MAGIC_CRYSTAL_SORCERY = crystal("magic_crystal_sorcery");
    public static final DeferredObject<Item> MAGIC_CRYSTAL_GRAND = crystal("magic_crystal_grand");
    //Wand Upgrades
    public static final DeferredObject<Item> ATTUNEMENT_UPGRADE = wandUpgrade("attunement_upgrade");
    public static final DeferredObject<Item> BLAST_UPGRADE = wandUpgrade("blast_upgrade");
    public static final DeferredObject<Item> CONDENSER_UPGRADE = wandUpgrade("condenser_upgrade");
    public static final DeferredObject<Item> COOLDOWN_UPGRADE = wandUpgrade("cooldown_upgrade");
    public static final DeferredObject<Item> RING_FULL_MOON = artifact("ring_full_moon", Rarity.RARE, QuickArtefactEffect.spellPreCast((e, s) -> e.getSpell().getElement() == Elements.EARTH && !e.getCaster().level().isDay() && e.getCaster().level().getMoonPhase() == 0, (e, s) -> e.getModifiers().set(EBItems.COOLDOWN_UPGRADE.get(), e.getModifiers().get(EBItems.COOLDOWN_UPGRADE.get()) * 0.3f, false)));
    public static final DeferredObject<Item> RING_STORM = artifact("ring_storm", Rarity.RARE, QuickArtefactEffect.spellPreCast((e, s) -> e.getSpell().getElement() == Elements.LIGHTNING && e.getLevel().isThundering(), (e, s) -> e.getModifiers().set(EBItems.COOLDOWN_UPGRADE.get(), e.getModifiers().get(EBItems.COOLDOWN_UPGRADE.get()) * 0.3f, false)));
    public static final DeferredObject<Item> DURATION_UPGRADE = wandUpgrade("duration_upgrade");
    public static final DeferredObject<Item> RING_CONJURER = artifact("ring_conjurer", Rarity.RARE, QuickArtefactEffect.spellPreCast((e, s) -> e.getSpell() instanceof ConjureItemSpell, (e, s) -> e.getModifiers().set(EBItems.DURATION_UPGRADE.get(), e.getModifiers().get(EBItems.DURATION_UPGRADE.get()) * 2, false)));
    public static final DeferredObject<Item> MELEE_UPGRADE = wandUpgrade("melee_upgrade");
    public static final DeferredObject<Item> RANGE_UPGRADE = wandUpgrade("range_upgrade");
    public static final DeferredObject<Item> SIPHON_UPGRADE = wandUpgrade("siphon_upgrade");
    public static final DeferredObject<Item> STORAGE_UPGRADE = wandUpgrade("storage_upgrade");
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
    // Charms
    public static final DeferredObject<Item> CHARM_ABSEILING = artifact("charm_abseiling", Rarity.RARE);
    public static final DeferredObject<Item> CHARM_AUTO_SMELT = artifact("charm_auto_smelt", Rarity.RARE);
    public static final DeferredObject<Item> CHARM_BLACK_HOLE = artifact("charm_black_hole", Rarity.EPIC);
    public static final DeferredObject<Item> CHARM_EXPERIENCE_TOME = artifact("charm_experience_tome", Rarity.EPIC, QuickArtefactEffect.spellPreCast((e, s) -> true, (e, s) -> e.getModifiers().set(SpellModifiers.PROGRESSION, e.getModifiers().get(SpellModifiers.PROGRESSION) * 1.5f, false)));
    public static final DeferredObject<Item> CHARM_FEEDING = artifact("charm_feeding", Rarity.UNCOMMON, new FeedingCharmEffect());
    public static final DeferredObject<Item> CHARM_FLIGHT = artifact("charm_flight", Rarity.RARE, QuickArtefactEffect.spellPreCast((e, s) -> e.getSpell() == Spells.FLIGHT, (e, s) -> e.getModifiers().set(SpellModifiers.POTENCY, 1.5f * e.getModifiers().get(SpellModifiers.POTENCY), true)));
    public static final DeferredObject<Item> CHARM_GROWTH = artifact("charm_growth", Rarity.UNCOMMON);
    public static final DeferredObject<Item> CHARM_HAGGLER = artifact("charm_haggler", Rarity.RARE);
    public static final DeferredObject<Item> CHARM_HUNGER_CASTING = artifact("charm_hunger_casting", Rarity.RARE, new HungerCastingCharmEffect());
    public static final DeferredObject<Item> CHARM_LAVA_WALKING = artifact("charm_lava_walking", Rarity.EPIC);
    public static final DeferredObject<Item> CHARM_LIGHT = artifact("charm_light", Rarity.RARE);
    public static final DeferredObject<Item> CHARM_MINION_HEALTH = artifact("charm_minion_health", Rarity.UNCOMMON);
    public static final DeferredObject<Item> CHARM_MINION_VARIANTS = artifact("charm_minion_variants", Rarity.UNCOMMON);
    public static final DeferredObject<Item> CHARM_MOUNT_TELEPORTING = artifact("charm_mount_teleporting", Rarity.RARE);
    public static final DeferredObject<Item> CHARM_MOVE_SPEED = artifact("charm_move_speed", Rarity.RARE);
    public static final DeferredObject<Item> CHARM_SILK_TOUCH = artifact("charm_silk_touch", Rarity.EPIC);
    public static final DeferredObject<Item> CHARM_SIXTH_SENSE = artifact("charm_sixth_sense", Rarity.UNCOMMON);
    public static final DeferredObject<Item> CHARM_SPELL_DISCOVERY = artifact("charm_spell_discovery", Rarity.UNCOMMON);
    public static final DeferredObject<Item> CHARM_STOP_TIME = artifact("charm_stop_time", Rarity.EPIC);
    public static final DeferredObject<Item> CHARM_STORM = artifact("charm_storm", Rarity.RARE);
    public static final DeferredObject<Item> CHARM_TRANSPORTATION = artifact("charm_transportation", Rarity.RARE);
    public static final DeferredObject<Item> CHARM_UNDEAD_HELMETS = artifact("charm_undead_helmets", Rarity.RARE);
    // Amulets
    public static final DeferredObject<Item> AMULET_ABSORPTION = artifact("amulet_absorption", Rarity.EPIC);
    public static final DeferredObject<Item> AMULET_ANCHORING = artifact("amulet_anchoring", Rarity.RARE);
    public static final DeferredObject<Item> AMULET_ARCANE_DEFENCE = artifact("amulet_arcane_defence", Rarity.RARE, new ArcaneDefenseAmuletEffect());
    public static final DeferredObject<Item> AMULET_AUTO_SHIELD = artifact("amulet_auto_shield", Rarity.RARE, new AutoShieldAmuletEffect());
    public static final DeferredObject<Item> AMULET_BANISHING = artifact("amulet_banishing", Rarity.UNCOMMON, QuickArtefactEffect.hurtEntity((e, s) -> e.getSource().getEntity() instanceof Player player && player.level().random.nextFloat() < 0.2f && !e.getSource().isIndirect() && e.getSource().getDirectEntity() instanceof LivingEntity target, (e, s) -> ((Banish) Spells.BANISH).teleport((LivingEntity) e.getSource().getDirectEntity(), e.getSource().getDirectEntity().level(), 8 + e.getSource().getDirectEntity().level().random.nextDouble() * 8)));
    public static final DeferredObject<Item> AMULET_CHANNELING = artifact("amulet_channeling", Rarity.RARE, QuickArtefactEffect.hurtEntity((e, s) -> e.getSource().getEntity() instanceof Player player && player.level().random.nextFloat() < 0.3f && e.getSource().is(EBDamageSources.FROST), (e, s) -> e.setCanceled(true)));
    public static final DeferredObject<Item> AMULET_FIRE_CLOAKING = artifact("amulet_fire_cloaking", Rarity.RARE, new FireCloakingAmuletEffect());
    public static final DeferredObject<Item> AMULET_FIRE_PROTECTION = artifact("amulet_fire_protection", Rarity.UNCOMMON, QuickArtefactEffect.hurtEntity((e, s) -> e.getSource().is(EBDamageSources.FIRE), (e, s) -> e.setAmount(e.getAmount() * 0.7f)));
    public static final DeferredObject<Item> AMULET_FROST_WARDING = artifact("amulet_frost_warding", Rarity.RARE, new FrostWardingAmuletEffect());
    public static final DeferredObject<Item> AMULET_ICE_IMMUNITY = artifact("amulet_ice_immunity", Rarity.EPIC);
    public static final DeferredObject<Item> AMULET_ICE_PROTECTION = artifact("amulet_ice_protection", Rarity.UNCOMMON, QuickArtefactEffect.hurtEntity((e, s) -> e.getSource().getEntity() instanceof Player player && e.getSource().is(EBDamageSources.FROST), (e, s) -> e.setAmount(e.getAmount() * 0.7f)));
    public static final DeferredObject<Item> AMULET_LICH = artifact("amulet_lich", Rarity.UNCOMMON, new LichAmuletEffect());
    public static final DeferredObject<Item> AMULET_POTENTIAL = artifact("amulet_potential", Rarity.RARE, QuickArtefactEffect.hurtEntity((e, s) -> e.getSource().getEntity() instanceof Player player && player.level().random.nextFloat() < 0.2f && !e.getSource().isIndirect() && e.getSource().getDirectEntity() instanceof LivingEntity target, (e, s) -> handleLightningEffect(e.getSource().getEntity(), (LivingEntity) e.getSource().getDirectEntity(), e)));
    public static final DeferredObject<Item> AMULET_RECOVERY = artifact("amulet_recovery", Rarity.UNCOMMON);
    public static final DeferredObject<Item> AMULET_RESURRECTION = artifact("amulet_resurrection", Rarity.EPIC); // TODO PlayerDropsEvent
    public static final DeferredObject<Item> AMULET_TRANSIENCE = artifact("amulet_transience", Rarity.EPIC, QuickArtefactEffect.hurtEntity((e, s) -> e.getSource().getEntity() instanceof Player player && player.getHealth() <= 6 && player.level().random.nextFloat() < 0.25f, (e, s) -> ((Player) e.getSource().getEntity()).addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 300, 0, false, false))));
    public static final DeferredObject<Item> AMULET_WARDING = artifact("amulet_warding", Rarity.UNCOMMON, QuickArtefactEffect.hurtEntity((e, s) -> EBDamageSources.isMagic(e.getSource()), (e, s) -> e.setAmount(e.getAmount() * 0.9f)));
    public static final DeferredObject<Item> AMULET_WISDOM = artifact("amulet_wisdom", Rarity.RARE);
    public static final DeferredObject<Item> AMULET_WITHER_IMMUNITY = artifact("amulet_wither_immunity", Rarity.EPIC);
    // Rings
    public static final DeferredObject<Item> RING_ARCANE_FROST = artifact("ring_arcane_frost", Rarity.EPIC, new ArcaneFrostRingEffect());
    public static final DeferredObject<Item> RING_BATTLEMAGE = artifact("ring_battlemage", Rarity.UNCOMMON, QuickArtefactEffect.spellPreCast((e, s) -> e.getCaster() instanceof Player player && player.getOffhandItem().getItem() instanceof ISpellCastingItem && ImbueWeapon.isSword(player.getMainHandItem()), (e, s) -> e.getModifiers().set(SpellModifiers.POTENCY, 1.1f * e.getModifiers().get(SpellModifiers.POTENCY), false)));
    public static final DeferredObject<Item> RING_BLOCKWRANGLER = artifact("ring_blockwrangler", Rarity.RARE, QuickArtefactEffect.spellPreCast((e, s) -> e.getSpell() == Spells.GREATER_TELEKINESIS, (e, s) -> e.getModifiers().set(SpellModifiers.POTENCY, e.getModifiers().get(SpellModifiers.POTENCY) * 2, false)));
    public static final DeferredObject<Item> RING_COMBUSTION = artifact("ring_combustion", Rarity.EPIC, QuickArtefactEffect.death((e, s) -> e.getSource().typeHolder().is(EBDamageSources.FIRE.location()), (e, s) -> e.getEntity().level().explode(e.getEntity(), e.getEntity().xo, e.getEntity().yo, e.getEntity().zo, 1.5f, Level.ExplosionInteraction.NONE)));
    public static final DeferredObject<Item> RING_CONDENSING = artifact("ring_condensing", Rarity.RARE, new CondensingRingEffect());
    public static final DeferredObject<Item> RING_DEFENDER = artifact("ring_defender", Rarity.RARE);
    public static final DeferredObject<Item> RING_DISINTEGRATION = artifact("ring_disintegration", Rarity.RARE);
    public static final DeferredObject<Item> RING_EARTH_BIOME = artifact("ring_earth_biome", Rarity.UNCOMMON, QuickArtefactEffect.spellPreCast((e, s) -> e.getSpell().getElement() == Elements.EARTH && Services.PLATFORM.inEarthBiomes(e.getCaster().level().getBiome(e.getCaster().blockPosition())), (e, s) -> e.getModifiers().set(SpellModifiers.POTENCY, 1.3f * e.getModifiers().get(SpellModifiers.POTENCY), false)));
    public static final DeferredObject<Item> RING_EARTH_MELEE = artifact("ring_earth_melee", Rarity.UNCOMMON, QuickArtefactEffect.hurtEntity((e, s) -> meleeRing(e.getSource(), Elements.EARTH), (e, s) -> e.getDamagedEntity().addEffect(new MobEffectInstance(MobEffects.POISON, 200))));
    public static final DeferredObject<Item> RING_EVOKER = artifact("ring_evoker", Rarity.RARE);
    public static final DeferredObject<Item> RING_EXTRACTION = artifact("ring_extraction", Rarity.UNCOMMON, new ExtractionRingEffect());
    public static final DeferredObject<Item> RING_FIRE_BIOME = artifact("ring_fire_biome", Rarity.UNCOMMON, QuickArtefactEffect.spellPreCast((e, s) -> e.getSpell().getElement() == Elements.FIRE && Services.PLATFORM.intHotBiomes(e.getCaster().level().getBiome(e.getCaster().blockPosition())), (e, s) -> e.getModifiers().set(SpellModifiers.POTENCY, 1.3f * e.getModifiers().get(SpellModifiers.POTENCY), false)));
    public static final DeferredObject<Item> RING_FIRE_MELEE = artifact("ring_fire_melee", Rarity.UNCOMMON, QuickArtefactEffect.hurtEntity((e, s) -> meleeRing(e.getSource(), Elements.FIRE), (e, s) -> e.getDamagedEntity().setSecondsOnFire(5)));
    public static final DeferredObject<Item> RING_HAMMER = artifact("ring_hammer", Rarity.EPIC);
    public static final DeferredObject<Item> RING_ICE_BIOME = artifact("ring_ice_biome", Rarity.UNCOMMON, QuickArtefactEffect.spellPreCast((e, s) -> e.getSpell().getElement() == Elements.ICE && Services.PLATFORM.inIceBiomes(e.getCaster().level().getBiome(e.getCaster().blockPosition())), (e, s) -> e.getModifiers().set(SpellModifiers.POTENCY, 1.3f * e.getModifiers().get(SpellModifiers.POTENCY), false)));
    public static final DeferredObject<Item> RING_ICE_MELEE = artifact("ring_ice_melee", Rarity.UNCOMMON, QuickArtefactEffect.hurtEntity((e, s) -> meleeRing(e.getSource(), Elements.ICE), (e, s) -> e.getDamagedEntity().addEffect(new MobEffectInstance(EBMobEffects.FROST.get(), 200))));
    public static final DeferredObject<Item> RING_INTERDICTION = artifact("ring_interdiction", Rarity.EPIC);
    public static final DeferredObject<Item> RING_LEECHING = artifact("ring_leeching", Rarity.RARE, new LeechingRingEffect());
    public static final DeferredObject<Item> RING_LIGHTNING_MELEE = artifact("ring_lightning_melee", Rarity.UNCOMMON, new LightningMeleeEffect());
    public static final DeferredObject<Item> RING_MANA_RETURN = artifact("ring_mana_return", Rarity.EPIC);
    public static final DeferredObject<Item> RING_METEOR = artifact("ring_meteor", Rarity.EPIC);
    public static final DeferredObject<Item> RING_MIND_CONTROL = artifact("ring_mind_control", Rarity.RARE);
    public static final DeferredObject<Item> RING_NECROMANCY_MELEE = artifact("ring_necromancy_melee", Rarity.UNCOMMON, QuickArtefactEffect.hurtEntity((e, s) -> meleeRing(e.getSource(), Elements.NECROMANCY), (e, s) -> e.getDamagedEntity().addEffect(new MobEffectInstance(MobEffects.WITHER, 200))));
    public static final DeferredObject<Item> RING_PALADIN = artifact("ring_paladin", Rarity.UNCOMMON, new PaladinRingEffect());
    public static final DeferredObject<Item> RING_POISON = artifact("ring_poison", Rarity.RARE, QuickArtefactEffect.hurtEntity((e, s) -> e.getSource().getEntity() instanceof Player player && e.getSource().is(EBDamageSources.POISON), (e, s) -> e.getDamagedEntity().addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0))));
    public static final DeferredObject<Item> RING_SEEKING = artifact("ring_seeking", Rarity.EPIC);
    public static final DeferredObject<Item> RING_SHATTERING = artifact("ring_shattering", Rarity.RARE, new ShatteringRingEffect());
    public static final DeferredObject<Item> RING_SIPHONING = artifact("ring_siphoning", Rarity.UNCOMMON);
    public static final DeferredObject<Item> RING_SOULBINDING = artifact("ring_soulbinding", Rarity.EPIC, new SoulBindingRingEffect());
    public static final DeferredObject<Item> RING_STORMCLOUD = artifact("ring_stormcloud", Rarity.RARE);
    //Spectral Armor
    public static final DeferredObject<Item> SPECTRAL_HELMET = item("spectral_helmet", () -> new SpectralArmorItem(ArmorItem.Type.HELMET));
    public static final DeferredObject<Item> SPECTRAL_CHESTPLATE = item("spectral_chestplate", () -> new SpectralArmorItem(ArmorItem.Type.CHESTPLATE));
    public static final DeferredObject<Item> SPECTRAL_LEGGINGS = item("spectral_leggings", () -> new SpectralArmorItem(ArmorItem.Type.LEGGINGS));
    public static final DeferredObject<Item> SPECTRAL_BOOTS = item("spectral_boots", () -> new SpectralArmorItem(ArmorItem.Type.BOOTS));
    //Spectral
    public static final DeferredObject<Item> SPECTRAL_SWORD = item("spectral_sword", SpectralSwordItem::new, false, true);
    public static final DeferredObject<Item> SPECTRAL_BOW = item("spectral_bow", SpectralBowItem::new, false, true);
    public static final DeferredObject<Item> SPECTRAL_PICKAXE = item("spectral_pickaxe", SpectralPickaxeItem::new);
    //Cast Items
    public static final DeferredObject<Item> FLAMECATCHER = item("flamecatcher", FlameCatcherItem::new, false, true);
    public static final DeferredObject<Item> FLAMING_AXE = item("flaming_axe", FlamingAxeItem::new, false, true);
    public static final DeferredObject<Item> FROST_AXE = item("frost_axe", FrostAxeItem::new, false, true);
    public static final DeferredObject<Item> LIGHTNING_HAMMER = item("lightning_hammer", () -> new Item(new Item.Properties().stacksTo(1)), false, true);
    //Bombs
    public static final DeferredObject<Item> FIREBOMB = item("firebomb", () -> new BombItem<>(EBEntities.FIRE_BOMB, EBSounds.ENTITY_FIREBOMB_THROW), true, true);
    public static final DeferredObject<Item> POISON_BOMB = item("poison_bomb", () -> new BombItem<>(EBEntities.POISON_BOMB, EBSounds.ENTITY_POISON_BOMB_THROW), true, true);
    public static final DeferredObject<Item> SMOKE_BOMB = item("smoke_bomb", () -> new BombItem<>(EBEntities.SMOKE_BOMB, EBSounds.ENTITY_SMOKE_BOMB_THROW), true, true);
    public static final DeferredObject<Item> SPARK_BOMB = item("spark_bomb", () -> new BombItem<>(EBEntities.SPARK_BOMB, EBSounds.ENTITY_SPARK_BOMB_THROW), true, false);
    // Spawn egg
    public static final DeferredObject<Item> WIZARD_SPAWN_EGG = item("wizard_spawn_egg", () -> new SpawnEggItem(EBEntities.WIZARD.get(), 0x19295e, 0xee9312, new Item.Properties()), false, true);
    public static final DeferredObject<Item> EVIL_WIZARD_SPAWN_EGG = item("evil_wizard_spawn_egg", () -> new SpawnEggItem(EBEntities.EVIL_WIZARD.get(), 0x290404, 0xee9312, new Item.Properties()), false, true);
    public static final DeferredObject<Item> REMNANT_SPAWN_EGG = item("remnant_spawn_egg", () -> new SpawnEggItem(EBEntities.REMNANT.get(), 0x414141, 0xe5daae, new Item.Properties()), false, true);

    public static final DeferredObject<Item> RECEPTACLE = item("receptacle", () -> new StandingAndWallBlockItem(EBBlocks.RECEPTACLE.get(), EBBlocks.WALL_RECEPTACLE.get(), new Item.Properties(), Direction.DOWN), false, false);
    private EBItems() {
    }

    // ======= Registry =======
    public static void register(RegisterFunction<Item> function) {
        ITEMS_REGISTER.forEach(((id, item) -> {
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

    /**
     * Add crystals with a default model and inside the item creative tab
     */
    static DeferredObject<Item> crystal(String name) {
        return item(name, CrystalItem::new, true, true);
    }

    /**
     * Add armor upgrades with a default model and inside the item creative tab
     */
    static DeferredObject<Item> armorUpgrade(String name) {
        return item(name, () -> new ArmorUpgradeItem(new Item.Properties().stacksTo(16)), true, true);
    }

    /**
     * Add wand upgrades with a default model and inside the item creative tab
     */
    static DeferredObject<Item> wandUpgrade(String name) {
        return item(name, () -> new WandUpgradeItem(new Item.Properties().stacksTo(16)), true, true);
    }

    /**
     * Add armor with a default model and not inside the item creative tab
     */
    static DeferredObject<Item> armor(String name, WizardArmorType wizardArmorType, ArmorItem.Type type, Element element) {
        return armor(name, () -> new WizardArmorItem(wizardArmorType, type, element), type);
    }

    /**
     * Add armor with a default model and not inside the item creative tab
     */
    static <T extends Item> DeferredObject<T> armor(String name, Supplier<T> sup, ArmorItem.Type type) {
        var registeredArmor = item(name, sup, true, false);
        if (type == ArmorItem.Type.LEGGINGS) LEGGINGS.add(registeredArmor);
        ARMORS.add(registeredArmor);
        return registeredArmor;
    }

    /**
     * Add wands with a default model and not inside the item creative tab
     */
    static DeferredObject<Item> wand(String name, SpellTier tier, Element element) {
        return wand(name, () -> new WandItem(tier, element), true);
    }

    /**
     * Add wands with a default model and not inside the item creative tab
     */
    static <T extends Item> DeferredObject<T> wand(String name, Supplier<T> sup, boolean defaultModel) {
        var registeredWand = item(name, sup, false, false);
        WANDS.add(registeredWand);
        if (defaultModel) EBDataGenProcessor.addWandItem(name, registeredWand);
        return registeredWand;
    }

    static DeferredObject<Item> artifact(String name, Rarity rarity, IArtefactEffect effect) {
        return artifact(name, () -> EBAccessoriesIntegration.getSafeArtifact(rarity, effect));
    }

    /**
     * Add artifacts with a default model and with a safe-dependency check
     */
    static DeferredObject<Item> artifact(String name, Rarity rarity) {
        return artifact(name, () -> EBAccessoriesIntegration.getSafeArtifact(rarity, null));
    }

    /**
     * Add artifacts with a default model and not inside the item creative tab
     */
    static <T extends Item> DeferredObject<T> artifact(String name, Supplier<T> sup) {
        var registeredArtifact = item(name, sup, true, false);
        ARTIFACTS.add(registeredArtifact);
        return registeredArtifact;
    }

    // Basically just a temp method to add items without any functional use
    static DeferredObject<Item> item(String name) {
        return item(name, () -> new Item(new Item.Properties()), true, true);
    }

    static <T extends Item> DeferredObject<T> item(String name, Supplier<T> itemSupplier) {
        return item(name, itemSupplier, true, false);
    }

    static <T extends Item> DeferredObject<T> item(String name, Supplier<T> itemSupplier, boolean defaultModel, boolean defaultTab) {
        var ret = new DeferredObject<>(itemSupplier);
        ITEMS_REGISTER.put(name, ret);
        if (defaultTab) GENERAL_ITEMS.add(ret);
        if (defaultModel) EBDataGenProcessor.addDefaultItem(name, ret);
        return ret;
    }
}
