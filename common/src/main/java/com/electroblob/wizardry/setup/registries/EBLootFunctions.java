package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import com.electroblob.wizardry.content.loot.RandomSpellFunction;
import com.electroblob.wizardry.content.loot.WizardSpellFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class EBLootFunctions {
    static Map<String, Supplier<LootItemFunctionType>> LOOT_FUNCTIONS = new HashMap<>();
    public static final Supplier<LootItemFunctionType> RANDOM_SPELL = lootFunction("random_spell", () -> new LootItemFunctionType(new RandomSpellFunction.Serializer()));
    public static final Supplier<LootItemFunctionType> WIZARD_SPELL = lootFunction("wizard_spell", () -> new LootItemFunctionType(new WizardSpellFunction.Serializer()));
    private EBLootFunctions() {
    }

    // ======= Registry =======
    public static void register(RegisterFunction<LootItemFunctionType> function) {
        LOOT_FUNCTIONS.forEach(((id, loot_function) ->
                function.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, WizardryMainMod.location(id), loot_function.get())));
    }

    static Supplier<LootItemFunctionType> lootFunction(String name, Supplier<LootItemFunctionType> loot_function) {
        LOOT_FUNCTIONS.put(name, loot_function);
        return loot_function;
    }
}
