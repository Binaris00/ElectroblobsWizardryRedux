package com.electroblob.wizardry.core.platform.services;

import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.Tier;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface IRegistryUtil {
    Collection<Element> getElements();

    Collection<Tier> getTiers();

    Collection<Spell> getSpells();

    @Nullable Element getElement(ResourceLocation location);

    @Nullable Tier getTier(ResourceLocation location);

    @Nullable Spell getSpell(ResourceLocation location);

    @Nullable ResourceLocation getSpell(Spell spell);

    @Nullable ResourceLocation getElement(Element element);

    @Nullable ResourceLocation getTier(Tier tier);
}
