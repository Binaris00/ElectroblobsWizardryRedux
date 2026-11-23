package com.binaris.wizardry.content.block;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.RegistryUtils;
import com.binaris.wizardry.content.blockentity.BookshelfBlockEntity;
import com.binaris.wizardry.content.item.SpellBookItem;
import com.binaris.wizardry.core.config.EBServerConfig;
import com.binaris.wizardry.setup.registries.EBTags;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

/// Finds the spell books stored in the bookshelves around a lectern.
public final class LecternBookshelfUtils {
    private LecternBookshelfUtils() {
    }

    public static int getSearchRadius() {
        return EBServerConfig.LECTERN_SEARCH_RADIUS.get();
    }

    public static List<BlockPos> findBookshelfPositions(Level level, BlockPos centre) {
        List<BlockPos> positions = new ArrayList<>();
        int radius = getSearchRadius();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = centre.offset(x, y, z);
                    if (!level.isLoaded(pos)) continue;
                    if (getBookshelf(level, pos) != null) positions.add(pos);
                }
            }
        }

        return positions;
    }

    public static List<Spell> findAvailableSpells(Level level, BlockPos centre) {
        List<Spell> spells = new ArrayList<>();

        for (BlockPos pos : findBookshelfPositions(level, centre)) {
            Container bookshelf = getBookshelf(level, pos);
            if (bookshelf == null) continue;

            for (int slot = 0; slot < bookshelf.getContainerSize(); slot++) {
                ItemStack stack = bookshelf.getItem(slot);
                if (!(stack.getItem() instanceof SpellBookItem)) continue;

                Spell spell = RegistryUtils.getSpell(stack);
                if (spell != Spells.NONE && !spells.contains(spell)) spells.add(spell);
            }
        }

        return spells;
    }

    public static List<BlockPos> findBookshelvesContaining(Level level, BlockPos centre, Spell spell) {
        List<BlockPos> positions = new ArrayList<>();

        for (BlockPos pos : findBookshelfPositions(level, centre)) {
            Container bookshelf = getBookshelf(level, pos);
            if (bookshelf != null && containsSpell(bookshelf, spell)) positions.add(pos);
        }

        return positions;
    }

    public static boolean containsSpell(Container bookshelf, Spell spell) {
        if (spell == Spells.NONE) return false;

        for (int slot = 0; slot < bookshelf.getContainerSize(); slot++) {
            ItemStack stack = bookshelf.getItem(slot);
            if (stack.getItem() instanceof SpellBookItem && RegistryUtils.getSpell(stack) == spell) return true;
        }

        return false;
    }

    public static Container getBookshelf(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!level.getBlockState(pos).is(EBTags.BOOKSHELF) && !(blockEntity instanceof BookshelfBlockEntity)) {
            return null;
        }

        return blockEntity instanceof Container container ? container : null;
    }
}
