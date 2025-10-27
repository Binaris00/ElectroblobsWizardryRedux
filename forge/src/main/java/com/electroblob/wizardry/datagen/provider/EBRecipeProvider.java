package com.electroblob.wizardry.datagen.provider;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.core.ImbuementAltarRecipeBuilder;
import com.electroblob.wizardry.datagen.help.ArmorData;
import com.electroblob.wizardry.datagen.help.ElementData;
import com.electroblob.wizardry.setup.registries.EBBlocks;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.EBTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class EBRecipeProvider extends RecipeProvider {
    public EBRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        lectern(EBBlocks.ACACIA_LECTERN.get(), EBBlocks.GILDED_ACACIA_WOOD.get(), consumer);
        lectern(EBBlocks.BIRCH_LECTERN.get(), EBBlocks.GILDED_BIRCH_WOOD.get(), consumer);
        lectern(EBBlocks.OAK_LECTERN.get(), EBBlocks.GILDED_OAK_WOOD.get(), consumer);
        lectern(EBBlocks.JUNGLE_LECTERN.get(), EBBlocks.GILDED_JUNGLE_WOOD.get(), consumer);
        lectern(EBBlocks.SPRUCE_LECTERN.get(), EBBlocks.GILDED_SPRUCE_WOOD.get(), consumer);
        lectern(EBBlocks.DARK_OAK_LECTERN.get(), EBBlocks.GILDED_DARK_OAK_WOOD.get(), consumer);

        bookshelf(EBBlocks.ACACIA_BOOKSHELF.get(), EBBlocks.GILDED_ACACIA_WOOD.get(), consumer);
        bookshelf(EBBlocks.BIRCH_BOOKSHELF.get(), EBBlocks.GILDED_BIRCH_WOOD.get(), consumer);
        bookshelf(EBBlocks.OAK_BOOKSHELF.get(), EBBlocks.GILDED_OAK_WOOD.get(), consumer);
        bookshelf(EBBlocks.JUNGLE_BOOKSHELF.get(), EBBlocks.GILDED_JUNGLE_WOOD.get(), consumer);
        bookshelf(EBBlocks.SPRUCE_BOOKSHELF.get(), EBBlocks.GILDED_SPRUCE_WOOD.get(), consumer);
        bookshelf(EBBlocks.DARK_OAK_BOOKSHELF.get(), EBBlocks.GILDED_DARK_OAK_WOOD.get(), consumer);

        wand(EBItems.NOVICE_WAND.get(), EBItems.MAGIC_CRYSTAL.get(), consumer);
        wand(EBItems.NOVICE_SORCERY_WAND.get(), EBItems.MAGIC_CRYSTAL_SORCERY.get(), consumer);
        wand(EBItems.NOVICE_EARTH_WAND.get(), EBItems.MAGIC_CRYSTAL_EARTH.get(), consumer);
        wand(EBItems.NOVICE_FIRE_WAND.get(), EBItems.MAGIC_CRYSTAL_FIRE.get(), consumer);
        wand(EBItems.NOVICE_HEALING_WAND.get(), EBItems.MAGIC_CRYSTAL_HEALING.get(), consumer);
        wand(EBItems.NOVICE_ICE_WAND.get(), EBItems.MAGIC_CRYSTAL_ICE.get(), consumer);
        wand(EBItems.NOVICE_LIGHTNING_WAND.get(), EBItems.MAGIC_CRYSTAL_LIGHTNING.get(), consumer);
        wand(EBItems.NOVICE_NECROMANCY_WAND.get(), EBItems.MAGIC_CRYSTAL_NECROMANCY.get(), consumer);

        runestone(EBBlocks.FIRE_RUNESTONE.get(), EBItems.MAGIC_CRYSTAL_FIRE.get(), consumer);
        runestone(EBBlocks.SORCERY_RUNESTONE.get(), EBItems.MAGIC_CRYSTAL_SORCERY.get(), consumer);
        runestone(EBBlocks.EARTH_RUNESTONE.get(), EBItems.MAGIC_CRYSTAL_EARTH.get(), consumer);
        runestone(EBBlocks.HEALING_RUNESTONE.get(), EBItems.MAGIC_CRYSTAL_HEALING.get(), consumer);
        runestone(EBBlocks.ICE_RUNESTONE.get(), EBItems.MAGIC_CRYSTAL_ICE.get(), consumer);
        runestone(EBBlocks.LIGHTNING_RUNESTONE.get(), EBItems.MAGIC_CRYSTAL_LIGHTNING.get(), consumer);
        runestone(EBBlocks.NECROMANCY_RUNESTONE.get(), EBItems.MAGIC_CRYSTAL_NECROMANCY.get(), consumer);

        runestonePedestal(EBBlocks.FIRE_RUNESTONE_PEDESTAL.get(), EBItems.MAGIC_CRYSTAL_FIRE.get(), consumer);
        runestonePedestal(EBBlocks.SORCERY_RUNESTONE_PEDESTAL.get(), EBItems.MAGIC_CRYSTAL_SORCERY.get(), consumer);
        runestonePedestal(EBBlocks.EARTH_RUNESTONE_PEDESTAL.get(), EBItems.MAGIC_CRYSTAL_EARTH.get(), consumer);
        runestonePedestal(EBBlocks.HEALING_RUNESTONE_PEDESTAL.get(), EBItems.MAGIC_CRYSTAL_HEALING.get(), consumer);
        runestonePedestal(EBBlocks.ICE_RUNESTONE_PEDESTAL.get(), EBItems.MAGIC_CRYSTAL_ICE.get(), consumer);
        runestonePedestal(EBBlocks.LIGHTNING_RUNESTONE_PEDESTAL.get(), EBItems.MAGIC_CRYSTAL_LIGHTNING.get(), consumer);
        runestonePedestal(EBBlocks.NECROMANCY_RUNESTONE_PEDESTAL.get(), EBItems.MAGIC_CRYSTAL_NECROMANCY.get(), consumer);

        gildedWood(EBBlocks.GILDED_ACACIA_WOOD.get(), Blocks.ACACIA_PLANKS, consumer);
        gildedWood(EBBlocks.GILDED_JUNGLE_WOOD.get(), Blocks.JUNGLE_PLANKS, consumer);
        gildedWood(EBBlocks.GILDED_SPRUCE_WOOD.get(), Blocks.SPRUCE_PLANKS, consumer);
        gildedWood(EBBlocks.GILDED_OAK_WOOD.get(), Blocks.OAK_PLANKS, consumer);
        gildedWood(EBBlocks.GILDED_BIRCH_WOOD.get(), Blocks.BIRCH_PLANKS, consumer);
        gildedWood(EBBlocks.GILDED_DARK_OAK_WOOD.get(), Blocks.DARK_OAK_PLANKS, consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, EBItems.WIZARD_HAT.get()).define('X', EBItems.MAGIC_SILK.get()).pattern("XXX").pattern("X X").unlockedBy("has_magic_silk", has(EBItems.MAGIC_SILK.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, EBItems.WIZARD_ROBE.get()).define('X', EBItems.MAGIC_SILK.get()).pattern("X X").pattern("XXX").pattern("XXX").unlockedBy("has_magic_silk", has(EBItems.MAGIC_SILK.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, EBItems.WIZARD_LEGGINGS.get()).define('X', EBItems.MAGIC_SILK.get()).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_magic_silk", has(EBItems.MAGIC_SILK.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, EBItems.WIZARD_BOOTS.get()).define('X', EBItems.MAGIC_SILK.get()).pattern("X X").pattern("X X").unlockedBy("has_magic_silk", has(EBItems.MAGIC_SILK.get())).save(consumer);

        nineBlockStorageRecipesRecipesWithCustomUnpacking(consumer, RecipeCategory.MISC, EBItems.MAGIC_CRYSTAL.get(),
                RecipeCategory.BUILDING_BLOCKS, EBBlocks.MAGIC_CRYSTAL_BLOCK.get(), "magic_crystal_from_magic_crystal_block", "magic_crystal");
        nineBlockStorageRecipesRecipesWithCustomUnpacking(consumer, RecipeCategory.MISC, EBItems.MAGIC_CRYSTAL_FIRE.get(),
                RecipeCategory.BUILDING_BLOCKS, EBBlocks.FIRE_CRYSTAL_BLOCK.get(), "magic_crystal_fire_from_magic_crystal_fire_block", "magic_crystal_fire");
        nineBlockStorageRecipesRecipesWithCustomUnpacking(consumer, RecipeCategory.MISC, EBItems.MAGIC_CRYSTAL_EARTH.get(),
                RecipeCategory.BUILDING_BLOCKS, EBBlocks.EARTH_CRYSTAL_BLOCK.get(), "magic_crystal_earth_from_magic_crystal_earth_block", "magic_crystal_earth");
        nineBlockStorageRecipesRecipesWithCustomUnpacking(consumer, RecipeCategory.MISC, EBItems.MAGIC_CRYSTAL_ICE.get(),
                RecipeCategory.BUILDING_BLOCKS, EBBlocks.ICE_CRYSTAL_BLOCK.get(), "magic_crystal_ice_from_magic_crystal_ice_block", "magic_crystal_ice");
        nineBlockStorageRecipesRecipesWithCustomUnpacking(consumer, RecipeCategory.MISC, EBItems.MAGIC_CRYSTAL_HEALING.get(),
                RecipeCategory.BUILDING_BLOCKS, EBBlocks.HEALING_CRYSTAL_BLOCK.get(), "magic_crystal_healing_from_magic_crystal_healing_block", "magic_crystal_healing");
        nineBlockStorageRecipesRecipesWithCustomUnpacking(consumer, RecipeCategory.MISC, EBItems.MAGIC_CRYSTAL_LIGHTNING.get(),
                RecipeCategory.BUILDING_BLOCKS, EBBlocks.LIGHTNING_CRYSTAL_BLOCK.get(), "magic_crystal_lightning_from_magic_crystal_lightning_block", "magic_crystal_lightning");
        nineBlockStorageRecipesRecipesWithCustomUnpacking(consumer, RecipeCategory.MISC, EBItems.MAGIC_CRYSTAL_SORCERY.get(),
                RecipeCategory.BUILDING_BLOCKS, EBBlocks.SORCERY_CRYSTAL_BLOCK.get(), "magic_crystal_sorcery_from_magic_crystal_sorcery_block", "magic_crystal_sorcery");
        nineBlockStorageRecipesRecipesWithCustomUnpacking(consumer, RecipeCategory.MISC, EBItems.MAGIC_CRYSTAL_NECROMANCY.get(),
                RecipeCategory.BUILDING_BLOCKS, EBBlocks.NECROMANCY_CRYSTAL_BLOCK.get(), "magic_crystal_necromancy_from_magic_crystal_necromancy_block", "magic_crystal_necromancy");

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EBBlocks.TRANSPORTATION_STONE.get())
                .define('x', EBTags.MAGIC_CRYSTAL_ITEM)
                .define('y', Blocks.STONE)
                .pattern(" x ")
                .pattern("xyx")
                .pattern(" x ")
                .unlockedBy("has_magic_crystal", has(EBTags.MAGIC_CRYSTAL_ITEM))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EBBlocks.ARCANE_WORKBENCH.get())
                .define('v', Items.GOLD_NUGGET)
                .define('w', ItemTags.WOOL_CARPETS)
                .define('x', EBTags.MAGIC_CRYSTAL_ITEM)
                .define('y', Items.LAPIS_BLOCK)
                .define('z', Items.STONE)
                .pattern("vwv").pattern("xyx").pattern("zzz")
                .unlockedBy("has_crystal", has(EBTags.MAGIC_CRYSTAL_ITEM)).save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EBItems.BLANK_SCROLL.get())
                .requires(Items.PAPER)
                .requires(Items.STRING)
                .unlockedBy("has_paper", has(Items.PAPER)).save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EBItems.MAGIC_CRYSTAL.get(), 2)
                .requires(EBBlocks.CRYSTAL_FLOWER.get())
                .unlockedBy("has_crystal", has(EBTags.MAGIC_CRYSTAL_ITEM)).save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EBItems.MAGIC_CRYSTAL_SHARD.get(), 9)
                .requires(EBItems.MAGIC_CRYSTAL.get())
                .unlockedBy("has_crystal", has(EBTags.MAGIC_CRYSTAL_ITEM)).save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EBItems.FIREBOMB.get(), 3)
                .requires(Items.BLAZE_POWDER, 2)
                .requires(Items.GLASS_BOTTLE)
                .requires(Items.GUNPOWDER)
                .unlockedBy("has_gunpowder", has(Items.GUNPOWDER)).save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EBItems.SPARK_BOMB.get(), 3)
                .requires(EBItems.MAGIC_CRYSTAL_LIGHTNING.get(), 2)
                .requires(Items.GLASS_BOTTLE)
                .requires(Items.GUNPOWDER)
                .unlockedBy("has_gunpowder", has(Items.GUNPOWDER)).save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EBItems.SMOKE_BOMB.get(), 3)
                .requires(Items.COAL, 2)
                .requires(Items.CHARCOAL, 2)
                .requires(Items.GLASS_BOTTLE)
                .requires(Items.GUNPOWDER)
                .unlockedBy("has_gunpowder", has(Items.GUNPOWDER)).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EBItems.MAGIC_SILK.get(), 2)
                .pattern(" x ")
                .pattern("xyx")
                .pattern(" x ")
                .define('x', Items.STRING)
                .define('y', EBTags.MAGIC_CRYSTAL_ITEM)
                .unlockedBy("has_magic_crystal", has(EBTags.MAGIC_CRYSTAL_ITEM))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EBItems.MEDIUM_MANA_FLASK.get())
                .pattern(" y ")
                .pattern("yxy")
                .pattern(" y ")
                .define('x', Items.GLASS_BOTTLE)
                .define('y', EBTags.MAGIC_CRYSTAL_ITEM)
                .unlockedBy("has_magic_crystal", has(EBTags.MAGIC_CRYSTAL_ITEM))
                .save(consumer);

