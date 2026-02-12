package com.binaris.wizardry.datagen.provider.loot;

import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.EBLootTables;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public final class EBSubsetsLootTables implements LootTableSubProvider {
    @Override
    public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> biConsumer) {
        biConsumer.accept(EBLootTables.SUBSET_ARCANE_TOMES,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(EBItems.APPRENTICE_ARCANE_TOME.get()).setWeight(4)).name("apprentice_tome")
                                .add(LootItem.lootTableItem(EBItems.ADVANCED_ARCANE_TOME.get()).setWeight(2)).name("advanced_tome")
                                .add(LootItem.lootTableItem(EBItems.MASTER_ARCANE_TOME.get()).setWeight(1)).name("master_tome")));

        biConsumer.accept(EBLootTables.SUBSET_ARMOR_UPGRADES,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .name("upgrades")
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(EBItems.RESPLENDENT_THREAD.get()))
                                .add(LootItem.lootTableItem(EBItems.CRYSTAL_SILVER_PLATING.get()))
                                .add(LootItem.lootTableItem(EBItems.ETHEREAL_CRYSTAL_WEAVE.get()))));

        biConsumer.accept(EBLootTables.SUBSET_ELEMENTAL_CRYSTALS,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .name("crystals")
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(EBItems.MAGIC_CRYSTAL.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.MAGIC_CRYSTAL_EARTH.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.MAGIC_CRYSTAL_FIRE.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.MAGIC_CRYSTAL_HEALING.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.MAGIC_CRYSTAL_ICE.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.MAGIC_CRYSTAL_LIGHTNING.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.MAGIC_CRYSTAL_NECROMANCY.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.MAGIC_CRYSTAL_SORCERY.get()).setWeight(1))
                        ));

        biConsumer.accept(EBLootTables.SUBSET_EPIC_ARTIFACTS,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .name("epic_artifacts")
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(EBItems.RING_COMBUSTION.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.RING_ARCANE_FROST.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.RING_SEEKING.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.RING_MANA_RETURN.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.AMULET_ICE_IMMUNITY.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.AMULET_WITHER_IMMUNITY.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.AMULET_ABSORPTION.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.CHARM_EXPERIENCE_TOME.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.CHARM_STOP_TIME.get()).setWeight(1))
                        ));

        biConsumer.accept(EBLootTables.SUBSET_RARE_ARTIFACTS,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .name("rare_artifacts")
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(EBItems.RING_CONDENSING.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.RING_SHATTERING.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.RING_STORM.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.RING_LEECHING.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.RING_POISON.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.RING_FULL_MOON.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.RING_EVOKER.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.RING_BLOCKWRANGLER.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.RING_CONJURER.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.AMULET_ARCANE_DEFENCE.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.AMULET_WISDOM.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.AMULET_POTENTIAL.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.AMULET_TRANSIENCE.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.CHARM_HAGGLER.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.CHARM_MOVE_SPEED.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.CHARM_FLIGHT.get()).setWeight(1))
                                .add(LootItem.lootTableItem(EBItems.CHARM_MOUNT_TELEPORTING.get()).setWeight(1))

                        ));

        biConsumer.accept(EBLootTables.SUBSET_UNCOMMON_ARTIFACTS,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .name("uncommon_artifacts")
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(EBItems.RING_SIPHONING.get()))
                                .add(LootItem.lootTableItem(EBItems.RING_BATTLEMAGE.get()))
                                .add(LootItem.lootTableItem(EBItems.RING_FIRE_MELEE.get()))
                                .add(LootItem.lootTableItem(EBItems.RING_FIRE_BIOME.get()))
                                .add(LootItem.lootTableItem(EBItems.RING_ICE_MELEE.get()))
                                .add(LootItem.lootTableItem(EBItems.RING_ICE_BIOME.get()))
                                .add(LootItem.lootTableItem(EBItems.RING_LIGHTNING_MELEE.get()))
                                .add(LootItem.lootTableItem(EBItems.RING_NECROMANCY_MELEE.get()))
                                .add(LootItem.lootTableItem(EBItems.RING_EARTH_MELEE.get()))
                                .add(LootItem.lootTableItem(EBItems.RING_EARTH_BIOME.get()))
                                .add(LootItem.lootTableItem(EBItems.RING_EXTRACTION.get()))
                                .add(LootItem.lootTableItem(EBItems.RING_PALADIN.get()))
                                .add(LootItem.lootTableItem(EBItems.AMULET_WARDING.get()))
                                .add(LootItem.lootTableItem(EBItems.AMULET_FIRE_PROTECTION.get()))
                                .add(LootItem.lootTableItem(EBItems.AMULET_ICE_PROTECTION.get()))
                                .add(LootItem.lootTableItem(EBItems.AMULET_CHANNELING.get()))
                                .add(LootItem.lootTableItem(EBItems.AMULET_LICH.get()))
                                .add(LootItem.lootTableItem(EBItems.CHARM_SPELL_DISCOVERY.get()))
                                .add(LootItem.lootTableItem(EBItems.CHARM_MINION_VARIANTS.get()))
                                .add(LootItem.lootTableItem(EBItems.CHARM_GROWTH.get()))
                                .add(LootItem.lootTableItem(EBItems.CHARM_FEEDING.get()))
                        ));

        biConsumer.accept(EBLootTables.SUBSET_WAND_UPGRADES,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .name("upgrades")
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(EBItems.CONDENSER_UPGRADE.get()))
                                .add(LootItem.lootTableItem(EBItems.SIPHON_UPGRADE.get()))
                                .add(LootItem.lootTableItem(EBItems.STORAGE_UPGRADE.get()))
                                .add(LootItem.lootTableItem(EBItems.RANGE_UPGRADE.get()))
                                .add(LootItem.lootTableItem(EBItems.DURATION_UPGRADE.get()))
                                .add(LootItem.lootTableItem(EBItems.COOLDOWN_UPGRADE.get()))
                                .add(LootItem.lootTableItem(EBItems.BLAST_UPGRADE.get()))
                                .add(LootItem.lootTableItem(EBItems.ATTUNEMENT_UPGRADE.get()))
                                .add(LootItem.lootTableItem(EBItems.MELEE_UPGRADE.get()))
                        ));

        biConsumer.accept(EBLootTables.SUBSET_WIZARD_ARMOR,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .name("armour")
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_HAT.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_HAT_FIRE.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_HAT_ICE.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_HAT_LIGHTNING.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_HAT_NECROMANCY.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_HAT_EARTH.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_HAT_SORCERY.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_HAT_HEALING.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_ROBE.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_ROBE_FIRE.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_ROBE_ICE.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_ROBE_LIGHTNING.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_ROBE_NECROMANCY.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_ROBE_EARTH.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_ROBE_SORCERY.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_ROBE_HEALING.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_LEGGINGS.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_LEGGINGS_FIRE.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_LEGGINGS_ICE.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_LEGGINGS_LIGHTNING.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_LEGGINGS_NECROMANCY.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_LEGGINGS_EARTH.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_LEGGINGS_SORCERY.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_LEGGINGS_HEALING.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_BOOTS.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_BOOTS_FIRE.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_BOOTS_ICE.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_BOOTS_LIGHTNING.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_BOOTS_NECROMANCY.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_BOOTS_EARTH.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_BOOTS_SORCERY.get()))
                                .add(LootItem.lootTableItem(EBItems.WIZARD_BOOTS_HEALING.get()))
                        ));
    }
}
