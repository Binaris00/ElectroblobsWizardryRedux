package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.api.content.spell.Tier;
import net.minecraft.ChatFormatting;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Tiers {
    private Tiers() {}

    public static final Tier NOVICE = Register.tier("novice", () -> new Tier("novice",700,
            3, 12, 0, ChatFormatting.WHITE));
    public static final Tier APPRENTICE = Register.tier("apprentice", () -> new Tier("apprentice",1000,
            5, 5, 1, ChatFormatting.AQUA));
    public static final Tier ADVANCED = Register.tier("advanced", () -> new Tier("advanced",1500,
            7, 2, 2, ChatFormatting.DARK_BLUE));
    public static final Tier MASTER = Register.tier("master", () -> new Tier("master",2500,
            9, 1, 3, ChatFormatting.DARK_PURPLE));

    static void handleRegistration(Consumer<Set<Map.Entry<String, Tier>>> handler) {
        handler.accept(Collections.unmodifiableSet(Register.TIERS.entrySet()));
    }

    public static class Register {
        public static Map<String, Tier> TIERS = new HashMap<>();

        static Tier tier(String name, Supplier<Tier> tierSupplier) {
            var tier = tierSupplier.get();
            TIERS.put(name, tier);
            return tier;
        }
    }
}
