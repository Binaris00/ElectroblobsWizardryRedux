package com.binaris.wizardry.client.compat.jei;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.item.IManaItem;
import com.binaris.wizardry.api.content.item.IWorkbenchItem;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.RegistryUtils;
import com.binaris.wizardry.content.item.WandItem;
import com.binaris.wizardry.content.menu.ArcaneWorkbenchMenu;
import com.binaris.wizardry.content.recipe.ArcaneWorkbenchRecipe;
import com.binaris.wizardry.core.config.EBServerConfig;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBBlocks;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.EBTags;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
    public @NotNull RecipeType<ArcaneWorkbenchRecipe> getRecipeType() {
        return ARCANE_WORKBENCH;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("gui.better_ebwizardry.arcane_workbench.title");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(ArcaneWorkbenchRecipe recipe, @NotNull IRecipeSlotsView view, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
        // Can't do this in IRecipeCategory#drawExtras because we have no access to the recipe there!
        // ArcaneWorkbenchRecipeCategory.TEXTURE is already bound at this point
        int slots = recipe.getSlots();
        for(int i = 0; i < slots; i++) {
            int x = CENTRE_SLOT_X + ArcaneWorkbenchMenu.getBookSlotXOffset(i, slots);
            int y = CENTRE_SLOT_Y + ArcaneWorkbenchMenu.getBookSlotYOffset(i, slots);
            this.bookSlots.draw(guiGraphics, x - 8, y - 8);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ArcaneWorkbenchRecipe recipe, @NotNull IFocusGroup focusGroup) {
        // Center Item
        ItemStack center = recipe.getCentreStack();
        // Crystal Item
        Ingredient crystals = recipe.getCrystals();
        // Upgrade Item
        Ingredient upgrades = recipe.getUpgrades();
        // Input Item
        List<ItemStack> input = recipe.getInputs();
        // Result Item
        ItemStack output = recipe.getResult();
        // Slot Number
        int slots = recipe.getSlots();

        // Output Slot
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_SLOT_X, OUTPUT_SLOT_Y)
                .addItemStack(output);

        // Slot initialisation
        for (int i = 0; i < slots; i++) {
            int x = CENTRE_SLOT_X + ArcaneWorkbenchMenu.getBookSlotXOffset(i, slots);
            int y = CENTRE_SLOT_Y + ArcaneWorkbenchMenu.getBookSlotYOffset(i, slots);
            builder.addSlot(RecipeIngredientRole.CATALYST, x ,y).addItemStacks(input);
        }

        // Crystal Slot
        builder.addSlot(RecipeIngredientRole.CATALYST, CRYSTAL_SLOT_X, CRYSTAL_SLOT_Y)
                .addIngredients(crystals);
        // Center Slot
        builder.addSlot(RecipeIngredientRole.INPUT, CENTRE_SLOT_X, CENTRE_SLOT_Y)
                .addItemStack(center);
        // Upgrade Slot
        builder.addSlot(RecipeIngredientRole.CATALYST, UPGRADE_SLOT_X, UPGRADE_SLOT_Y)
                .addIngredients(upgrades);
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
        // Upgrades Item, include Arcane Tome, Armor Upgrade
        ItemStack[] wandUpgrades = Ingredient.of(EBTags.WAND_UPGRADES).getItems();
        ItemStack[] armorUpgrade = Ingredient.of(EBTags.ARMOR_UPGRADE).getItems();
        // Get all items that implement the IWorkbenchItem interface.
        // WandItem, ScrollItem, BlankScrollItem, WizardArmorItem
        ItemStack[] wands = Ingredient.of(EBTags.WAND).getItems();
        ItemStack[] armors = Ingredient.of(EBTags.WIZARD_ARMOR).getItems();

        // Wand Upgrade
        for (ItemStack origin : wands) {
            if (origin.getItem() instanceof IWorkbenchItem workbenchItem) {
                for (ItemStack upgrade : wandUpgrades) {
                    // Copy both input stacks to ignore any modifications to them during the upgrading process
                    ItemStack result = workbenchItem.applyUpgrade(null, origin.copy(), upgrade.copy());
                    // It's only a valid 'recipe' if something actually changed
                    if (!ItemStack.isSameItem(origin, result)) {
                        recipes.add(ArcaneWorkbenchRecipe.addUpgradeItem(origin, Ingredient.of(upgrade), result));
                        break;
                    }
                }
                // Condense all special upgrades into one ingredient in an effort to reduce the number of separate recipes
                if (origin.getItem() instanceof WandItem)
                    recipes.add(ArcaneWorkbenchRecipe.addUpgradeItem(origin, Ingredient.of(EBTags.SPECIAL_UPGRADES), origin));
            }
        }

        // Armor Upgrade
        for (ItemStack upgrade : armorUpgrade) {
            for (ItemStack armor : armors) {
                if (armor.getItem() instanceof IWorkbenchItem workbenchItem) {
                    ItemStack result = workbenchItem.applyUpgrade(null, armor.copy(), upgrade.copy());
                    if (!ItemStack.isSameItem(armor, result)) {
                        recipes.add(ArcaneWorkbenchRecipe.addUpgradeItem(armor, Ingredient.of(upgrade), result));
                    }
                }
            }
        }

        return recipes;
    }

    private static List<ArcaneWorkbenchRecipe> generateChargingRecipes() {
        List<ArcaneWorkbenchRecipe> recipes = new ArrayList<>();
        ItemStack[] chargeableItems = Ingredient.of(EBTags.MANA_ITEM).getItems();

        for (ItemStack chargeable : chargeableItems) {
            if(!(chargeable.getItem() instanceof IManaItem manaItem))
                throw new IllegalArgumentException("Item to be charged must be an instance of IManaItem");

            ItemStack input = chargeable.copy();
            manaItem.setMana(input, 0);

            ItemStack result = chargeable.copy();
            manaItem.setMana(result, EBServerConfig.MANA_PER_CRYSTAL.get());
            recipes.add(ArcaneWorkbenchRecipe.addMagicValue(input, Ingredient.of(EBTags.NORMAL_MAGIC_CRYSTAL), result));

            result = chargeable.copy();
            manaItem.setMana(result, EBServerConfig.MANA_PER_SHARD.get());
            recipes.add(ArcaneWorkbenchRecipe.addMagicValue(input, Ingredient.of(EBTags.MAGIC_SHARD_ITEM), result));

            result = chargeable.copy();
            manaItem.setMana(result, EBServerConfig.GRAND_CRYSTAL_MANA.get());
            recipes.add(ArcaneWorkbenchRecipe.addMagicValue(input, Ingredient.of(EBTags.GRAND_MAGIC_CRYSTAL), result));
        }

        return recipes;
    }

    private static List<ArcaneWorkbenchRecipe> generateScrollRecipes() {
        List<ArcaneWorkbenchRecipe> recipes = new ArrayList<>();
        // We need not make people register these manually since spells already have control over what they can be put on
        ItemStack blankScroll = new ItemStack(EBItems.BLANK_SCROLL.get());

        for (Spell spell : Services.REGISTRY_UTIL.getSpells()) {
            ItemStack book = setMetaData(EBItems.SPELL_BOOK.get(), spell);
            ItemStack result = setMetaData(EBItems.SCROLL.get(), spell);
            recipes.add(ArcaneWorkbenchRecipe.recipe(blankScroll, book, Ingredient.of(EBTags.MAGIC_CRYSTAL_ITEM), result));
        }

        return recipes;
    }

    public static ItemStack setMetaData(Item item, Spell spell) {
        ItemStack stack = new ItemStack(item);
        RegistryUtils.setSpell(stack, spell);
        return stack;
    }
}
