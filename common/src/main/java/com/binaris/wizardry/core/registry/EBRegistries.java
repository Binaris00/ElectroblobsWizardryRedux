package com.binaris.wizardry.core.registry;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellTier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public final class EBRegistries {
    public static final ResourceKey<Registry<Element>> ELEMENT = ResourceKey.createRegistryKey(WizardryMainMod.location("elements"));
    public static final ResourceKey<Registry<SpellTier>> TIER = ResourceKey.createRegistryKey(WizardryMainMod.location("tiers"));
    public static final ResourceKey<Registry<Spell>> SPELL = ResourceKey.createRegistryKey(WizardryMainMod.location("spells"));

    private EBRegistries() {
    }
}
