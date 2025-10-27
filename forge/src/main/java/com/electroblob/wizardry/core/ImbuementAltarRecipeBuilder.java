package com.electroblob.wizardry.core;

import com.electroblob.wizardry.setup.registries.EBRecipeTypes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        finishedRecipeConsumer.accept(new Result(recipeId, this.group == null ? "" : this.group, this.centerIngredient, this.receptacleIngredients, this.result, this.count, this.nbt, recipeId.withPrefix("recipes/imbuement_altar/")));
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
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, String group, Ingredient centerIngredient, Ingredient[] receptacleIngredients, Item result, int count, @Nullable CompoundTag nbt, ResourceLocation advancementId) {
            this.id = id;
            this.group = group;
            this.centerIngredient = centerIngredient;
            this.receptacleIngredients = receptacleIngredients;
            this.result = result;
            this.count = count;
            this.nbt = nbt;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(@NotNull JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }

            json.add("center", this.centerIngredient.toJson());

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
