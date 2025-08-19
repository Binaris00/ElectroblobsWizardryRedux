package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class SpellTiers {
    public static Map<String, SpellTier> TIERS = new HashMap<>();
    private SpellTiers() {}

    public static final SpellTier NOVICE = tier("novice", () -> new SpellTier("novice", 700, 3, 12, 0, ChatFormatting.WHITE, 100));
    public static final SpellTier APPRENTICE = tier("apprentice", () -> new SpellTier("apprentice", 1500, 5, 5, 1, ChatFormatting.AQUA, 200));
    public static final SpellTier ADVANCED = tier("advanced", () -> new SpellTier("advanced", 3500, 7, 2, 2, ChatFormatting.DARK_BLUE, 300));
    public static final SpellTier MASTER = tier("master", () -> new SpellTier("master",6000, 9, 1, 3, ChatFormatting.DARK_PURPLE, 400));

    // ======= Registry =======
    public static void registerNull(RegisterFunction<SpellTier> function){
        register(null, function);
    }

    @SuppressWarnings("unchecked")
    public static void register(Registry<?> registry, RegisterFunction<SpellTier> function){
        TIERS.forEach(((id, tier) ->
                function.register((Registry<SpellTier>) registry, WizardryMainMod.location(id), tier)));
    }

    //
    static SpellTier tier(String name, Supplier<SpellTier> tierSupplier) {
        var tier = tierSupplier.get();
        TIERS.put(name, tier);
        return tier;
    }

    // TODO Check and rewrite logic for spell tiers in order to allow addon compatibility
    public static SpellTier getNextByLevel(int level){
        for(SpellTier tier : TIERS.values()){
            if(tier.level == level) return tier;
        }
        return null;
    }
}