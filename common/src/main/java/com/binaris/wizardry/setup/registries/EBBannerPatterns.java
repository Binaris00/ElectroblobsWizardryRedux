package com.binaris.wizardry.setup.registries;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.DeferredObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EBBannerPatterns {
    public static final Map<String, DeferredObject<BannerPattern>> BANNER_PATTERNS = new HashMap<>();

    public static final DeferredObject<BannerPattern> EARTH = bannerPattern("earth", () -> new BannerPattern("ebe"));
    public static final DeferredObject<BannerPattern> FIRE = bannerPattern("fire", () -> new BannerPattern("ebf"));
    public static final DeferredObject<BannerPattern> HEALING = bannerPattern("healing", () -> new BannerPattern("ebh"));
    public static final DeferredObject<BannerPattern> ICE = bannerPattern("ice", () -> new BannerPattern("ebi"));
    public static final DeferredObject<BannerPattern> LIGHTNING = bannerPattern("lightning", () -> new BannerPattern("ebl"));
    public static final DeferredObject<BannerPattern> NECROMANCY = bannerPattern("necromancy", () -> new BannerPattern("ebn"));
    public static final DeferredObject<BannerPattern> SORCERY = bannerPattern("sorcery", () -> new BannerPattern("ebs"));

    public static void register(RegisterFunction<BannerPattern> function) {
        BANNER_PATTERNS.forEach((id, bannerPatterns) -> function.register(
                BuiltInRegistries.BANNER_PATTERN, WizardryMainMod.location(id), new BannerPattern(bannerPatterns.get().getHashname())
        ));
    }

    public static DeferredObject<BannerPattern> bannerPattern(String name, Supplier<BannerPattern> supplier) {
        DeferredObject<BannerPattern> bannerPattern = new DeferredObject<>(supplier);
        BANNER_PATTERNS.put(name, bannerPattern);
        return bannerPattern;
    }
}
