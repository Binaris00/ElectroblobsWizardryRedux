package com.binaris.wizardry.setup.registries;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.content.recipe.ImbuementAltarRecipe;
import com.binaris.wizardry.content.recipe.MagicRepairRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

import java.util.HashMap;
import java.util.Map;

public final class EBRecipeTypes {
    static Map<String, RecipeType<?>> RECIPE_TYPES = new HashMap<>();
    public static final RecipeType<ImbuementAltarRecipe> IMBUEMENT_ALTAR = recipe("imbuement_altar");
    public static final RecipeType<MagicRepairRecipe> MAGIC_REPAIR = recipe("crafting_special_magicrepair");
    static Map<String, RecipeSerializer<?>> RECIPE_SERIALIZERS = new HashMap<>();
    public static final RecipeSerializer<ImbuementAltarRecipe> IMBUEMENT_ALTAR_SERIALIZER = serializer("imbuement_altar", new ImbuementAltarRecipe.Serializer());
    public static final RecipeSerializer<MagicRepairRecipe> MAGIC_REPAIR_SERIALIZER = serializer("crafting_special_magicrepair", new SimpleCraftingRecipeSerializer<>(MagicRepairRecipe::new));

    private EBRecipeTypes() {
    }

    // ======= Registry =======
    public static void register(RegisterFunction<RecipeType<?>> function) {
        RECIPE_TYPES.forEach((name, type) -> function.register(BuiltInRegistries.RECIPE_TYPE, WizardryMainMod.location(name), type));
    }

    public static void registerSerializers(RegisterFunction<RecipeSerializer<?>> function) {
        RECIPE_SERIALIZERS.forEach((name, serializer) -> function.register(BuiltInRegistries.RECIPE_SERIALIZER, WizardryMainMod.location(name), serializer));
    }

    // ======= Helpers =======
    static <T extends Recipe<?>> RecipeType<T> recipe(String name) {
        RecipeType<T> ret = new RecipeType<>() {
            @Override
            public String toString() {
                return name;
            }
        };
        RECIPE_TYPES.put(name, ret);
        return ret;
    }

    static <T extends RecipeSerializer<?>> T serializer(String name, T serializer) {
        RECIPE_SERIALIZERS.put(name, serializer);
        return serializer;
    }
}
