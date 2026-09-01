package com.binaris.wizardry.client.gui.screens.handbook;

import com.binaris.wizardry.api.client.util.DrawingUtils;
import com.binaris.wizardry.core.EBLogger;
import com.google.common.collect.Streams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.Nullable;
import java.util.*;

public class CraftingRecipe {
    public static final int BORDER = 7;
    public static final int TEXTURE_INSET_X = 40, TEXTURE_INSET_Y = 190;
    public static final int WIDTH = 121, HEIGHT = 66;

    // Final fields are mandatory, the rest are optional
    private final ResourceLocation[] locations;
    private final Set<int[]> instances = new HashSet<>();
    // Derived fields, not specifically defined in JSON
    private List<Recipe<?>> recipes;

    private CraftingRecipe(ResourceLocation[] locations) {
        this.locations = locations;
    }

    /// Parses the given JSON object and constructs a new {@code Image} from it, setting all the relevant fields
    /// and references.
    /// 
    /// @param json A JSON object representing the image to be constructed. This must contain at least a "locations" string.
    /// @return The resulting {@code Image} object.
    /// @throws JsonSyntaxException if at any point the JSON object is found to be invalid.
    public static CraftingRecipe fromJson(JsonObject json) {
        ResourceLocation[] locations = Streams.stream(GsonHelper.getAsJsonArray(json, "locations"))
                .map(je -> new ResourceLocation(je.getAsString())).toArray(ResourceLocation[]::new);
        return new CraftingRecipe(locations);
    }

    public static void populate(Map<String, CraftingRecipe> map, JsonObject json) {

        JsonObject sectionsObject = GsonHelper.getAsJsonObject(json, "recipes");

        // Need to iterate over these since we don't know what they're called or how many there are
        for (Map.Entry<String, JsonElement> entry : sectionsObject.entrySet()) {

            String key = entry.getKey(); // Find out what each element is called, this will be the sections map key

            CraftingRecipe recipe = fromJson(entry.getValue().getAsJsonObject());
            map.put(key, recipe);
        }
    }

    private static void renderCraftingRecipe(GuiGraphics guiGraphics, Font font, ResourceLocation texture, ItemRenderer itemRenderer, int x, int y, @Nullable Recipe<?> recipe) {
        DrawingUtils.drawTexturedRect(guiGraphics, texture, x, y, TEXTURE_INSET_X, TEXTURE_INSET_Y, WIDTH, HEIGHT, HandBookScreen.TEXTURE_WIDTH, HandBookScreen.TEXTURE_HEIGHT);

        if (recipe != null) {
            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();

            int index = (int) ((System.currentTimeMillis() % Integer.MAX_VALUE) / 2000);

            int i = 0;
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient != Ingredient.EMPTY) {
                    ItemStack stack = ingredient.getItems()[index % ingredient.getItems().length];
                    if (!stack.isEmpty()) {
                        guiGraphics.renderItem(stack, x + BORDER + 18 * (i % 3), y + BORDER + 18 * (i / 3));
                        guiGraphics.renderItemDecorations(font, stack, x + BORDER + 18 * (i % 3), y + BORDER + 18 * (i / 3));
                    }
                }

                i++;
            }

            ItemStack result = recipe.getResultItem(null);
            if (!result.isEmpty()) {
                guiGraphics.renderItem(result, x + BORDER + 86, y + BORDER + 18);
                guiGraphics.renderItemDecorations(font, result, x + BORDER + 86, y + BORDER + 18);
            }

