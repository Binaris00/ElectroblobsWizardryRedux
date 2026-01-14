package com.binaris.wizardry.setup.registries;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.api.content.util.RegisterFunction;
import com.binaris.wizardry.content.spell_tier.AdvancedTier;
import com.binaris.wizardry.content.spell_tier.ApprenticeTier;
import com.binaris.wizardry.content.spell_tier.MasterTier;
import com.binaris.wizardry.content.spell_tier.NoviceTier;
import net.minecraft.core.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class SpellTiers {
    public static Map<String, SpellTier> TIERS = new HashMap<>();
    public static final SpellTier NOVICE = tier("novice", NoviceTier::new);
    public static final SpellTier APPRENTICE = tier("apprentice", ApprenticeTier::new);
    public static final SpellTier ADVANCED = tier("advanced", AdvancedTier::new);
    public static final SpellTier MASTER = tier("master", MasterTier::new);

    private SpellTiers() {
    }

    // ======= Registry =======
    public static void registerNull(RegisterFunction<SpellTier> function) {
        register(null, function);
    }

    @SuppressWarnings("unchecked")
    public static void register(Registry<?> registry, RegisterFunction<SpellTier> function) {
        TIERS.forEach(((id, tier) ->
                function.register((Registry<SpellTier>) registry, WizardryMainMod.location(id), tier)));
    }

    static SpellTier tier(String name, Supplier<SpellTier> tierSupplier) {
        var tier = tierSupplier.get();
        tier.setLocation(WizardryMainMod.location(name));
        TIERS.put(name, tier);
        return tier;
    }

    // TODO Check and rewrite logic for spell tiers in order to allow addon compatibility
    public static SpellTier getNextByLevel(int level) {
        for (SpellTier tier : TIERS.values()) {
            if (tier.level == level) return tier;
        }
        return null;
    }
}