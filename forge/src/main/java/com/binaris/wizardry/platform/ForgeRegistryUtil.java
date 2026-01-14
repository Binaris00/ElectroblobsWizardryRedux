package com.binaris.wizardry.platform;

import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.core.platform.services.IRegistryUtil;
import com.binaris.wizardry.registry.EBRegistriesForge;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Comparator;

public class ForgeRegistryUtil implements IRegistryUtil {
    @Override
    public Collection<Element> getElements() {
        return EBRegistriesForge.ELEMENT.get().getValues().stream().toList();
    }

    @Override
    public Collection<SpellTier> getTiers() {
        return EBRegistriesForge.TIER.get().getValues().stream()
                .sorted(Comparator.comparingInt(t -> t.level))
                .toList();
    }

    @Override
    public Collection<Spell> getSpells() {
        return EBRegistriesForge.SPELL.get().getValues().stream().toList();
    }

    @Override
    public @Nullable Element getElement(ResourceLocation location) {
        return EBRegistriesForge.ELEMENT.get().getValue(location);
    }

    @Override
    public @Nullable SpellTier getTier(ResourceLocation location) {
        return EBRegistriesForge.TIER.get().getValue(location);
    }

    @Override
    public @Nullable Spell getSpell(ResourceLocation location) {
        return EBRegistriesForge.SPELL.get().getValue(location);
    }

    @Override
    public @Nullable ResourceLocation getSpell(Spell spell) {
        return EBRegistriesForge.SPELL.get().getKey(spell);
    }

    @Override
    public @Nullable ResourceLocation getElement(Element element) {
        return EBRegistriesForge.ELEMENT.get().getKey(element);
    }

    @Override
    public @Nullable ResourceLocation getTier(SpellTier tier) {
        return EBRegistriesForge.TIER.get().getKey(tier);
    }
}
