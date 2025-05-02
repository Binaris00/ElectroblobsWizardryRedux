package com.electroblob.wizardry.platform;

import com.electroblob.wizardry.EBRegistriesFabric;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.core.platform.services.IRegistryUtil;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Comparator;

public class FabricRegistryUtil implements IRegistryUtil {
    @Override
    public Collection<Element> getElements() {
        return EBRegistriesFabric.ELEMENTS.stream().toList();
    }

    @Override
    public Collection<SpellTier> getTiers() {
        return EBRegistriesFabric.TIERS.stream()
                .sorted(Comparator.comparingInt(t -> t.level))
                .toList();
    }

    @Override
    public Collection<Spell> getSpells() {
        return EBRegistriesFabric.SPELLS.stream().toList();
    }

    @Override
    public @Nullable Element getElement(ResourceLocation location) {
        return EBRegistriesFabric.ELEMENTS.get(location);
    }

    @Override
    public @Nullable SpellTier getTier(ResourceLocation location) {
        return EBRegistriesFabric.TIERS.get(location);
    }

    @Override
    public @Nullable Spell getSpell(ResourceLocation location) {
        return EBRegistriesFabric.SPELLS.get(location);
    }

    @Override
    public @Nullable ResourceLocation getSpell(Spell spell) {
        return EBRegistriesFabric.SPELLS.getKey(spell);
    }

    @Override
    public @Nullable ResourceLocation getElement(Element element) {
        return EBRegistriesFabric.ELEMENTS.getKey(element);
    }

    @Override
    public @Nullable ResourceLocation getTier(SpellTier tier) {
        return EBRegistriesFabric.TIERS.getKey(tier);
    }
}
