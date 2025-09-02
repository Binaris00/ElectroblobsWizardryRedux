package com.electroblob.wizardry.content.advancement;

import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.core.platform.Services;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SpellPredicate {
    private final Spell spell;
    private final Set<SpellTier> tiers;
    private final Set<Element> elements;

    private SpellPredicate(@Nullable Spell spell, Set<SpellTier> tiers, Set<Element> elements) {
        this.spell = spell;
        this.tiers = tiers;
        this.elements = elements;
    }

    public static SpellPredicate any() {
        Set<SpellTier> trs = new HashSet<>(Services.REGISTRY_UTIL.getTiers());
        Set<Element> els = new HashSet<>(Services.REGISTRY_UTIL.getElements());
        return new SpellPredicate(null, trs, els);
    }

    public boolean test(Spell spell) {
        if (this.spell != null && !this.spell.equals(spell)) {
            return false;
        }
        if (!this.tiers.contains(spell.getTier())) {
            return false;
        }
        return this.elements.contains(spell.getElement());
    }

    public static SpellPredicate deserialize(@Nullable JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return any();
        }
        JsonObject json = GsonHelper.convertToJsonObject(element, "spell");

        Spell spell = Optional.ofNullable(json.get("spell"))
                .map(JsonElement::getAsString).map(ResourceLocation::tryParse)
                .map(Services.REGISTRY_UTIL::getSpell).orElse(null);

        if (json.has("spell") && spell == null)
            throw new JsonSyntaxException("Unknown spell id '" + json.get("spell").getAsString() + "'");


        Set<SpellTier> tiers = json.has("tiers")
                ? json.getAsJsonArray("tiers").asList().stream()
                .map(JsonElement::getAsString).map(ResourceLocation::tryParse).map(Services.REGISTRY_UTIL::getTier)
                .collect(Collectors.toSet()) : new HashSet<>(Services.REGISTRY_UTIL.getTiers());

        Set<Element> elements = json.has("elements")
                ? json.getAsJsonArray("elements").asList().stream()
                .map(JsonElement::getAsString).map(ResourceLocation::tryParse)
                .map(Services.REGISTRY_UTIL::getElement).collect(Collectors.toSet())
                : new HashSet<>(Services.REGISTRY_UTIL.getElements());

        return new SpellPredicate(spell, tiers, elements);
    }
}