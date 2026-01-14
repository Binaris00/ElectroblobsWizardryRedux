package com.binaris.wizardry.core.platform.services;

import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellTier;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface IRegistryUtil {
    Collection<Element> getElements();

    Collection<SpellTier> getTiers();

    Collection<Spell> getSpells();

    @Nullable Element getElement(ResourceLocation location);

    @Nullable
    SpellTier getTier(ResourceLocation location);

    @Nullable Spell getSpell(ResourceLocation location);

    @Nullable ResourceLocation getSpell(Spell spell);

    @Nullable ResourceLocation getElement(Element element);

    @Nullable ResourceLocation getTier(SpellTier tier);
}
