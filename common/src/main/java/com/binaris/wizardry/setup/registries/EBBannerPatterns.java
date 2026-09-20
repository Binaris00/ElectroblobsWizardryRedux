package com.binaris.wizardry.setup.registries;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.DeferredObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.HashMap;
import java.util.Map;

public final class EBBannerPatterns {
    static final Map<String, DeferredObject<BannerPattern>> PATTERNS_REGISTER = new HashMap<>();

    public static final DeferredObject<BannerPattern> EARTH = pattern("earth");
    public static final DeferredObject<BannerPattern> FIRE = pattern("fire");
    public static final DeferredObject<BannerPattern> HEALING = pattern("healing");
    public static final DeferredObject<BannerPattern> ICE = pattern("ice");
    public static final DeferredObject<BannerPattern> LIGHTNING = pattern("lightning");
    public static final DeferredObject<BannerPattern> NECROMANCY = pattern("necromancy");
    public static final DeferredObject<BannerPattern> SORCERY = pattern("sorcery");

    private EBBannerPatterns() {
    }

    static DeferredObject<BannerPattern> pattern(String name) {
        var ret = new DeferredObject<>(() -> new BannerPattern(bannerHash(name)));
        PATTERNS_REGISTER.put(name, ret);
        return ret;
    }

    /**
     * Unique short code stored in banner NBT, mirrors the original mod's "eb" + initial scheme.
     */
    private static String bannerHash(String name) {
        return "eb" + name.charAt(0);
    }

    // ======= Registry =======
    public static void register(RegisterFunction<BannerPattern> function) {
        PATTERNS_REGISTER.forEach((id, pattern) ->
                function.register(BuiltInRegistries.BANNER_PATTERN, WizardryMainMod.location(id), pattern.get()));
    }
}
