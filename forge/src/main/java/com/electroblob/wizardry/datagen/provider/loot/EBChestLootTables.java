package com.electroblob.wizardry.datagen.provider.loot;

import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.content.loot.RandomSpellFunction;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.EBLootTables;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;

public final class EBChestLootTables implements LootTableSubProvider {
    @Override
    public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> biConsumer) {
//        biConsumer.accept(EBLootTables.DUNGEON_ADDITIONS,
//                LootTable.lootTable()
//                        .withPool(LootPool.lootPool()
//                                .setRolls(UniformGenerator.between(1, 3))
//                                .add(LootTableReference.lootTableReference(EBLootTables.SUBSET_ELEMENTAL_CRYSTALS).setWeight(6))
//                                .add(LootTableReference.lootTableReference(EBLootTables.SUBSET_WIZARD_ARMOR).setWeight(10))
//                                .add(LootTableReference.lootTableReference(EBLootTables.SUBSET_ARCANE_TOMES).setWeight(8))
//                                .add(LootTableReference.lootTableReference(EBLootTables.SUBSET_WAND_UPGRADES).setWeight(4))
//                                .add(LootItem.lootTableItem(EBItems.MAGIC_CRYSTAL.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))).setWeight(10))
//                                .add(LootItem.lootTableItem(EBItems.SPELL_BOOK.get()).apply(RandomSpellFunction.setRandomSpell(List.of(), false, 0.3F, List.of(), List.of())).setWeight(40))
//                                //.add(LootItem.lootTableItem(EBItems.SCROLL.get()).apply(RandomSpellFunction.setRandomSpell(List.of(), false, 0.3F, List.of(), List.of())).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))).setWeight(14))
//                                .add(LootItem.lootTableItem(EBItems.IDENTIFICATION_SCROLL.get()).setWeight(6))
//                                .add(LootItem.lootTableItem(EBItems.FIREBOMB.get()).setWeight(8))
//                                .add(LootItem.lootTableItem(EBItems.POISON_BOMB.get()).setWeight(8))
//                                .add(LootItem.lootTableItem(EBItems.SMOKE_BOMB.get()).setWeight(8))
//                                .add(LootItem.lootTableItem(EBItems.SPARK_BOMB.get()).setWeight(8))
//                                .add(LootItem.lootTableItem(EBItems.ASTRAL_DIAMOND.get()).setWeight(2))
//                                .add(LootItem.lootTableItem(EBItems.PURIFYING_ELIXIR.get()).setWeight(1))
//                                .add(LootItem.lootTableItem(EBItems.MAGIC_CRYSTAL_GRAND.get()).setWeight(2))
//                        ));
    }

}
