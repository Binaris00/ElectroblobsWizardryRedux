package com.binaris.wizardry.content.recipe;

import com.binaris.wizardry.setup.registries.EBRecipeTypes;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/// Defines a crafting recipe for the Imbuement Altar, requiring a center item placed on the
/// altar block and exactly four receptacle ingredients placed in surrounding Wall Receptacle
/// pedestals to produce a result.
///
/// The recipe uses custom matching logic separate from Minecraft's standard shaped/shapeless
/// recipes. Items are placed in the altar's five slots (one center plus four receptacle
/// pedestals). Matching greedily pairs each receptacle item with the first available unmatched
/// recipe ingredient, rejecting any arrangement where any pedestal is empty or any recipe
/// ingredient goes unmatched.
///
/// This recipe type is looked up at runtime by {@code ImbuementAltarBlockEntity} via
/// {@code EBRecipeTypes.IMBUEMENT_ALTAR} during both {@code checkRecipe()} (to validate
/// and start the imbuement timer) and {@code craftRecipe()} (to re-validate and produce
/// the output).
public class ImbuementAltarRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final NonNullList<Ingredient> receptacleIngredients;
    private final Ingredient centerIngredient;
    private final ItemStack output;

    public ImbuementAltarRecipe(ResourceLocation id, NonNullList<Ingredient> receptacleIngredients, Ingredient centerIngredient, ItemStack output) {
        this.id = id;
        this.receptacleIngredients = receptacleIngredients;
        this.centerIngredient = centerIngredient;
        this.output = output;
    }

    /// Reads an item stack from a JSON object.
    ///
    /// Checks for a valid item, optional count (default 1), an optional nbt data from that item and
    /// then returns the item stack. This is specially made by hand because Minecraft doesn't support
    /// nbt data in recipe JSONs for items.
    private static ItemStack itemStackFromJson(JsonObject stackObject) {
        Item item = ShapedRecipe.itemFromJson(stackObject);
        int count = GsonHelper.getAsInt(stackObject, "count", 1);

        if (count < 1) {
            throw new JsonSyntaxException("Invalid output count: " + count);
        }

        ItemStack stack = new ItemStack(item, count);

        if (stackObject.has("nbt")) {
            try {
                CompoundTag nbt = TagParser.parseTag(GsonHelper.getAsString(stackObject, "nbt"));
                stack.setTag(nbt);
            } catch (Exception e) {
                throw new JsonParseException("Invalid NBT data: " + e.getMessage());
            }
        }

        return stack;
    }

    /// Checks whether the given center item and four receptacle items satisfy this recipe's
    /// ingredient requirements.
    ///
    /// Immediately rejects if fewer than exactly four receptacle items are provided or the
    /// center fails the ingredient test. Each receptacle item is greedily paired with the
    /// first available (unmatched) recipe ingredient that accepts it, requiring every recipe
    /// ingredient to be matched exactly once and every receptacle pedestal to be non-empty.
    ///
    /// @param centerStack the item placed in the altar's center slot.
    /// @param receptacleStacks an array of exactly four items from the surrounding receptacle
    ///                          pedestals.
    /// @return true if all ingredients are satisfied, false otherwise.
    public boolean matches(ItemStack centerStack, ItemStack[] receptacleStacks) {
        if (receptacleStacks.length != 4) return false;
        if (!centerIngredient.test(centerStack)) return false;

        boolean[] matched = new boolean[4];
        for (int i = 0; i < 4; i++) {
            if (receptacleStacks[i].isEmpty()) return false;

            for (int j = 0; j < receptacleIngredients.size(); j++) {
                if (!matched[j] && receptacleIngredients.get(j).test(receptacleStacks[i])) {
                    matched[j] = true;
                    break;
                }
            }
        }

        for (boolean m : matched) {
            if (!m) return false;
        }

        return true;
    }

    @Override
    public boolean matches(@NotNull Container container, @NotNull Level level) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container container, @NotNull RegistryAccess access) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess access) {
        return output.copy();
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EBRecipeTypes.IMBUEMENT_ALTAR_SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return EBRecipeTypes.IMBUEMENT_ALTAR;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    /// Returns the list of four receptacle ingredients required by this recipe.
    ///
    /// @return the NonNullList of receptacle ingredients, never null.
    public NonNullList<Ingredient> getReceptacleIngredients() {
        return receptacleIngredients;
    }

    /// Returns the center ingredient required by this recipe.
    ///
    /// @return the center Ingredient, never null.
    public Ingredient getCenterIngredient() {
        return centerIngredient;
    }

    /// Reads and writes {@code ImbuementAltarRecipe} instances from JSON, network packets, and
    /// data generation output.
    ///
    /// Registered as the map value behind {@code EBRecipeTypes.IMBUEMENT_ALTAR_SERIALIZER}
    /// (key {@code "imbuement_altar"}). Minecraft's recipe loader calls {@code fromJson} to
    /// parse JSON recipe files from data packs and generated resources, while {@code toNetwork}
    /// and {@code fromNetwork} synchronize recipes between server and client during login and
    /// datapack reloads. The Forge data generation pipeline ({@code ImbuementAltarRecipeBuilder})
    /// serializes recipe definitions to JSON using the same field names expected by
    /// {@code fromJson}.
    public static class Serializer implements RecipeSerializer<ImbuementAltarRecipe> {
        /// Expects a {@code "receptacles"} JSON array (exactly 4 entries), a {@code "center"}
        /// ingredient object, and a {@code "result"} item object. The result may optionally
        /// include an {@code "nbt"} string for NBT-tagged outputs (used by ruined spell book
        /// repair recipes that attach a loot table). Throws {@link JsonParseException} if the
        /// receptacle count is not exactly 4 or if the NBT data is malformed.
        ///
        /// @param id the resource location for this recipe.
        /// @param json the JSON object to parse.
        /// @return a fully constructed ImbuementAltarRecipe.
        @Override
        public @NotNull ImbuementAltarRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            NonNullList<Ingredient> receptacleIngredients = NonNullList.withSize(4, Ingredient.EMPTY);
            var receptaclesArray = GsonHelper.getAsJsonArray(json, "receptacles");

            if (receptaclesArray.size() != 4) {
                throw new JsonParseException("Imbuement recipe must have exactly 4 receptacle ingredients");
            }

            for (int i = 0; i < 4; i++) {
                receptacleIngredients.set(i, Ingredient.fromJson(receptaclesArray.get(i)));
            }

            Ingredient centerIngredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "center"));
            ItemStack output = itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            return new ImbuementAltarRecipe(id, receptacleIngredients, centerIngredient, output);
        }

        /// Reads four receptacle Ingredients, the center Ingredient, and the output ItemStack
        /// in the exact fixed order written by {@code toNetwork}.
        ///
        /// @param id the resource location for this recipe.
        /// @param buf the packet buffer to read from.
        /// @return a fully constructed ImbuementAltarRecipe.
        @Override
        public @NotNull ImbuementAltarRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
            NonNullList<Ingredient> receptacleIngredients = NonNullList.withSize(4, Ingredient.EMPTY);
            for (int i = 0; i < 4; i++) {
                receptacleIngredients.set(i, Ingredient.fromNetwork(buf));
            }

            Ingredient centerIngredient = Ingredient.fromNetwork(buf);
            ItemStack output = buf.readItem();

            return new ImbuementAltarRecipe(id, receptacleIngredients, centerIngredient, output);
        }

        /// Writes the four receptacle Ingredients, the center Ingredient, and the output
        /// ItemStack in a fixed order. Must match the read order in {@code fromNetwork}.
        ///
        /// @param buf the packet buffer to write to.
        /// @param recipe the recipe to serialize.
        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, ImbuementAltarRecipe recipe) {
            for (Ingredient ingredient : recipe.receptacleIngredients) {
                ingredient.toNetwork(buf);
            }
            recipe.centerIngredient.toNetwork(buf);
            buf.writeItem(recipe.output);
        }
    }
}