            poseStack.popPose();
        }
    }

    private static void renderCraftingTooltips(GuiGraphics guiGraphics, Font font, int x, int y, int mouseX, int mouseY, Recipe<?> recipe) {

        ItemStack result = recipe.getResultItem(null);

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        int index = (int) ((System.currentTimeMillis() % Integer.MAX_VALUE) / 2000);

        int i = 0;
        for (Ingredient ingredient : recipe.getIngredients()) {

            if (ingredient != Ingredient.EMPTY) {
                ItemStack stack = ingredient.getItems()[index % ingredient.getItems().length];
                if (!stack.isEmpty() && isPointInRegion(x + BORDER + 18 * (i % 3), y + BORDER + 18 * (i / 3), 16, 16, mouseX, mouseY)) {
                    List<Component> tooltip = Screen.getTooltipFromItem(Minecraft.getInstance(), stack);
                    guiGraphics.renderTooltip(font, tooltip, stack.getTooltipImage(), mouseX, mouseY);
                }
            }

            i++;
        }

        if (!result.isEmpty() && isPointInRegion(x + BORDER + 86, y + BORDER + 18, 16, 16, mouseX, mouseY)) {
            List<Component> tooltip = Screen.getTooltipFromItem(Minecraft.getInstance(), result);
            guiGraphics.renderTooltip(font, tooltip, result.getTooltipImage(), mouseX, mouseY);
        }

        poseStack.popPose();
    }

    private static boolean isPointInRegion(int left, int top, int width, int height, int mouseX, int mouseY) {
        return mouseX >= left - 1 && mouseX < left + width + 1 && mouseY >= top - 1 && mouseY < top + height + 1;
    }

    /// Adds an instance of this recipe to the list.
    /// 
    /// @param page The index of the <b>single</b> page this image is on.
    /// @param x    The x-coordinate of the top-left corner of the image, <i>relative</i> to the top-left corner of the GUI.
    /// @param y    The y-coordinate of the top-left corner of the image, <i>relative</i> to the top-left corner of the GUI.
    public void addInstance(int page, int x, int y) {
        instances.add(new int[]{page, x, y});
    }

    /// Removes all instances of this recipe from the list.
    public void clearInstances() {
        instances.clear();
    }

    /// Called on GUI open to load the actual recipe object from the registry. This cannot be done on JSON load since
    /// the recipes aren't necessarily loaded at that point.
    public void load() {
        recipes = new ArrayList<>(locations.length);

        for (ResourceLocation location : locations) {
            Recipe<?> recipe = Minecraft.getInstance().level.getRecipeManager().byKey(location).orElse(null);
            if (recipe == null)
                EBLogger.warn("The recipe {} used in the wizard's handbook does not exist, it will display a blank grid instead", location);
            else recipes.add(recipe);
        }
    }

    /// Draws all instances of this recipe that are located on the given double-page spread.
    /// 
    /// @param font         The font renderer object.
    /// @param itemRenderer The item renderer object.
    /// @param doublePage   The double-page index of the page to be drawn.
    /// @param left         The x coordinate of the left side of the GUI.
    /// @param top          The y coordinate of the top of the GUI.
    public void draw(GuiGraphics guiGraphics, Font font, ResourceLocation texture, ItemRenderer itemRenderer, int doublePage, int left, int top) {

        int index = (int) (System.currentTimeMillis() % Integer.MAX_VALUE) / 2000;

        for (int[] instance : instances) {
            if (HandBookScreen.singleToDoublePage(instance[0]) == doublePage) {
                renderCraftingRecipe(guiGraphics, font, texture, itemRenderer, left + instance[1], top + instance[2], recipes.isEmpty() ? null : recipes.get(index % recipes.size()));
            }
        }
    }

    /// Draws the tooltips for all instances of this recipe that are located on the given double-page spread. This has to
    /// be done separately so that the tooltips are on top of everything else.
    /// 
    /// @param font       The item renderer object.
    /// @param doublePage The double-page index of the page to be drawn.
    /// @param left       The x coordinate of the left side of the GUI.
    /// @param top        The y coordinate of the top of the GUI.
    public void drawTooltips(GuiGraphics guiGraphics, Font font, int doublePage, int left, int top, int mouseX, int mouseY) {
        if (recipes.isEmpty()) return;

        int index = (int) ((System.currentTimeMillis() % Integer.MAX_VALUE) / 2000);
        for (int[] instance : instances) {
            if (HandBookScreen.singleToDoublePage(instance[0]) == doublePage) {
                renderCraftingTooltips(guiGraphics, font, left + instance[1], top + instance[2], mouseX, mouseY, recipes.get(index % recipes.size()));
            }
        }
    }
}