//        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, spellBook())
//                .pattern(" x ")
//                .pattern("xyx")
//                .pattern(" x ")
//                .define('x', EBItems.MAGIC_CRYSTAL.get())
//                .define('y', Items.BOOK)
//                .unlockedBy("has_magic_crystal", has(EBItems.MAGIC_CRYSTAL.get()))
//                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EBItems.LARGE_MANA_FLASK.get())
                .pattern(" y ")
                .pattern("yxy")
                .pattern(" y ")
                .define('x', Items.GLASS_BOTTLE)
                .define('y', EBItems.MAGIC_CRYSTAL_GRAND.get())
                .unlockedBy("has_grand_crystal", has(EBItems.MAGIC_CRYSTAL_GRAND.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EBItems.SMALL_MANA_FLASK.get())
                .pattern("yyy")
                .pattern("yxy")
                .pattern("yyy")
                .define('x', Items.GLASS_BOTTLE)
                .define('y', EBItems.MAGIC_CRYSTAL_SHARD.get())
                .unlockedBy("has_crystal_shard", has(EBItems.MAGIC_CRYSTAL_SHARD.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EBBlocks.RECEPTACLE.get())
                .pattern("gpg")
                .pattern(" g ")
                .define('g', Items.GOLD_INGOT)
                .define('p', Items.FLOWER_POT)
                .unlockedBy("has_flower_pot", has(Items.FLOWER_POT))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, EBItems.POISON_BOMB.get(), 3)
                .requires(Items.SPIDER_EYE, 2)
                .requires(Items.GLASS_BOTTLE)
                .requires(Items.GUNPOWDER)
                .unlockedBy("has_gunpowder", has(Items.GUNPOWDER))
                .save(consumer);

        imbuementFixRuinedBook(EBItems.SPECTRAL_DUST_EARTH.get(), WizardryMainMod.location("gameplay/imbuement/ruined_spell_book_sorcery"), WizardryMainMod.location("imbuement/fix_ruined_spell_book_earth"), consumer);
        imbuementFixRuinedBook(EBItems.SPECTRAL_DUST_FIRE.get(), WizardryMainMod.location("gameplay/imbuement/ruined_spell_book_fire"), WizardryMainMod.location("imbuement/fix_ruined_spell_book_fire"), consumer);
        imbuementFixRuinedBook(EBItems.SPECTRAL_DUST_HEALING.get(), WizardryMainMod.location("gameplay/imbuement/ruined_spell_book_healing"), WizardryMainMod.location("imbuement/fix_ruined_spell_book_healing"), consumer);
        imbuementFixRuinedBook(EBItems.SPECTRAL_DUST_ICE.get(), WizardryMainMod.location("gameplay/imbuement/ruined_spell_book_ice"), WizardryMainMod.location("imbuement/fix_ruined_spell_book_ice"), consumer);
        imbuementFixRuinedBook(EBItems.SPECTRAL_DUST_LIGHTNING.get(), WizardryMainMod.location("gameplay/imbuement/ruined_spell_book_lightning"), WizardryMainMod.location("imbuement/fix_ruined_spell_book_lightning"), consumer);
        imbuementFixRuinedBook(EBItems.SPECTRAL_DUST_NECROMANCY.get(), WizardryMainMod.location("gameplay/imbuement/ruined_spell_book_necromancy"), WizardryMainMod.location("imbuement/fix_ruined_spell_book_necromancy"), consumer);
        imbuementFixRuinedBook(EBItems.SPECTRAL_DUST_SORCERY.get(), WizardryMainMod.location("gameplay/imbuement/ruined_spell_book_sorcery"), WizardryMainMod.location("imbuement/fix_ruined_spell_book_sorcery"), consumer);

        imbuementDustToCrystal(EBItems.SPECTRAL_DUST_EARTH.get(), EBItems.MAGIC_CRYSTAL_EARTH.get(), WizardryMainMod.location("imbuement/magic_crystal_earth"), consumer);
        imbuementDustToCrystal(EBItems.SPECTRAL_DUST_FIRE.get(), EBItems.MAGIC_CRYSTAL_FIRE.get(), WizardryMainMod.location("imbuement/magic_crystal_fire"), consumer);
        imbuementDustToCrystal(EBItems.SPECTRAL_DUST_HEALING.get(), EBItems.MAGIC_CRYSTAL_HEALING.get(), WizardryMainMod.location("imbuement/magic_crystal_healing"), consumer);
        imbuementDustToCrystal(EBItems.SPECTRAL_DUST_ICE.get(), EBItems.MAGIC_CRYSTAL_ICE.get(), WizardryMainMod.location("imbuement/magic_crystal_ice"), consumer);
        imbuementDustToCrystal(EBItems.SPECTRAL_DUST_LIGHTNING.get(), EBItems.MAGIC_CRYSTAL_LIGHTNING.get(), WizardryMainMod.location("imbuement/magic_crystal_lightning"), consumer);
        imbuementDustToCrystal(EBItems.SPECTRAL_DUST_NECROMANCY.get(), EBItems.MAGIC_CRYSTAL_NECROMANCY.get(), WizardryMainMod.location("imbuement/magic_crystal_necromancy"), consumer);
        imbuementDustToCrystal(EBItems.SPECTRAL_DUST_SORCERY.get(), EBItems.MAGIC_CRYSTAL_SORCERY.get(), WizardryMainMod.location("imbuement/magic_crystal_sorcery"), consumer);

        ElementData[] data = new ElementData[]{
                new ElementData(
                        EBItems.SPECTRAL_DUST_SORCERY.get(),
                        new ArmorData(EBItems.WIZARD_HAT_SORCERY.get(), EBItems.WIZARD_ROBE_SORCERY.get(), EBItems.WIZARD_LEGGINGS_SORCERY.get(), EBItems.WIZARD_BOOTS_SORCERY.get(), EBItems.WIZARD_HAT.get(), EBItems.WIZARD_ROBE.get(), EBItems.WIZARD_LEGGINGS.get(), EBItems.WIZARD_BOOTS.get()),
                        new ArmorData(EBItems.SAGE_HAT_SORCERY.get(), EBItems.SAGE_ROBE_SORCERY.get(), EBItems.SAGE_LEGGINGS_SORCERY.get(), EBItems.SAGE_BOOTS_SORCERY.get(), EBItems.SAGE_HAT.get(), EBItems.SAGE_ROBE.get(), EBItems.SAGE_LEGGINGS.get(), EBItems.SAGE_BOOTS.get()),
                        new ArmorData(EBItems.WARLOCK_HOOD_SORCERY.get(), EBItems.WARLOCK_ROBE_SORCERY.get(), EBItems.WARLOCK_LEGGINGS_SORCERY.get(), EBItems.WARLOCK_BOOTS_SORCERY.get(), EBItems.WARLOCK_HOOD.get(), EBItems.WARLOCK_ROBE.get(), EBItems.WARLOCK_LEGGINGS.get(), EBItems.WARLOCK_BOOTS.get()),
                        new ArmorData(EBItems.BATTLEMAGE_HELMET_SORCERY.get(), EBItems.BATTLEMAGE_CHESTPLATE_SORCERY.get(), EBItems.BATTLEMAGE_LEGGINGS_SORCERY.get(), EBItems.BATTLEMAGE_BOOTS_SORCERY.get(), EBItems.BATTLEMAGE_HELMET.get(), EBItems.BATTLEMAGE_CHESTPLATE.get(), EBItems.BATTLEMAGE_LEGGINGS.get(), EBItems.BATTLEMAGE_BOOTS.get())
                ),

                new ElementData(
                        EBItems.SPECTRAL_DUST_FIRE.get(),
                        new ArmorData(EBItems.WIZARD_HAT_FIRE.get(), EBItems.WIZARD_ROBE_FIRE.get(), EBItems.WIZARD_LEGGINGS_FIRE.get(), EBItems.WIZARD_BOOTS_FIRE.get(), EBItems.WIZARD_HAT.get(), EBItems.WIZARD_ROBE.get(), EBItems.WIZARD_LEGGINGS.get(), EBItems.WIZARD_BOOTS.get()),
                        new ArmorData(EBItems.SAGE_HAT_FIRE.get(), EBItems.SAGE_ROBE_FIRE.get(), EBItems.SAGE_LEGGINGS_FIRE.get(), EBItems.SAGE_BOOTS_FIRE.get(), EBItems.SAGE_HAT.get(), EBItems.SAGE_ROBE.get(), EBItems.SAGE_LEGGINGS.get(), EBItems.SAGE_BOOTS.get()),
                        new ArmorData(EBItems.WARLOCK_HOOD_FIRE.get(), EBItems.WARLOCK_ROBE_FIRE.get(), EBItems.WARLOCK_LEGGINGS_FIRE.get(), EBItems.WARLOCK_BOOTS_FIRE.get(), EBItems.WARLOCK_HOOD.get(), EBItems.WARLOCK_ROBE.get(), EBItems.WARLOCK_LEGGINGS.get(), EBItems.WARLOCK_BOOTS.get()),
                        new ArmorData(EBItems.BATTLEMAGE_HELMET_FIRE.get(), EBItems.BATTLEMAGE_CHESTPLATE_FIRE.get(), EBItems.BATTLEMAGE_LEGGINGS_FIRE.get(), EBItems.BATTLEMAGE_BOOTS_FIRE.get(), EBItems.BATTLEMAGE_HELMET.get(), EBItems.BATTLEMAGE_CHESTPLATE.get(), EBItems.BATTLEMAGE_LEGGINGS.get(), EBItems.BATTLEMAGE_BOOTS.get())
                ),

                new ElementData(
                        EBItems.SPECTRAL_DUST_EARTH.get(),
                        new ArmorData(EBItems.WIZARD_HAT_EARTH.get(), EBItems.WIZARD_ROBE_EARTH.get(), EBItems.WIZARD_LEGGINGS_EARTH.get(), EBItems.WIZARD_BOOTS_EARTH.get(), EBItems.WIZARD_HAT.get(), EBItems.WIZARD_ROBE.get(), EBItems.WIZARD_LEGGINGS.get(), EBItems.WIZARD_BOOTS.get()),
                        new ArmorData(EBItems.SAGE_HAT_EARTH.get(), EBItems.SAGE_ROBE_EARTH.get(), EBItems.SAGE_LEGGINGS_EARTH.get(), EBItems.SAGE_BOOTS_EARTH.get(), EBItems.SAGE_HAT.get(), EBItems.SAGE_ROBE.get(), EBItems.SAGE_LEGGINGS.get(), EBItems.SAGE_BOOTS.get()),
                        new ArmorData(EBItems.WARLOCK_HOOD_EARTH.get(), EBItems.WARLOCK_ROBE_EARTH.get(), EBItems.WARLOCK_LEGGINGS_EARTH.get(), EBItems.WARLOCK_BOOTS_EARTH.get(), EBItems.WARLOCK_HOOD.get(), EBItems.WARLOCK_ROBE.get(), EBItems.WARLOCK_LEGGINGS.get(), EBItems.WARLOCK_BOOTS.get()),
                        new ArmorData(EBItems.BATTLEMAGE_HELMET_EARTH.get(), EBItems.BATTLEMAGE_CHESTPLATE_EARTH.get(), EBItems.BATTLEMAGE_LEGGINGS_EARTH.get(), EBItems.BATTLEMAGE_BOOTS_EARTH.get(), EBItems.BATTLEMAGE_HELMET.get(), EBItems.BATTLEMAGE_CHESTPLATE.get(), EBItems.BATTLEMAGE_LEGGINGS.get(), EBItems.BATTLEMAGE_BOOTS.get())
                ),

                new ElementData(
                        EBItems.SPECTRAL_DUST_HEALING.get(),
                        new ArmorData(EBItems.WIZARD_HAT_HEALING.get(), EBItems.WIZARD_ROBE_HEALING.get(), EBItems.WIZARD_LEGGINGS_HEALING.get(), EBItems.WIZARD_BOOTS_HEALING.get(), EBItems.WIZARD_HAT.get(), EBItems.WIZARD_ROBE.get(), EBItems.WIZARD_LEGGINGS.get(), EBItems.WIZARD_BOOTS.get()),
                        new ArmorData(EBItems.SAGE_HAT_HEALING.get(), EBItems.SAGE_ROBE_HEALING.get(), EBItems.SAGE_LEGGINGS_HEALING.get(), EBItems.SAGE_BOOTS_HEALING.get(), EBItems.SAGE_HAT.get(), EBItems.SAGE_ROBE.get(), EBItems.SAGE_LEGGINGS.get(), EBItems.SAGE_BOOTS.get()),
                        new ArmorData(EBItems.WARLOCK_HOOD_HEALING.get(), EBItems.WARLOCK_ROBE_HEALING.get(), EBItems.WARLOCK_LEGGINGS_HEALING.get(), EBItems.WARLOCK_BOOTS_HEALING.get(), EBItems.WARLOCK_HOOD.get(), EBItems.WARLOCK_ROBE.get(), EBItems.WARLOCK_LEGGINGS.get(), EBItems.WARLOCK_BOOTS.get()),
                        new ArmorData(EBItems.BATTLEMAGE_HELMET_HEALING.get(), EBItems.BATTLEMAGE_CHESTPLATE_HEALING.get(), EBItems.BATTLEMAGE_LEGGINGS_HEALING.get(), EBItems.BATTLEMAGE_BOOTS_HEALING.get(), EBItems.BATTLEMAGE_HELMET.get(), EBItems.BATTLEMAGE_CHESTPLATE.get(), EBItems.BATTLEMAGE_LEGGINGS.get(), EBItems.BATTLEMAGE_BOOTS.get())
                ),

                new ElementData(
                        EBItems.SPECTRAL_DUST_ICE.get(),
                        new ArmorData(EBItems.WIZARD_HAT_ICE.get(), EBItems.WIZARD_ROBE_ICE.get(), EBItems.WIZARD_LEGGINGS_ICE.get(), EBItems.WIZARD_BOOTS_ICE.get(), EBItems.WIZARD_HAT.get(), EBItems.WIZARD_ROBE.get(), EBItems.WIZARD_LEGGINGS.get(), EBItems.WIZARD_BOOTS.get()),
                        new ArmorData(EBItems.SAGE_HAT_ICE.get(), EBItems.SAGE_ROBE_ICE.get(), EBItems.SAGE_LEGGINGS_ICE.get(), EBItems.SAGE_BOOTS_ICE.get(), EBItems.SAGE_HAT.get(), EBItems.SAGE_ROBE.get(), EBItems.SAGE_LEGGINGS.get(), EBItems.SAGE_BOOTS.get()),
                        new ArmorData(EBItems.WARLOCK_HOOD_ICE.get(), EBItems.WARLOCK_ROBE_ICE.get(), EBItems.WARLOCK_LEGGINGS_ICE.get(), EBItems.WARLOCK_BOOTS_ICE.get(), EBItems.WARLOCK_HOOD.get(), EBItems.WARLOCK_ROBE.get(), EBItems.WARLOCK_LEGGINGS.get(), EBItems.WARLOCK_BOOTS.get()),
                        new ArmorData(EBItems.BATTLEMAGE_HELMET_ICE.get(), EBItems.BATTLEMAGE_CHESTPLATE_ICE.get(), EBItems.BATTLEMAGE_LEGGINGS_ICE.get(), EBItems.BATTLEMAGE_BOOTS_ICE.get(), EBItems.BATTLEMAGE_HELMET.get(), EBItems.BATTLEMAGE_CHESTPLATE.get(), EBItems.BATTLEMAGE_LEGGINGS.get(), EBItems.BATTLEMAGE_BOOTS.get())
                ),

                new ElementData(
                        EBItems.SPECTRAL_DUST_LIGHTNING.get(),
                        new ArmorData(EBItems.WIZARD_HAT_LIGHTNING.get(), EBItems.WIZARD_ROBE_LIGHTNING.get(), EBItems.WIZARD_LEGGINGS_LIGHTNING.get(), EBItems.WIZARD_BOOTS_LIGHTNING.get(), EBItems.WIZARD_HAT.get(), EBItems.WIZARD_ROBE.get(), EBItems.WIZARD_LEGGINGS.get(), EBItems.WIZARD_BOOTS.get()),
                        new ArmorData(EBItems.SAGE_HAT_LIGHTNING.get(), EBItems.SAGE_ROBE_LIGHTNING.get(), EBItems.SAGE_LEGGINGS_LIGHTNING.get(), EBItems.SAGE_BOOTS_LIGHTNING.get(), EBItems.SAGE_HAT.get(), EBItems.SAGE_ROBE.get(), EBItems.SAGE_LEGGINGS.get(), EBItems.SAGE_BOOTS.get()),
                        new ArmorData(EBItems.WARLOCK_HOOD_LIGHTNING.get(), EBItems.WARLOCK_ROBE_LIGHTNING.get(), EBItems.WARLOCK_LEGGINGS_LIGHTNING.get(), EBItems.WARLOCK_BOOTS_LIGHTNING.get(), EBItems.WARLOCK_HOOD.get(), EBItems.WARLOCK_ROBE.get(), EBItems.WARLOCK_LEGGINGS.get(), EBItems.WARLOCK_BOOTS.get()),
                        new ArmorData(EBItems.BATTLEMAGE_HELMET_LIGHTNING.get(), EBItems.BATTLEMAGE_CHESTPLATE_LIGHTNING.get(), EBItems.BATTLEMAGE_LEGGINGS_LIGHTNING.get(), EBItems.BATTLEMAGE_BOOTS_LIGHTNING.get(), EBItems.BATTLEMAGE_HELMET.get(), EBItems.BATTLEMAGE_CHESTPLATE.get(), EBItems.BATTLEMAGE_LEGGINGS.get(), EBItems.BATTLEMAGE_BOOTS.get())
                ),

                new ElementData(
                        EBItems.SPECTRAL_DUST_NECROMANCY.get(),
                        new ArmorData(EBItems.WIZARD_HAT_NECROMANCY.get(), EBItems.WIZARD_ROBE_NECROMANCY.get(), EBItems.WIZARD_LEGGINGS_NECROMANCY.get(), EBItems.WIZARD_BOOTS_NECROMANCY.get(), EBItems.WIZARD_HAT.get(), EBItems.WIZARD_ROBE.get(), EBItems.WIZARD_LEGGINGS.get(), EBItems.WIZARD_BOOTS.get()),
                        new ArmorData(EBItems.SAGE_HAT_NECROMANCY.get(), EBItems.SAGE_ROBE_NECROMANCY.get(), EBItems.SAGE_LEGGINGS_NECROMANCY.get(), EBItems.SAGE_BOOTS_NECROMANCY.get(), EBItems.SAGE_HAT.get(), EBItems.SAGE_ROBE.get(), EBItems.SAGE_LEGGINGS.get(), EBItems.SAGE_BOOTS.get()),
                        new ArmorData(EBItems.WARLOCK_HOOD_NECROMANCY.get(), EBItems.WARLOCK_ROBE_NECROMANCY.get(), EBItems.WARLOCK_LEGGINGS_NECROMANCY.get(), EBItems.WARLOCK_BOOTS_NECROMANCY.get(), EBItems.WARLOCK_HOOD.get(), EBItems.WARLOCK_ROBE.get(), EBItems.WARLOCK_LEGGINGS.get(), EBItems.WARLOCK_BOOTS.get()),
                        new ArmorData(EBItems.BATTLEMAGE_HELMET_NECROMANCY.get(), EBItems.BATTLEMAGE_CHESTPLATE_NECROMANCY.get(), EBItems.BATTLEMAGE_LEGGINGS_NECROMANCY.get(), EBItems.BATTLEMAGE_BOOTS_NECROMANCY.get(), EBItems.BATTLEMAGE_HELMET.get(), EBItems.BATTLEMAGE_CHESTPLATE.get(), EBItems.BATTLEMAGE_LEGGINGS.get(), EBItems.BATTLEMAGE_BOOTS.get())
                )
        };

        for (ElementData datum : data) {
            ArmorData[] armors = new ArmorData[]{datum.normal(), datum.sage(), datum.warlock(), datum.battleMage()};

            for (ArmorData armorData : armors) {
                imbuementArmor(armorData.defaultHat(), datum.dust(), armorData.hat(), WizardryMainMod.location(BuiltInRegistries.ITEM.getKey(armorData.hat()).getNamespace(), "imbuement/" + BuiltInRegistries.ITEM.getKey(armorData.hat()).getPath()), consumer);
                imbuementArmor(armorData.defaultChest(), datum.dust(), armorData.chest(), WizardryMainMod.location(BuiltInRegistries.ITEM.getKey(armorData.chest()).getNamespace(), "imbuement/" + BuiltInRegistries.ITEM.getKey(armorData.chest()).getPath()), consumer);
                imbuementArmor(armorData.defaultLegs(), datum.dust(), armorData.legs(), WizardryMainMod.location(BuiltInRegistries.ITEM.getKey(armorData.legs()).getNamespace(), "imbuement/" + BuiltInRegistries.ITEM.getKey(armorData.legs()).getPath()), consumer);
                imbuementArmor(armorData.defaultBoots(), datum.dust(), armorData.boots(), WizardryMainMod.location(BuiltInRegistries.ITEM.getKey(armorData.boots()).getNamespace(), "imbuement/" + BuiltInRegistries.ITEM.getKey(armorData.boots()).getPath()), consumer);
            }
        }
    }


    private void imbuementArmor(Item baseArmor, Item spectralDust, Item result, ResourceLocation location, @NotNull Consumer<FinishedRecipe> consumer) {
        ImbuementAltarRecipeBuilder.imbuement(Ingredient.of(baseArmor), Ingredient.of(spectralDust), result)
                .save(consumer, location);
    }

    private void imbuementFixRuinedBook(Item spectralDust, ResourceLocation loot, ResourceLocation location, @NotNull Consumer<FinishedRecipe> consumer) {
        ImbuementAltarRecipeBuilder.imbuement(Ingredient.of(EBItems.RUINED_SPELL_BOOK.get()), Ingredient.of(spectralDust), EBItems.RANDOM_SPELL_BOOK.get())
                .withNbt(nbtForRandomSpellBook(loot.toString())).unlockedBy("has_ruined_spell_book", has(EBItems.RUINED_SPELL_BOOK.get()))
                .save(consumer, location);
    }

    private void imbuementDustToCrystal(Item spectralDust, Item crystal, ResourceLocation location, @NotNull Consumer<FinishedRecipe> consumer) {
        ImbuementAltarRecipeBuilder.imbuement(Ingredient.of(EBItems.MAGIC_CRYSTAL.get()), Ingredient.of(spectralDust),
                crystal).unlockedBy("has_magic_crystal", has(EBItems.MAGIC_CRYSTAL.get())).save(consumer, location);
    }

    private void wand(Item wand, Item crystal, @NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, wand)
                .define('x', Items.GOLD_NUGGET)
                .define('y', ItemTags.PLANKS)
                .define('z', crystal)
                .pattern("  z")
                .pattern(" y ")
                .pattern("x  ")
                .unlockedBy("has_magic_crystal", has(EBTags.MAGIC_CRYSTAL_ITEM))
                .save(consumer);
    }

    private void gildedWood(Block gildenWood, Block planks, @NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, gildenWood)
                .define('b', planks)
                .define('i', Items.GOLD_NUGGET)
                .pattern("bbb").pattern("ibi").pattern("bbb")
                .unlockedBy("has_" + ForgeRegistries.BLOCKS.getKey(planks).getPath(), has(planks))
                .save(consumer);
    }

    private void runestone(Block runestone, Item crystal, @NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, runestone, 8)
                .pattern("zzz")
                .pattern("zyz")
                .pattern("zzz")
                .define('y', crystal)
                .define('z', Items.STONE)
                .unlockedBy("has_magic_crystal", has(EBTags.MAGIC_CRYSTAL_ITEM))
                .save(consumer);
    }

    private void runestonePedestal(Block runestone, Item crystal, @NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, runestone, 2)
                .pattern("zzz")
                .pattern("yyy")
                .pattern("zyz")
                .define('y', crystal)
                .define('z', Items.STONE)
                .unlockedBy("has_magic_crystal", has(EBTags.MAGIC_CRYSTAL_ITEM))
                .save(consumer);
    }

    private void bookshelf(Block bookshelf, Block wood, @NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, bookshelf)
                .define('b', wood)
                .define('i', EBItems.MAGIC_CRYSTAL_GRAND.get())
                .pattern("bbb").pattern(" i ").pattern("bbb")
                .unlockedBy("has_gilded_wood", has(EBTags.GILDED_WOOD_ITEM)).save(consumer);
    }

    private void lectern(Block lectern, Block wood, @NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, lectern)
                .define('b', wood)
                .define('i', EBItems.MAGIC_CRYSTAL_GRAND.get())
                .define('n', Items.BOOK)
                .pattern("bnb").pattern("ibi").pattern("bbb")
                .unlockedBy("has_gilded_wood", has(EBTags.GILDED_WOOD_ITEM)).save(consumer);
    }

    private CompoundTag nbtForRandomSpellBook(String loot) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("LootTable", loot);
        return nbt;
    }
}
