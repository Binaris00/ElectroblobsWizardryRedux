package com.binaris.wizardry.datagen.provider;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.DeferredObject;
import com.binaris.wizardry.setup.registries.EBBannerPatterns;
import com.binaris.wizardry.setup.registries.EBTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BannerPatternTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public final class EBBannerPatternTagsProvider extends BannerPatternTagsProvider {
    public EBBannerPatternTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, WizardryMainMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        pattern(EBTags.EARTH_PATTERN_ITEM, EBBannerPatterns.EARTH);
        pattern(EBTags.FIRE_PATTERN_ITEM, EBBannerPatterns.FIRE);
        pattern(EBTags.HEALING_PATTERN_ITEM, EBBannerPatterns.HEALING);
        pattern(EBTags.ICE_PATTERN_ITEM, EBBannerPatterns.ICE);
        pattern(EBTags.LIGHTNING_PATTERN_ITEM, EBBannerPatterns.LIGHTNING);
        pattern(EBTags.NECROMANCY_PATTERN_ITEM, EBBannerPatterns.NECROMANCY);
        pattern(EBTags.SORCERY_PATTERN_ITEM, EBBannerPatterns.SORCERY);
    }

    private void pattern(TagKey<BannerPattern> tag, DeferredObject<BannerPattern> pattern) {
        ResourceKey<BannerPattern> key = BuiltInRegistries.BANNER_PATTERN.getResourceKey(pattern.get())
                .orElseThrow(() -> new IllegalStateException("Unregistered banner pattern: " + pattern.get()));
        this.tag(tag).add(key).replace(false);
    }
}
