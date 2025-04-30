package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.Tier;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class EBRegister {
    private EBRegister() {}

    public static void registerSpells(Consumer<Set<Map.Entry<String, Spell>>> handler) {
        //Spells.handleRegistration(handler);
    }

    public static void registerTiers(Consumer<Set<Map.Entry<String, Tier>>> handler) {
        //Tiers.handleRegistration(handler);
    }
}
