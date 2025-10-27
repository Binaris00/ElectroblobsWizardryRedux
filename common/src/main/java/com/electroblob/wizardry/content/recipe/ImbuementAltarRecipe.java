package com.electroblob.wizardry.content.recipe;

import com.electroblob.wizardry.setup.registries.EBRecipeTypes;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public class ImbuementAltarRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final NonNullList<Ingredient> receptacleIngredients;
    private final Ingredient centerIngredient;
    private final ItemStack output;

    public ImbuementAltarRecipe(ResourceLocation id, NonNullList<Ingredient> receptacleIngredients,
                                Ingredient centerIngredient, ItemStack output) {
        this.id = id;
        this.receptacleIngredients = receptacleIngredients;
        this.centerIngredient = centerIngredient;
        this.output = output;
    }

    private static ItemStack itemStackFromJson(JsonObject stackObject) {
        Item item = ShapedRecipe.itemFromJson(stackObject);
        int count = GsonHelper.getAsInt(stackObject, "count", 1);

        if (count < 1) {
            throw new JsonSyntaxException("Invalid output count: " + count);
        }

        ItemStack stack = new ItemStack(item, count);

        // Soporte para NBT data
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

    public NonNullList<Ingredient> getReceptacleIngredients() {
        return receptacleIngredients;
    }

    public Ingredient getCenterIngredient() {
        return centerIngredient;
    }

    public static class Serializer implements RecipeSerializer<ImbuementAltarRecipe> {
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
