package com.electroblob.wizardry.datagen.provider;

import com.electroblob.wizardry.setup.registries.EBBlocks;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.EBTags;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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
    }

//    private ItemStack spellBook() {
//        ItemStack stack = EBItems.SPELL_BOOK.get().getDefaultInstance();
//        SpellUtil.setSpell(stack, Spells.MAGIC_MISSILE);
//        return stack;
//    }

    private void wand(Item wand, Item crystal, @NotNull Consumer<FinishedRecipe> consumer){
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

    private void gildedWood(Block gildenWood, Block planks, @NotNull Consumer<FinishedRecipe> consumer){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, gildenWood)
                .define('b', planks)
                .define('i', Items.GOLD_NUGGET)
                .pattern("bbb").pattern("ibi").pattern("bbb")
                .unlockedBy("has_" + ForgeRegistries.BLOCKS.getKey(planks).getPath(), has(planks))
                .save(consumer);
    }

    private void runestone(Block runestone, Item crystal, @NotNull Consumer<FinishedRecipe> consumer){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, runestone, 8)
                .pattern("zzz")
                .pattern("zyz")
                .pattern("zzz")
                .define('y', crystal)
                .define('z', Items.STONE)
                .unlockedBy("has_magic_crystal", has(EBTags.MAGIC_CRYSTAL_ITEM))
                .save(consumer);
    }

    private void runestonePedestal(Block runestone, Item crystal, @NotNull Consumer<FinishedRecipe> consumer){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, runestone, 2)
                .pattern("zzz")
                .pattern("yyy")
                .pattern("zyz")
                .define('y', crystal)
                .define('z', Items.STONE)
                .unlockedBy("has_magic_crystal", has(EBTags.MAGIC_CRYSTAL_ITEM))
                .save(consumer);
    }

    private void bookshelf(Block bookshelf, Block wood, @NotNull Consumer<FinishedRecipe> consumer){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, bookshelf)
                .define('b', wood)
                .define('i', EBItems.MAGIC_CRYSTAL_GRAND.get())
                .pattern("bbb").pattern(" i ").pattern("bbb")
                .unlockedBy("has_gilded_wood", has(EBTags.GILDED_WOOD_ITEM)).save(consumer);
    }

    private void lectern(Block lectern, Block wood, @NotNull Consumer<FinishedRecipe> consumer){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, lectern)
                .define('b', wood)
                .define('i', EBItems.MAGIC_CRYSTAL_GRAND.get())
                .define('n', Items.BOOK)
                .pattern("bnb").pattern("ibi").pattern("bbb")
                .unlockedBy("has_gilded_wood", has(EBTags.GILDED_WOOD_ITEM)).save(consumer);
    }
}
