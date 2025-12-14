package com.electroblob.wizardry.datagen.provider;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.setup.registries.EBBlocks;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.EBTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

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

        this.tag(EBTags.RING_ACCESSORIES)
                .add(EBItems.RING_ARCANE_FROST.get())
                .add(EBItems.RING_BATTLEMAGE.get())
                .add(EBItems.RING_BLOCKWRANGLER.get())
                .add(EBItems.RING_COMBUSTION.get())
                .add(EBItems.RING_CONDENSING.get())
                .add(EBItems.RING_CONJURER.get())
                .add(EBItems.RING_DEFENDER.get())
                .add(EBItems.RING_DISINTEGRATION.get())
                .add(EBItems.RING_EARTH_BIOME.get())
                .add(EBItems.RING_EVOKER.get())
                .add(EBItems.RING_EXTRACTION.get())
                .add(EBItems.RING_FIRE_MELEE.get())
                .add(EBItems.RING_FULL_MOON.get())
                .add(EBItems.RING_HAMMER.get())
                .add(EBItems.RING_ICE_BIOME.get())
                .add(EBItems.RING_ICE_MELEE.get())
                .add(EBItems.RING_INTERDICTION.get())
                .add(EBItems.RING_LEECHING.get())
                .add(EBItems.RING_HAMMER.get())
                .add(EBItems.RING_LIGHTNING_MELEE.get())
                .add(EBItems.RING_MANA_RETURN.get())
                .add(EBItems.RING_METEOR.get())
                .add(EBItems.RING_MIND_CONTROL.get())
                .add(EBItems.RING_NECROMANCY_MELEE.get())
                .add(EBItems.RING_PALADIN.get())
                .add(EBItems.RING_POISON.get())
                .add(EBItems.RING_SEEKING.get())
                .add(EBItems.RING_SHATTERING.get())
                .add(EBItems.RING_SIPHONING.get())
                .add(EBItems.RING_SOULBINDING.get())
                .add(EBItems.RING_STORM.get())
                .add(EBItems.RING_STORMCLOUD.get())
                .replace(false);

        this.tag(EBTags.CHARM_ACCESSORIES)
                .add(EBItems.CHARM_ABSEILING.get())
                .add(EBItems.CHARM_AUTO_SMELT.get())
                .add(EBItems.CHARM_BLACK_HOLE.get())
                .add(EBItems.CHARM_EXPERIENCE_TOME.get())
                .add(EBItems.CHARM_FEEDING.get())
                .add(EBItems.CHARM_FLIGHT.get())
                .add(EBItems.CHARM_GROWTH.get())
                .add(EBItems.CHARM_HAGGLER.get())
                .add(EBItems.CHARM_HUNGER_CASTING.get())
                .add(EBItems.CHARM_LAVA_WALKING.get())
                .add(EBItems.CHARM_LIGHT.get())
                .add(EBItems.CHARM_MINION_HEALTH.get())
                .add(EBItems.CHARM_MINION_VARIANTS.get())
                .add(EBItems.CHARM_MOUNT_TELEPORTING.get())
                .add(EBItems.CHARM_MOVE_SPEED.get())
                .add(EBItems.CHARM_SILK_TOUCH.get())
                .add(EBItems.CHARM_SIXTH_SENSE.get())
                .add(EBItems.CHARM_SPELL_DISCOVERY.get())
                .add(EBItems.CHARM_STOP_TIME.get())
                .add(EBItems.CHARM_STORM.get())
                .add(EBItems.CHARM_TRANSPORTATION.get())
                .add(EBItems.CHARM_UNDEAD_HELMETS.get())
                .replace(false);

        this.tag(EBTags.NECKLACE_ACCESSORIES)
                .add(EBItems.AMULET_ABSORPTION.get())
                .add(EBItems.AMULET_ANCHORING.get())
                .add(EBItems.AMULET_ARCANE_DEFENCE.get())
                .add(EBItems.AMULET_AUTO_SHIELD.get())
                .add(EBItems.AMULET_BANISHING.get())
                .add(EBItems.AMULET_CHANNELING.get())
                .add(EBItems.AMULET_FIRE_CLOAKING.get())
                .add(EBItems.AMULET_FIRE_PROTECTION.get())
                .add(EBItems.AMULET_FROST_WARDING.get())
                .add(EBItems.AMULET_ICE_IMMUNITY.get())
                .add(EBItems.AMULET_ICE_PROTECTION.get())
                .add(EBItems.AMULET_LICH.get())
                .add(EBItems.AMULET_POTENTIAL.get())
                .add(EBItems.AMULET_RECOVERY.get())
                .add(EBItems.AMULET_RESURRECTION.get())
                .add(EBItems.AMULET_TRANSIENCE.get())
                .add(EBItems.AMULET_WARDING.get())
                .add(EBItems.AMULET_WISDOM.get())
                .add(EBItems.AMULET_WITHER_IMMUNITY.get())
                .replace(false);
    }
}
