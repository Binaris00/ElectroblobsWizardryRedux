package com.binaris.wizardry.core.platform.services;

import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellTier;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/// Platform abstraction providing read-only access to the mod's custom registries (elements, spell tiers, spells).
///
/// Provides forward lookups (ResourceLocation → registry entry) and reverse lookups (registry entry → ResourceLocation)
/// for the three custom registry types: [Element], [SpellTier], and [Spell]. Backed by loader-specific
/// implementations — Fabric uses {@code MappedRegistry}, Forge uses {@code IForgeRegistry}. Obtained via
/// {@code Services.REGISTRY_UTIL}, which resolves the implementation at class-load time through Java {@code ServiceLoader}.
public interface IRegistryUtil {
    /// Returns all registered elements. The collection is unordered (implementation-defined iteration order).
    ///
    /// @return a live-collection copy of all registered elements
    Collection<Element> getElements();

    /// Returns all registered spell tiers, sorted by tier level in ascending order.
    ///
    /// Both Fabric and Forge implementations sort the underlying registry values by [SpellTier#getLevel()]
    /// before returning, so callers can safely index into the returned list for tier-adjacency navigation.
    ///
    /// @return an ordered list of all registered tiers, from lowest level to highest
    Collection<SpellTier> getTiers();

    /// Returns all registered spells. The collection is unordered.
    ///
    /// @return a live-collection copy of all registered spells
    Collection<Spell> getSpells();

    /// Looks up an element by its registry key. Returns {@code null} if the given location is not registered.
    ///
    /// @param location the registry {@code ResourceLocation} of the element
    /// @return the matching {@link Element}, or {@code null} if not found
    @Nullable Element getElement(ResourceLocation location);

    /// Looks up a spell tier by its registry key. Returns {@code null} if the given location is not registered.
    ///
    /// @param location the registry {@code ResourceLocation} of the tier
    /// @return the matching [SpellTier], or {@code null} if not found
    @Nullable
    SpellTier getTier(ResourceLocation location);

    /// Looks up a spell by its registry key. Returns {@code null} if the given location is not registered.
    ///
    /// @param location the registry {@code ResourceLocation} of the spell
    /// @return the matching [Spell], or {@code null} if not found
    @Nullable Spell getSpell(ResourceLocation location);

    /// Reverse lookup — returns the registry key for a given spell. Returns {@code null} if the spell
    /// has not been registered (e.g. a transient object that was never added to the registry).
    ///
    /// @param spell the [Spell] instance to look up
    /// @return the {@code ResourceLocation} key, or {@code null} if not registered
    @Nullable ResourceLocation getSpell(Spell spell);

    /// Reverse lookup — returns the registry key for a given element.
    ///
    /// @param element the [Element] instance to look up
    /// @return the {@code ResourceLocation} key, or {@code null} if not registered
    @Nullable ResourceLocation getElement(Element element);

    /// Reverse lookup — returns the registry key for a given spell tier.
    ///
    /// @param tier the [SpellTier] instance to look up
    /// @return the {@code ResourceLocation} key, or {@code null} if not registered
    @Nullable ResourceLocation getTier(SpellTier tier);
}
