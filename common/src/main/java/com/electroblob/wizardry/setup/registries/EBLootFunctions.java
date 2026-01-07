package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import com.electroblob.wizardry.content.loot.RandomSpellFunction;
import com.electroblob.wizardry.content.loot.WizardSpellFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.resources.ResourceLocation;
import java.util.LinkedHashMap;
import java.util.Map;

public final class EBLootFunctions {

    private static final Map<ResourceLocation, LootItemFunctionType> FUNCTIONS_TO_REGISTER = new LinkedHashMap<>();

    public static final LootItemFunctionType RANDOM_SPELL = register("random_spell", new LootItemFunctionType(new RandomSpellFunction.Serializer()));
    public static final LootItemFunctionType WIZARD_SPELL = register("wizard_spell", new LootItemFunctionType(new WizardSpellFunction.Serializer()));

    private EBLootFunctions() {}

    private static LootItemFunctionType register(String name, LootItemFunctionType type) {
        FUNCTIONS_TO_REGISTER.put(WizardryMainMod.location(name), type);
        return type;
    }

    public static void register(RegisterFunction<LootItemFunctionType> function) {
        FUNCTIONS_TO_REGISTER.forEach(((id, loot_function) ->
                function.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, id, loot_function)));
    }
}