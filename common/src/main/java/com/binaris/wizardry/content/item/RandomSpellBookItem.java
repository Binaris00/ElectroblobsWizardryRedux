package com.binaris.wizardry.content.item;

import com.binaris.wizardry.api.EBLogger;
import com.binaris.wizardry.api.content.util.SpellUtil;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RandomSpellBookItem extends Item {
    public RandomSpellBookItem(Properties properties) {
        super(properties);
    }

    public static void create(Level level, Player player, ItemStack original) {
        if (level.isClientSide) return;
        ItemStack stack;
        CompoundTag tag = original.getTag();

        if (tag == null || !tag.contains("LootTable")) {
            stack = SpellUtil.setSpell(new ItemStack(EBItems.SPELL_BOOK.get()), Spells.MAGIC_MISSILE);
            spawn(level, player.blockPosition(), stack);
            EBLogger.error("Attempted to use a RandomSpellBookItem without a LootTable NBT tag.");
            return;
        }

        String lootTableId = tag.getString("LootTable");
        ResourceLocation lootTableLocation = ResourceLocation.tryParse(lootTableId);

        if (lootTableLocation == null) {
            stack = SpellUtil.setSpell(new ItemStack(EBItems.SPELL_BOOK.get()), Spells.MAGIC_MISSILE);
            spawn(level, player.blockPosition(), stack);
            EBLogger.error("Attempted to use a RandomSpellBookItem with an invalid LootTable NBT tag.");
            return;
        }

        LootTable lootTable = level.getServer().getLootData().getLootTable(lootTableLocation);

        LootParams lootParams = new LootParams.Builder((ServerLevel) level)
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withParameter(LootContextParams.ORIGIN, player.position())
                .create(LootContextParamSets.GIFT);

        List<ItemStack> loot = lootTable.getRandomItems(lootParams);
        loot.forEach(i -> spawn(level, player.blockPosition(), i));

        original.shrink(1);
    }

    private static void spawn(Level level, BlockPos pos, ItemStack stack) {
        ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
        level.addFreshEntity(itemEntity);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        create(level, player, stack);
        return InteractionResultHolder.pass(stack);
    }
}
