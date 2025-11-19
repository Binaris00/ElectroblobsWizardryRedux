package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class EBEnchantments {
    static Map<String, DeferredObject<Enchantment>> ENCHANTMENTS = new HashMap<>();

    private EBEnchantments() {
    }

    // ======= Registry =======
    public static void register(RegisterFunction<Enchantment> function) {
        ENCHANTMENTS.forEach(((id, enchantment) ->
                function.register(BuiltInRegistries.ENCHANTMENT, WizardryMainMod.location(id), enchantment.get())));
    }

    // ======= Helpers =======
    static DeferredObject<Enchantment> enchantment(String name, Enchantment enchantment) {
        DeferredObject<Enchantment> deferredEnchant = new DeferredObject<>(() -> enchantment);
        ENCHANTMENTS.put(name, deferredEnchant);
        return deferredEnchant;
    }
}
