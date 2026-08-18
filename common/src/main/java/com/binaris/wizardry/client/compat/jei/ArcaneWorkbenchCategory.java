package com.binaris.wizardry.client.compat.jei;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.item.IManaItem;
import com.binaris.wizardry.api.content.item.IWorkbenchItem;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.RegistryUtils;
import com.binaris.wizardry.content.item.*;
import com.binaris.wizardry.content.menu.ArcaneWorkbenchMenu;
import com.binaris.wizardry.content.recipe.ArcaneWorkbenchRecipe;
import com.binaris.wizardry.content.recipe.WizardryRecipes;
import com.binaris.wizardry.core.config.EBServerConfig;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBBlocks;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.WandUpgrades;
import com.google.common.collect.Streams;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ArcaneWorkbenchCategory implements IRecipeCategory<ArcaneWorkbenchRecipe> {
    public static final ResourceLocation TEXTURE = WizardryMainMod.location("textures/integration/jei/arcane_workbench_background.png");
    public static final RecipeType<ArcaneWorkbenchRecipe> ARCANE_WORKBENCH = new RecipeType<>(WizardryMainMod.location("arcane_workbench"), ArcaneWorkbenchRecipe.class);
    // Center Item Stack Slot Position
    private static final int CENTRE_SLOT_X = 75;
    private static final int CENTRE_SLOT_Y = 55;
    // Crystal Slot Position
    private static final int CRYSTAL_SLOT_X = 8;
    private static final int CRYSTAL_SLOT_Y = 92;
    // Upgrade Slot Position
    private static final int UPGRADE_SLOT_X = 142;
    private static final int UPGRADE_SLOT_Y = 8;
    // Output Slot Position
    private static final int OUTPUT_SLOT_X = 142;
    private static final int OUTPUT_SLOT_Y = 102;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable bookSlots;

    public ArcaneWorkbenchCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 166, 126);
        // Imbuement Altar Block
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EBBlocks.ARCANE_WORKBENCH.get()));
        // Spell Book Slots
        this.bookSlots = helper.createDrawable(TEXTURE, 2, 128, 32, 32);
    }

    @Deprecated
    @Override
    public @Nullable IDrawable getBackground() {
        return this.background;
    }

    @Override
    public RecipeType<ArcaneWorkbenchRecipe> getRecipeType() {
        return ARCANE_WORKBENCH;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.better_ebwizardry.arcane_workbench.title");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(ArcaneWorkbenchRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
        // Can't do this in IRecipeCategory#drawExtras because we have no access to the recipe there!
        // ArcaneWorkbenchRecipeCategory.TEXTURE is already bound at this point
        int bookSlots = recipe.getBookSlots();
        for(int i = 0; i < bookSlots; i++) {
            int x = CENTRE_SLOT_X + ArcaneWorkbenchMenu.getBookSlotXOffset(i, bookSlots);
            int y = CENTRE_SLOT_Y + ArcaneWorkbenchMenu.getBookSlotYOffset(i, bookSlots);
            this.bookSlots.draw(guiGraphics, x - 8, y - 8);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ArcaneWorkbenchRecipe recipe, IFocusGroup iFocusGroup) {
        // IGuiItemStackGroup slots = recipeLayout.getItemStacks();
        // Center Item
        ItemStack center = recipe.getCentreStack();
        // Crystal Item
        List<ItemStack> crystals = recipe.getCrystals();
        // Upgrade Item
        List<ItemStack> upgrades = recipe.getUpgrades();
        // Book Item
        List<ItemStack> books = recipe.getBooks();
        // Input Item
        List<List<ItemStack>> input = recipe.getInputs();
        // Result Item
        ItemStack output = recipe.getResult();

        // Output Slot
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_SLOT_X, OUTPUT_SLOT_Y)
                .addItemStack(output);

        // Spell Book Slot Count
        int bookSlots = 0;
        if (center.getItem() instanceof IWorkbenchItem item) {
            bookSlots = item.getSpellSlotCount(center);
        }

        // Slot initialisation
        int i = 0;
        /* Set Spell Book Slot */
        while (i < bookSlots) {
            int x = CENTRE_SLOT_X + ArcaneWorkbenchMenu.getBookSlotXOffset(i, bookSlots);
            int y = CENTRE_SLOT_Y + ArcaneWorkbenchMenu.getBookSlotYOffset(i, bookSlots);
            i++;
            builder.addSlot(RecipeIngredientRole.CATALYST, x, y);
        }

        if (bookSlots == 1 && !books.isEmpty()) {
            int bookSlotX = CENTRE_SLOT_X + ArcaneWorkbenchMenu.getBookSlotXOffset(1, 1);
            int bookSlotY = CENTRE_SLOT_Y + ArcaneWorkbenchMenu.getBookSlotYOffset(1, 1);
            builder.addSlot(RecipeIngredientRole.CATALYST, bookSlotX, bookSlotY)
                    .addItemStacks(input.get(0));
        }

        // Crystal Slot
        builder.addSlot(RecipeIngredientRole.CATALYST, CRYSTAL_SLOT_X, CRYSTAL_SLOT_Y)
                .addItemStacks(crystals);
        // Center Slot
        builder.addSlot(RecipeIngredientRole.INPUT, CENTRE_SLOT_X, CENTRE_SLOT_Y)
                .addItemStack(center);
        // Upgrade Slot
        builder.addSlot(RecipeIngredientRole.CATALYST, UPGRADE_SLOT_X, UPGRADE_SLOT_Y)
                .addItemStacks(upgrades);
    }

    public static List<ArcaneWorkbenchRecipe> generateRecipes() {
        List<ArcaneWorkbenchRecipe> recipes = new ArrayList<>();

        // Probably nicest to have these first, they're the most useful
        recipes.addAll(generateUpgradeRecipes());
        recipes.addAll(generateChargingRecipes());
        recipes.addAll(generateScrollRecipes());

        return recipes;
    }

    private static List<ArcaneWorkbenchRecipe> generateUpgradeRecipes() {
        // Recipes
        List<ArcaneWorkbenchRecipe> recipes = new ArrayList<>();
        // Upgrades
        List<ItemStack> upgrades = new ArrayList<>();

        for(Item item : BuiltInRegistries.ITEM){
            if(item instanceof ArcaneTomeItem || item instanceof ArmorUpgradeItem){
                NonNullList<ItemStack> variants = NonNullList.create();
                variants.add(new ItemStack(item));
                upgrades.addAll(variants);
            }
        }

        // Condense all special upgrades into one ingredient in an effort to reduce the number of separate recipes
        List<ItemStack> specialUpgrades = new ArrayList<>();

        for(Item item : WandUpgrades.getSpecialUpgrades()){
            NonNullList<ItemStack> variants = NonNullList.create();
            variants.add(new ItemStack(item));
            specialUpgrades.addAll(variants);
        }

        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof IWorkbenchItem workbenchItem) {
                ItemStack origin = new ItemStack(item);

                for (ItemStack upgrade : upgrades) {
                    // Copy both input stacks to ignore any modifications to them during the upgrading process
                    ItemStack result = workbenchItem.applyUpgrade(null, origin.copy(), upgrade.copy());
                    // It's only a valid 'recipe' if something actually changed
                    if (!ItemStack.isSameItem(origin, result)) {
                        recipes.add(new ArcaneWorkbenchRecipe(origin, Collections.emptyList(), Collections.emptyList(),
                                Collections.singletonList(upgrade), result));
                    }
                }

                List<ItemStack> applicableSpecialUpgrades = new ArrayList<>();
                for (ItemStack upgrade : specialUpgrades) {
                    // Copy both input stacks to ignore any modifications to them during the upgrading process
                    ItemStack result = workbenchItem.applyUpgrade(null, origin.copy(), upgrade.copy());
                    // It's only a valid 'recipe' if something actually changed
                    if (!ItemStack.isSameItem(origin, result)) {
                        applicableSpecialUpgrades.add(upgrade);
                    }
                }

                if (!applicableSpecialUpgrades.isEmpty()) {
                    recipes.add(new ArcaneWorkbenchRecipe(origin, Collections.emptyList(), Collections.emptyList(),
                            applicableSpecialUpgrades, origin)); // Wands with special upgrades look no different anyway
                }
            }
        }

        return recipes;
    }

    private static List<ArcaneWorkbenchRecipe> generateChargingRecipes() {
        List<ArcaneWorkbenchRecipe> recipes = new ArrayList<>();

        List<ItemStack> crystals = new ArrayList<>();

        ArcaneWorkbenchRecipe.getAllCrystal(crystals);

        List<ItemStack> shard = Collections.singletonList(new ItemStack(EBItems.MAGIC_CRYSTAL_GRAND.get()));
        List<ItemStack> grandCrystal = Collections.singletonList(new ItemStack(EBItems.MAGIC_CRYSTAL_GRAND.get()));

        for (Item chargeable : WizardryRecipes.getChargeableItems()) {
            if(!(chargeable instanceof IManaItem manaItem))
                throw new IllegalArgumentException("Item to be charged must be an instance of IManaItem");

            ItemStack input = new ItemStack(chargeable);
            manaItem.setMana(input, 0);

            ItemStack result = new ItemStack(chargeable);
            manaItem.setMana(result, EBServerConfig.MANA_PER_CRYSTAL.get());
            recipes.add(new ArcaneWorkbenchRecipe(input, Collections.emptyList(), crystals, Collections.emptyList(), result));

            result = new ItemStack(chargeable);
            manaItem.setMana(result, EBServerConfig.MANA_PER_SHARD.get());
            recipes.add(new ArcaneWorkbenchRecipe(input, Collections.emptyList(), shard, Collections.emptyList(), result));

            result = new ItemStack(chargeable);
            manaItem.setMana(result, EBServerConfig.GRAND_CRYSTAL_MANA.get());
            recipes.add(new ArcaneWorkbenchRecipe(input, Collections.emptyList(), grandCrystal, Collections.emptyList(), result));
        }

        return recipes;
    }

    private static List<ArcaneWorkbenchRecipe> generateScrollRecipes() {
        List<ArcaneWorkbenchRecipe> recipes = new ArrayList<>();

        ItemStack blankScroll = new ItemStack(EBItems.BLANK_SCROLL.get());
        // We need not make people register these manually since spells already have control over what they can be put on
        List<Item> spellBooks = Streams.stream(BuiltInRegistries.ITEM).filter(item -> item instanceof SpellBookItem).toList();
        List<Item> scrolls = Streams.stream(BuiltInRegistries.ITEM).filter(item -> item instanceof ScrollItem).toList();

        for (Spell spell : Services.REGISTRY_UTIL.getSpells()) {
            for(Item spellBook : spellBooks) {
                for(Item scroll : scrolls) {
                    if (spell.applicableForItem(spellBook) && spell.applicableForItem(scroll)) {
                        List<ItemStack> books = Collections.singletonList(setMetaData(spellBook, spell));
                        ItemStack result = setMetaData(scroll, spell);
                        recipes.add(new ArcaneWorkbenchRecipe(blankScroll, books, spell.getCost(), Collections.emptyList(), result));
                    }
                }
            }
        }

        return recipes;
    }

    public static ItemStack setMetaData(Item item, Spell spell) {
        ItemStack stack = new ItemStack(item);
        RegistryUtils.setSpell(stack, spell);
        return stack;
    }

}
