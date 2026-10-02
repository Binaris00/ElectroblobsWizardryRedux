package com.binaris.wizardry.core;

import com.binaris.wizardry.setup.registries.EBRecipeTypes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

public class ImbuementAltarRecipeBuilder implements RecipeBuilder {
    private final Ingredient centerIngredient;
    private final Ingredient[] receptacleIngredients;
    private final Item result;
    private final int count;
    @Nullable
    private CompoundTag nbt;
    @Nullable
    private String group;
    @Nullable
    private List<TagKey<Item>> poll;
    @Nullable
    private LinkedHashMap<TagKey<Item>, ResultEntry> results;

    private ImbuementAltarRecipeBuilder(Ingredient centerIngredient, Ingredient[] receptacleIngredients, ItemLike result, int count) {
        if (receptacleIngredients.length != 4) {
            throw new IllegalArgumentException("ImbuementAltarRecipe must have exactly 4 receptacle ingredients");
        }
        this.centerIngredient = centerIngredient;
        this.receptacleIngredients = receptacleIngredients;
        this.result = result.asItem();
        this.count = count;
    }

    public static ImbuementAltarRecipeBuilder imbuement(Ingredient centerIngredient, Ingredient receptacle, ItemLike result) {
        return new ImbuementAltarRecipeBuilder(centerIngredient, new Ingredient[]{receptacle, receptacle, receptacle, receptacle}, result, 1);
    }

    public static ImbuementAltarRecipeBuilder imbuement(Ingredient centerIngredient, Ingredient receptacle1, Ingredient receptacle2, Ingredient receptacle3, Ingredient receptacle4, ItemLike result) {
        return new ImbuementAltarRecipeBuilder(centerIngredient, new Ingredient[]{receptacle1, receptacle2, receptacle3, receptacle4}, result, 1);
    }

    public static ImbuementAltarRecipeBuilder imbuement(Ingredient centerIngredient, Ingredient receptacle1, Ingredient receptacle2, Ingredient receptacle3, Ingredient receptacle4, ItemLike result, int count) {
        return new ImbuementAltarRecipeBuilder(centerIngredient, new Ingredient[]{receptacle1, receptacle2, receptacle3, receptacle4}, result, count);
    }

    /// Creates a poll recipe: the four receptacles accept any item matching one of the poll tags
    /// and the outcome is randomly chosen among {@code results}, weighted by how many receptacle
    /// items match each result's tag. No {@code "receptacles"} field is written to the generated
    /// JSON since the poll tags define what can be placed. The {@code result} item is only used
    /// as metadata (e.g. for advancement lookup) and is not written to the generated JSON.
    ///
    /// @param centerIngredient the ingredient placed on the altar.
    /// @param result the representative result item.
    /// @param results the poll tags mapped to their winning output, preserving iteration order.
    public static ImbuementAltarRecipeBuilder imbuementPoll(Ingredient centerIngredient, ItemLike result, LinkedHashMap<TagKey<Item>, ResultEntry> results) {
        ImbuementAltarRecipeBuilder builder = new ImbuementAltarRecipeBuilder(centerIngredient, new Ingredient[]{Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY}, result, 1);
        builder.poll = new ArrayList<>(results.keySet());
        builder.results = results;
        return builder;
    }

    /// The output of a poll recipe entry: the item, its count and optional NBT tag data.
    public record ResultEntry(ItemLike item, int count, @Nullable CompoundTag nbt) {
    }

    public ImbuementAltarRecipeBuilder withNbt(CompoundTag nbt) {
        this.nbt = nbt;
        return this;
    }

    @Override
    public @NotNull ImbuementAltarRecipeBuilder unlockedBy(@NotNull String criterionName, @NotNull CriterionTriggerInstance criterionTrigger) {
        return this;
    }

    @Override
    public @NotNull ImbuementAltarRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return this.result;
    }

    @Override
    public void save(Consumer<FinishedRecipe> finishedRecipeConsumer, @NotNull ResourceLocation recipeId) {
        finishedRecipeConsumer.accept(new Result(recipeId, this.group == null ? "" : this.group, this.centerIngredient, this.receptacleIngredients, this.result, this.count, this.nbt, this.poll, this.results, recipeId.withPrefix("recipes/imbuement_altar/")));
    }


    static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        private final Ingredient centerIngredient;
        private final Ingredient[] receptacleIngredients;
        private final Item result;
        private final int count;
        @Nullable
        private final CompoundTag nbt;
        @Nullable
        private final List<TagKey<Item>> poll;
        @Nullable
        private final LinkedHashMap<TagKey<Item>, ResultEntry> results;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, String group, Ingredient centerIngredient, Ingredient[] receptacleIngredients, Item result, int count, @Nullable CompoundTag nbt, @Nullable List<TagKey<Item>> poll, @Nullable LinkedHashMap<TagKey<Item>, ResultEntry> results, ResourceLocation advancementId) {
            this.id = id;
            this.group = group;
            this.centerIngredient = centerIngredient;
            this.receptacleIngredients = receptacleIngredients;
            this.result = result;
            this.count = count;
            this.nbt = nbt;
            this.poll = poll;
            this.results = results;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(@NotNull JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }

            json.add("center", this.centerIngredient.toJson());

            if (this.poll != null) {
                JsonArray pollArray = new JsonArray();
                for (TagKey<Item> tag : this.poll) {
                    JsonObject pollObject = new JsonObject();
                    pollObject.addProperty("tag", tag.location().toString());
                    pollArray.add(pollObject);
                }
                json.add("poll", pollArray);

                JsonArray resultsArray = new JsonArray();
                for (var entry : this.results.entrySet()) {
                    ResultEntry resultEntry = entry.getValue();
                    JsonObject resultObject = new JsonObject();
                    resultObject.addProperty("key", entry.getKey().location().toString());
                    resultObject.addProperty("item", BuiltInRegistries.ITEM.getKey(resultEntry.item().asItem()).toString());
                    resultObject.addProperty("count", resultEntry.count());
                    if (resultEntry.nbt() != null) {
                        resultObject.addProperty("nbt", resultEntry.nbt().toString());
                    }
                    resultsArray.add(resultObject);
                }
                json.add("results", resultsArray);
                return;
            }

            JsonArray receptaclesArray = new JsonArray();
            for (Ingredient ingredient : this.receptacleIngredients) {
                receptaclesArray.add(ingredient.toJson());
            }
            json.add("receptacles", receptaclesArray);

            JsonObject resultObject = new JsonObject();
            resultObject.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
            resultObject.addProperty("count", this.count);
            if (this.nbt != null) {
                resultObject.addProperty("nbt", this.nbt.toString());
            }
            json.add("result", resultObject);
        }

        @Override
        public RecipeSerializer<?> getType() {
            return EBRecipeTypes.IMBUEMENT_ALTAR_SERIALIZER;
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        @Nullable
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
