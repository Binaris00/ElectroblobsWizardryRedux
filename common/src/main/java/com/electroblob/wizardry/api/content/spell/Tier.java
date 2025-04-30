package com.electroblob.wizardry.api.content.spell;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.Tiers;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Random;

public class Tier {
    public final int maxCharge;
    public final int level;
    public final int upgradeLimit;
    public final int weight;
    private final ChatFormatting color;

    private String descriptionId;
    private final ResourceLocation location;

    public Tier(String name, int maxCharge, int upgradeLimit, int weight, int level, ChatFormatting color){
        this(WizardryMainMod.location(name), maxCharge, upgradeLimit, weight, level, color);
    }

    public Tier(ResourceLocation location, int maxCharge, int upgradeLimit, int weight, int level, ChatFormatting color){
        this.location = location;
        this.maxCharge = maxCharge;
        this.level = level;
        this.upgradeLimit = upgradeLimit;
        this.weight = weight;
        this.color = color;

    }

    /** Returns the tier above this one, or the same tier if this is the highest tier. */
    public Tier next(){
        // TODO TIER NEXT()
        //return ordinal() + 1 < values().length ? values()[ordinal() + 1] : this;
        return Tiers.NOVICE;
    }

    /** Returns the tier below this one, or the same tier if this is the lowest tier. */
    public Tier previous(){
        // TODO TIER PREVIOUS()
        //return ordinal() > 0 ? values()[ordinal() - 1] : this;
        return Tiers.NOVICE;
    }

    // ===================================================
    // NAME AND FORMATTING
    // ==================================================
    /** Returns the description/name translatable for this tier formatted with the color */
    public Component getDescriptionFormatted(){
        return Component.translatable(getOrCreateDescriptionId()).withStyle(this.color);
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) this.descriptionId = Util.makeDescriptionId("tier", Services.REGISTRY_UTIL.getTier(this));
        return this.descriptionId;
    }

    /** Will return the description ID for the tier (e.g. "tier.ebwizardry.novice")
     * if you want the location instead, use {@link #getLocation()} */
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    /** Will return the location for the tier (e.g. "ebwizardry:novice") */
    public ResourceLocation getLocation() {
        return this.location;
    }

    /** Will return true if the tier is registered at the given location */
    public final boolean is(ResourceLocation location) {
        return location.equals(getLocation());
    }

    /** Will return true if the tier is registered at the given location */
    public final boolean is(String location) {
        return location.equals(getLocation().toString());
    }

    // ===================================================


    public ChatFormatting getColor() {
        return color;
    }

    // TODO EBCONFIG
    public int getProgression(){
        return 1;
        //return Wizardry.settings.progressionRequirements[this.ordinal() - 1];
    }


    /**
     * Returns a random tier based on the standard weighting. Currently, the standard weighting is: Basic (Novice) 60%,
     * Apprentice 25%, Advanced 10%, Master 5%. If an array of tiers is given, it picks a tier from the array, with the
     * same relative weights for each. For example, if the array contains APPRENTICE and MASTER, then the weighting will
     * become: Apprentice 83.3%, Master 16.7%.
     */
    public static Tier getWeightedRandomTier(Random random, Tier... tiers){
        // TODO
        //if(tiers.length == 0) tiers = values();

        int totalWeight = 0;

        for(Tier tier : tiers) totalWeight += tier.weight;

        int randomiser = random.nextInt(totalWeight);
        int cumulativeWeight = 0;

        for(Tier tier : tiers){
            cumulativeWeight += tier.weight;
            if(randomiser < cumulativeWeight) return tier;
        }

        // This will never happen, but it might as well be a sensible result.
        return tiers[tiers.length - 1];
    }
}
