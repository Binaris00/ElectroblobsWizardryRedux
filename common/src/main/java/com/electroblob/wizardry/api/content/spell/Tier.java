package com.electroblob.wizardry.api.content.spell;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.setup.registries.Tiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Random;

public class Tier {
    public final int maxCharge;
    public final int level;
    public final int upgradeLimit;
    public final int weight;
    private final ChatFormatting colour;
    private final ResourceLocation unlocalisedName;

    public Tier(String name, int maxCharge, int upgradeLimit, int weight, int level, ChatFormatting colour){
        this(WizardryMainMod.MOD_ID, name, maxCharge, upgradeLimit, weight, level, colour);
    }

    Tier(String modid, String name, int maxCharge, int upgradeLimit, int weight, int level, ChatFormatting colour){
        this.maxCharge = maxCharge;
        this.level = level;
        this.upgradeLimit = upgradeLimit;
        this.weight = weight;
        this.colour = colour;
        this.unlocalisedName = new ResourceLocation(modid, name);
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

    public Component getNameForTranslation(){
        return Component.translatable("tier." + unlocalisedName.getNamespace() + "." + unlocalisedName.getPath());
    }


    public Component getNameForTranslationFormatted(){
        return Component.translatable("tier." + unlocalisedName.getNamespace() + "." + unlocalisedName.getPath()).withStyle(this.colour);
    }

    public ResourceLocation getUnlocalisedName(){
        return unlocalisedName;
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
