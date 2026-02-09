package com.binaris.wizardry.api.content.spell;

import com.binaris.wizardry.core.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpellTier {
    final int maxCharge;
    final int level;
    final int upgradeLimit;
    final int weight;
    final int progression;
    final ChatFormatting color;
    private String descriptionId;
    private ResourceLocation location;

    public SpellTier(int maxCharge, int upgradeLimit, int weight, int level, ChatFormatting color, int progression) {
        this.maxCharge = maxCharge;
        this.level = level;
        this.upgradeLimit = upgradeLimit;
        this.weight = weight;
        this.color = color;
        this.progression = progression;
    }

    /**
     * Returns a random tier based on the standard weighting. Currently, the standard weighting is: Basic (Novice) 60%,
     * Apprentice 25%, Advanced 10%, Master 5%. If an array of tiers is given, it picks a tier from the array, with the
     * same relative weights for each. For example, if the array contains APPRENTICE and MASTER, then the weighting will
     * become: Apprentice 83.3%, Master 16.7%.
     */
    public static SpellTier getWeightedRandomTier(RandomSource random, SpellTier... tiers) {
        // TODO
        //if(tiers.length == 0) tiers = values();

        int totalWeight = 0;

        for (SpellTier tier : tiers) totalWeight += tier.weight;

        int randomiser = random.nextInt(totalWeight);
        int cumulativeWeight = 0;

        for (SpellTier tier : tiers) {
            cumulativeWeight += tier.weight;
            if (randomiser < cumulativeWeight) return tier;
        }

        // This will never happen, but it might as well be a sensible result.
        return tiers[tiers.length - 1];
    }

    /**
     * Returns the tier above this one, or the same tier if this is the highest tier.
     */
    public SpellTier next() {
        List<SpellTier> tiers = Services.REGISTRY_UTIL.getTiers().stream().toList();
        int thisTierID = tiers.indexOf(this);

        return thisTierID < Services.REGISTRY_UTIL.getTiers().size() - 1 ?
                tiers.get(thisTierID + 1)
                : tiers.get(tiers.size() - 1); // Last tier
    }

    // ===================================================
    // NAME AND FORMATTING
    // ==================================================

    /**
     * Returns the tier below this one, or the same tier if this is the lowest tier.
     */
    public SpellTier previous() {
        List<SpellTier> tiers = Services.REGISTRY_UTIL.getTiers().stream().toList();
        int thisTierID = tiers.indexOf(this);

        return thisTierID > 0 ?
                tiers.get(thisTierID - 1)
                : tiers.get(0); // First tier
    }

    /**
     * Returns the description/name translatable for this tier formatted with the color
     */
    public Component getDescriptionFormatted() {
        return Component.translatable(getOrCreateDescriptionId()).withStyle(this.color);
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null)
            this.descriptionId = Util.makeDescriptionId("tier", Services.REGISTRY_UTIL.getTier(this));
        return this.descriptionId;
    }

    /**
     * Will return the description ID for the tier (e.g. "tier.ebwizardry.novice")
     * if you want the location instead, use {@link #getOrCreateLocation()}
     */
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    /**
     * Will return the location for the tier, or null if it hasn't been registered yet (e.g. "ebwizardry:novice")
     */
    public @Nullable ResourceLocation getOrCreateLocation() {
        if (this.location == null) this.location = Services.REGISTRY_UTIL.getTier(this);
        return this.location;
    }

    /**
     * Will return the location for the tier (e.g. "ebwizardry:novice")
     */
    public ResourceLocation getLocation() {
        return this.getOrCreateLocation();
    }

    /**
     * Sets the location for this tier. This should only be called during registration.
     *
     * @param location The resource location for this tier
     */
    public void setLocation(ResourceLocation location) {
        if (this.location != null) {
            throw new IllegalStateException("Location already set for tier");
        }
        this.location = location;
    }

    /**
     * Will return true if the tier is registered at the given location
     */
    public final boolean is(ResourceLocation location) {
        return location.equals(getLocation());
    }

    @Override
    public String toString() {
        return getLocation().toString();
    }

    // ===================================================

    public ChatFormatting getColor() {
        return color;
    }

    public int getLevel() {
        return level;
    }

    public int getMaxCharge() {
        return maxCharge;
    }

    public int getUpgradeLimit() {
        return upgradeLimit;
    }

    public int getWeight() {
        return weight;
    }

    // TODO EBCONFIG
    public int getProgression() {
        return progression;
    }
}
