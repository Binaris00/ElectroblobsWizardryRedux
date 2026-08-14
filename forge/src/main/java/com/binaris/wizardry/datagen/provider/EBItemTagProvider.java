package com.binaris.wizardry.datagen.provider;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.DeferredObject;
import com.binaris.wizardry.api.content.item.ArtifactItem;
import com.binaris.wizardry.setup.registries.EBBlocks;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.EBTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class EBItemTagProvider extends ItemTagsProvider {
    public EBItemTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, WizardryMainMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(EBTags.GILDED_WOOD_ITEM)
                .add(EBBlocks.GILDED_ACACIA_WOOD.get().asItem())
                .add(EBBlocks.GILDED_BIRCH_WOOD.get().asItem())
                .add(EBBlocks.GILDED_DARK_OAK_WOOD.get().asItem())
                .add(EBBlocks.GILDED_OAK_WOOD.get().asItem())
                .add(EBBlocks.GILDED_JUNGLE_WOOD.get().asItem())
                .add(EBBlocks.GILDED_SPRUCE_WOOD.get().asItem())
                .replace(false);

        this.tag(EBTags.MAGIC_CRYSTAL_ITEM)
                .add(EBItems.MAGIC_CRYSTAL.get())
                .add(EBItems.MAGIC_CRYSTAL_GRAND.get())
                .add(EBItems.MAGIC_CRYSTAL_EARTH.get())
                .add(EBItems.MAGIC_CRYSTAL_FIRE.get())
                .add(EBItems.MAGIC_CRYSTAL_ICE.get())
                .add(EBItems.MAGIC_CRYSTAL_HEALING.get())
                .add(EBItems.MAGIC_CRYSTAL_LIGHTNING.get())
                .add(EBItems.MAGIC_CRYSTAL_SORCERY.get())
                .add(EBItems.MAGIC_CRYSTAL_NECROMANCY.get())
                .replace(false);

        this.tag(EBTags.MAGIC_SHARD_ITEM)
                .add(EBItems.MAGIC_CRYSTAL_SHARD.get())
                .replace(false);

        this.tag(EBTags.WAND_UPGRADES)
                .add(EBItems.ATTUNEMENT_UPGRADE.get())
                .add(EBItems.BLAST_UPGRADE.get())
                .add(EBItems.CONDENSER_UPGRADE.get())
                .add(EBItems.COOLDOWN_UPGRADE.get())
                .add(EBItems.DURATION_UPGRADE.get())
                .add(EBItems.MELEE_UPGRADE.get())
                .add(EBItems.RANGE_UPGRADE.get())
                .add(EBItems.SIPHON_UPGRADE.get())
                .add(EBItems.STORAGE_UPGRADE.get())
                .replace(false);

        this.tag(EBTags.SPECTRAL_DUSTS)
                .add(EBItems.SPECTRAL_DUST.get())
                .add(EBItems.SPECTRAL_DUST_EARTH.get())
                .add(EBItems.SPECTRAL_DUST_FIRE.get())
                .add(EBItems.SPECTRAL_DUST_HEALING.get())
                .add(EBItems.SPECTRAL_DUST_ICE.get())
                .add(EBItems.SPECTRAL_DUST_LIGHTNING.get())
                .add(EBItems.SPECTRAL_DUST_NECROMANCY.get())
                .add(EBItems.SPECTRAL_DUST_SORCERY.get())
                .replace(false);

        this.tag(EBTags.ARMOR_UPGRADE)
                .add(EBItems.CRYSTAL_SILVER_PLATING.get())
                .add(EBItems.ETHEREAL_CRYSTAL_WEAVE.get())
                .add(EBItems.RESPLENDENT_THREAD.get())
                .replace(false);

        for (Map.Entry<DeferredObject<? extends Item>, ArtifactItem.Type> entry : EBItems.getArtifacts().entrySet()) {
            if (entry.getValue() == ArtifactItem.Type.CHARM) {
                this.tag(EBTags.CHARM_ACCESSORIES).add(entry.getKey().get()).replace(false);
                this.tag(EBTags.CHARM_CURIOS).add(entry.getKey().get()).replace(false);
                this.tag(EBTags.GLOVE_TRINKETS).add(entry.getKey().get()).replace(false);

            }

            if (entry.getValue() == ArtifactItem.Type.RING) {
                this.tag(EBTags.RING_CURIOS).add(entry.getKey().get()).replace(false);
                this.tag(EBTags.RING_ACCESSORIES).add(entry.getKey().get()).replace(false);
                this.tag(EBTags.MAIN_HAND_RING_TRINKETS).add(entry.getKey().get()).replace(false);
            }

            if (entry.getValue() == ArtifactItem.Type.NECKLACE) {
                this.tag(EBTags.NECKLACE_CURIOS).add(entry.getKey().get()).replace(false);
                this.tag(EBTags.NECKLACE_ACCESSORIES).add(entry.getKey().get()).replace(false);
                this.tag(EBTags.NECKLACE_TRINKETS).add(entry.getKey().get()).replace(false);
            }
        }
    }
}
