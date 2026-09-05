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

        this.tag(EBTags.NORMAL_MAGIC_CRYSTAL)
                .add(EBItems.MAGIC_CRYSTAL.get())
                .add(EBItems.MAGIC_CRYSTAL_EARTH.get())
                .add(EBItems.MAGIC_CRYSTAL_FIRE.get())
                .add(EBItems.MAGIC_CRYSTAL_ICE.get())
                .add(EBItems.MAGIC_CRYSTAL_HEALING.get())
                .add(EBItems.MAGIC_CRYSTAL_LIGHTNING.get())
                .add(EBItems.MAGIC_CRYSTAL_SORCERY.get())
                .add(EBItems.MAGIC_CRYSTAL_NECROMANCY.get())
                .replace(false);

        this.tag(EBTags.GRAND_MAGIC_CRYSTAL)
                .add(EBItems.MAGIC_CRYSTAL_GRAND.get())
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

        this.tag(EBTags.SPECIAL_UPGRADES)
                .addTag(EBTags.WAND_UPGRADES)
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

        this.tag(EBTags.ARCANE_TOMES)
                .add(EBItems.APPRENTICE_ARCANE_TOME.get())
                .add(EBItems.ADVANCED_ARCANE_TOME.get())
                .add(EBItems.MASTER_ARCANE_TOME.get())
                .replace(false);

        this.tag(EBTags.WORKBENCH_ITEM)
                .addTag(EBTags.WAND)
                .addTag(EBTags.WIZARD_ARMOR)
                .replace(false);

        this.tag(EBTags.MANA_ITEM)
                .addTag(EBTags.WAND)
                .addTag(EBTags.ARMOR)
                .replace(false);

        this.tag(EBTags.WAND)
                .add(EBItems.NOVICE_WAND.get())
                .add(EBItems.APPRENTICE_WAND.get())
                .add(EBItems.ADVANCED_WAND.get())
                .add(EBItems.MASTER_WAND.get())

                .add(EBItems.NOVICE_FIRE_WAND.get())
                .add(EBItems.APPRENTICE_FIRE_WAND.get())
                .add(EBItems.ADVANCED_FIRE_WAND.get())
                .add(EBItems.MASTER_FIRE_WAND.get())

                .add(EBItems.NOVICE_EARTH_WAND.get())
                .add(EBItems.APPRENTICE_EARTH_WAND.get())
                .add(EBItems.ADVANCED_EARTH_WAND.get())
                .add(EBItems.MASTER_EARTH_WAND.get())

                .add(EBItems.NOVICE_HEALING_WAND.get())
                .add(EBItems.APPRENTICE_HEALING_WAND.get())
                .add(EBItems.ADVANCED_HEALING_WAND.get())
                .add(EBItems.MASTER_HEALING_WAND.get())

                .add(EBItems.NOVICE_ICE_WAND.get())
                .add(EBItems.APPRENTICE_ICE_WAND.get())
                .add(EBItems.ADVANCED_ICE_WAND.get())
                .add(EBItems.MASTER_ICE_WAND.get())

                .add(EBItems.NOVICE_LIGHTNING_WAND.get())
                .add(EBItems.APPRENTICE_LIGHTNING_WAND.get())
                .add(EBItems.ADVANCED_LIGHTNING_WAND.get())
                .add(EBItems.MASTER_LIGHTNING_WAND.get())

                .add(EBItems.NOVICE_NECROMANCY_WAND.get())
                .add(EBItems.APPRENTICE_NECROMANCY_WAND.get())
                .add(EBItems.ADVANCED_NECROMANCY_WAND.get())
                .add(EBItems.MASTER_NECROMANCY_WAND.get())

                .add(EBItems.NOVICE_SORCERY_WAND.get())
                .add(EBItems.APPRENTICE_SORCERY_WAND.get())
                .add(EBItems.ADVANCED_SORCERY_WAND.get())
                .add(EBItems.MASTER_SORCERY_WAND.get())
                .replace(false);

        this.tag(EBTags.WIZARD_ARMOR)
                .add(EBItems.WIZARD_HAT.get())
                .add(EBItems.WIZARD_ROBE.get())
                .add(EBItems.WIZARD_LEGGINGS.get())
                .add(EBItems.WIZARD_BOOTS.get())

                .add(EBItems.WIZARD_HAT_EARTH.get())
                .add(EBItems.WIZARD_ROBE_EARTH.get())
                .add(EBItems.WIZARD_LEGGINGS_EARTH.get())
                .add(EBItems.WIZARD_BOOTS_EARTH.get())

                .add(EBItems.WIZARD_HAT_FIRE.get())
                .add(EBItems.WIZARD_ROBE_FIRE.get())
                .add(EBItems.WIZARD_LEGGINGS_FIRE.get())
                .add(EBItems.WIZARD_BOOTS_FIRE.get())

                .add(EBItems.WIZARD_HAT_HEALING.get())
                .add(EBItems.WIZARD_ROBE_HEALING.get())
                .add(EBItems.WIZARD_LEGGINGS_HEALING.get())
                .add(EBItems.WIZARD_BOOTS_HEALING.get())

                .add(EBItems.WIZARD_HAT_ICE.get())
                .add(EBItems.WIZARD_ROBE_ICE.get())
                .add(EBItems.WIZARD_LEGGINGS_ICE.get())
                .add(EBItems.WIZARD_BOOTS_ICE.get())

                .add(EBItems.WIZARD_HAT_LIGHTNING.get())
                .add(EBItems.WIZARD_ROBE_LIGHTNING.get())
                .add(EBItems.WIZARD_LEGGINGS_LIGHTNING.get())
                .add(EBItems.WIZARD_BOOTS_LIGHTNING.get())

                .add(EBItems.WIZARD_HAT_NECROMANCY.get())
                .add(EBItems.WIZARD_ROBE_NECROMANCY.get())
                .add(EBItems.WIZARD_LEGGINGS_NECROMANCY.get())
                .add(EBItems.WIZARD_BOOTS_NECROMANCY.get())

                .add(EBItems.WIZARD_HAT_SORCERY.get())
                .add(EBItems.WIZARD_ROBE_SORCERY.get())
                .add(EBItems.WIZARD_LEGGINGS_SORCERY.get())
                .add(EBItems.WIZARD_BOOTS_SORCERY.get())
                .replace(false);

        this.tag(EBTags.SAGE_ARMOR)
                .add(EBItems.SAGE_HAT.get())
                .add(EBItems.SAGE_ROBE.get())
                .add(EBItems.SAGE_LEGGINGS.get())
                .add(EBItems.SAGE_BOOTS.get())

                .add(EBItems.SAGE_HAT_EARTH.get())
                .add(EBItems.SAGE_ROBE_EARTH.get())
                .add(EBItems.SAGE_LEGGINGS_EARTH.get())
                .add(EBItems.SAGE_BOOTS_EARTH.get())

                .add(EBItems.SAGE_HAT_FIRE.get())
                .add(EBItems.SAGE_ROBE_FIRE.get())
                .add(EBItems.SAGE_LEGGINGS_FIRE.get())
                .add(EBItems.SAGE_BOOTS_FIRE.get())

                .add(EBItems.SAGE_HAT_HEALING.get())
                .add(EBItems.SAGE_ROBE_HEALING.get())
                .add(EBItems.SAGE_LEGGINGS_HEALING.get())
                .add(EBItems.SAGE_BOOTS_HEALING.get())

                .add(EBItems.SAGE_HAT_ICE.get())
                .add(EBItems.SAGE_ROBE_ICE.get())
                .add(EBItems.SAGE_LEGGINGS_ICE.get())
                .add(EBItems.SAGE_BOOTS_ICE.get())

                .add(EBItems.SAGE_HAT_LIGHTNING.get())
                .add(EBItems.SAGE_ROBE_LIGHTNING.get())
                .add(EBItems.SAGE_LEGGINGS_LIGHTNING.get())
                .add(EBItems.SAGE_BOOTS_LIGHTNING.get())

                .add(EBItems.SAGE_HAT_NECROMANCY.get())
                .add(EBItems.SAGE_ROBE_NECROMANCY.get())
                .add(EBItems.SAGE_LEGGINGS_NECROMANCY.get())
                .add(EBItems.SAGE_BOOTS_NECROMANCY.get())

                .add(EBItems.SAGE_HAT_SORCERY.get())
                .add(EBItems.SAGE_ROBE_SORCERY.get())
                .add(EBItems.SAGE_LEGGINGS_SORCERY.get())
                .add(EBItems.SAGE_BOOTS_SORCERY.get())
                .replace(false);

        this.tag(EBTags.WARLOCK_ARMOR)
                .add(EBItems.WARLOCK_HOOD.get())
                .add(EBItems.WARLOCK_ROBE.get())
                .add(EBItems.WARLOCK_LEGGINGS.get())
                .add(EBItems.WARLOCK_BOOTS.get())

                .add(EBItems.WARLOCK_HOOD_EARTH.get())
                .add(EBItems.WARLOCK_ROBE_EARTH.get())
                .add(EBItems.WARLOCK_LEGGINGS_EARTH.get())
                .add(EBItems.WARLOCK_BOOTS_EARTH.get())

                .add(EBItems.WARLOCK_HOOD_FIRE.get())
                .add(EBItems.WARLOCK_ROBE_FIRE.get())
                .add(EBItems.WARLOCK_LEGGINGS_FIRE.get())
                .add(EBItems.WARLOCK_BOOTS_FIRE.get())

                .add(EBItems.WARLOCK_HOOD_HEALING.get())
                .add(EBItems.WARLOCK_ROBE_HEALING.get())
                .add(EBItems.WARLOCK_LEGGINGS_HEALING.get())
                .add(EBItems.WARLOCK_BOOTS_HEALING.get())

                .add(EBItems.WARLOCK_HOOD_ICE.get())
                .add(EBItems.WARLOCK_ROBE_ICE.get())
                .add(EBItems.WARLOCK_LEGGINGS_ICE.get())
                .add(EBItems.WARLOCK_BOOTS_ICE.get())

                .add(EBItems.WARLOCK_HOOD_LIGHTNING.get())
                .add(EBItems.WARLOCK_ROBE_LIGHTNING.get())
                .add(EBItems.WARLOCK_LEGGINGS_LIGHTNING.get())
                .add(EBItems.WARLOCK_BOOTS_LIGHTNING.get())

                .add(EBItems.WARLOCK_HOOD_NECROMANCY.get())
                .add(EBItems.WARLOCK_ROBE_NECROMANCY.get())
                .add(EBItems.WARLOCK_LEGGINGS_NECROMANCY.get())
                .add(EBItems.WARLOCK_BOOTS_NECROMANCY.get())

                .add(EBItems.WARLOCK_HOOD_SORCERY.get())
                .add(EBItems.WARLOCK_ROBE_SORCERY.get())
                .add(EBItems.WARLOCK_LEGGINGS_SORCERY.get())
                .add(EBItems.WARLOCK_BOOTS_SORCERY.get())
                .replace(false);

        this.tag(EBTags.BATTLEMAGE_ARMOR)
                .add(EBItems.BATTLEMAGE_HELMET.get())
                .add(EBItems.BATTLEMAGE_CHESTPLATE_EARTH.get())
                .add(EBItems.BATTLEMAGE_LEGGINGS.get())
                .add(EBItems.BATTLEMAGE_BOOTS.get())

                .add(EBItems.BATTLEMAGE_HELMET_EARTH.get())
                .add(EBItems.BATTLEMAGE_CHESTPLATE_EARTH.get())
                .add(EBItems.BATTLEMAGE_LEGGINGS_EARTH.get())
                .add(EBItems.BATTLEMAGE_BOOTS_EARTH.get())

                .add(EBItems.BATTLEMAGE_HELMET_FIRE.get())
                .add(EBItems.BATTLEMAGE_CHESTPLATE_FIRE.get())
                .add(EBItems.BATTLEMAGE_LEGGINGS_FIRE.get())
                .add(EBItems.BATTLEMAGE_BOOTS_FIRE.get())

                .add(EBItems.BATTLEMAGE_HELMET_HEALING.get())
                .add(EBItems.BATTLEMAGE_CHESTPLATE_HEALING.get())
                .add(EBItems.BATTLEMAGE_LEGGINGS_HEALING.get())
                .add(EBItems.BATTLEMAGE_BOOTS_HEALING.get())

                .add(EBItems.BATTLEMAGE_HELMET_ICE.get())
                .add(EBItems.BATTLEMAGE_CHESTPLATE_ICE.get())
                .add(EBItems.BATTLEMAGE_LEGGINGS_ICE.get())
                .add(EBItems.BATTLEMAGE_BOOTS_ICE.get())

                .add(EBItems.BATTLEMAGE_HELMET_LIGHTNING.get())
                .add(EBItems.BATTLEMAGE_CHESTPLATE_LIGHTNING.get())
                .add(EBItems.BATTLEMAGE_LEGGINGS_LIGHTNING.get())
                .add(EBItems.BATTLEMAGE_BOOTS_LIGHTNING.get())

                .add(EBItems.BATTLEMAGE_HELMET_NECROMANCY.get())
                .add(EBItems.BATTLEMAGE_CHESTPLATE_NECROMANCY.get())
                .add(EBItems.BATTLEMAGE_LEGGINGS_NECROMANCY.get())
                .add(EBItems.BATTLEMAGE_BOOTS_NECROMANCY.get())

                .add(EBItems.BATTLEMAGE_HELMET_SORCERY.get())
                .add(EBItems.BATTLEMAGE_CHESTPLATE_SORCERY.get())
                .add(EBItems.BATTLEMAGE_LEGGINGS_SORCERY.get())
                .add(EBItems.BATTLEMAGE_BOOTS_SORCERY.get())
                .replace(false);

        this.tag(EBTags.ARMOR)
                .addTag(EBTags.WIZARD_ARMOR)
                .addTag(EBTags.WARLOCK_ARMOR)
                .addTag(EBTags.BATTLEMAGE_ARMOR)
                .replace(false);

        this.tag(EBTags.UPGRADE)
                .addTag(EBTags.ARCANE_TOMES)
                .addTag(EBTags.ARMOR_UPGRADE)
                .replace(false);

        this.tag(EBTags.MANA_FLASK)
                .add(EBItems.SMALL_MANA_FLASK.get())
                .add(EBItems.MEDIUM_MANA_FLASK.get())
                .add(EBItems.LARGE_MANA_FLASK.get());

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
