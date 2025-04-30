package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.spell.Tier;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Tiers {
    public static Map<String, Tier> TIERS = new HashMap<>();
    private Tiers() {}

    public static final Tier NOVICE = tier("novice", () -> new Tier(700, 3, 12, 0, ChatFormatting.WHITE));
    public static final Tier APPRENTICE = tier("apprentice", () -> new Tier(1000, 5, 5, 1, ChatFormatting.AQUA));
    public static final Tier ADVANCED = tier("advanced", () -> new Tier(1500, 7, 2, 2, ChatFormatting.DARK_BLUE));
    public static final Tier MASTER = tier("master", () -> new Tier(2500, 9, 1, 3, ChatFormatting.DARK_PURPLE));

    // ======= Registry =======
    public static void registerNull(RegisterFunction<Tier> function){
        register(null, function);
    }

    @SuppressWarnings("unchecked")
    public static void register(Registry<?> registry, RegisterFunction<Tier> function){
        TIERS.forEach(((id, tier) ->
                function.register((Registry<Tier>) registry, WizardryMainMod.location(id), tier)));
    }

    //
    static Tier tier(String name, Supplier<Tier> tierSupplier) {
        var tier = tierSupplier.get();
        TIERS.put(name, tier);
        return tier;
    }

    // TODO MAYBE CHECK ADDON COMPATIBILITY
    public static Tier getNextByLevel(int level){
        for(Tier tier : TIERS.values()){
            if(tier.level == level) return tier;
        }
        return null;
    }
}
