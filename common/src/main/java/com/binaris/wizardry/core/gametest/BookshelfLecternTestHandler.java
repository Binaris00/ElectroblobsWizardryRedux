package com.binaris.wizardry.core.gametest;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.RegistryUtils;
import com.binaris.wizardry.content.block.BookShelfBlock;
import com.binaris.wizardry.content.block.LecternBookshelfUtils;
import com.binaris.wizardry.content.blockentity.ArcaneWorkbenchBlockEntity;
import com.binaris.wizardry.content.blockentity.BookshelfBlockEntity;
import com.binaris.wizardry.content.blockentity.MagicLecternBlockEntity;
import com.binaris.wizardry.content.menu.ArcaneWorkbenchMenu;
import com.binaris.wizardry.content.menu.BookshelfMenu;
import com.binaris.wizardry.setup.registries.EBBlocks;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

final class BookshelfLecternTestHandler {
    private BookshelfLecternTestHandler() {
    }

    static void bookshelfAcceptsOnlyBooks(GameTestHelper helper) {
        GST.assertTrue(helper, "A spell book should be a valid bookshelf item",
                BookshelfMenu.isBook(RegistryUtils.spellBookItem(Spells.FIREBALL)));
        GST.assertTrue(helper, "A vanilla book should be a valid bookshelf item",
                BookshelfMenu.isBook(new ItemStack(Items.BOOK)));
        GST.assertFalse(helper, "Dirt should not be a valid bookshelf item",
                BookshelfMenu.isBook(new ItemStack(Items.DIRT)));
        helper.succeed();
    }

    static void lecternFindsNearbySpells(GameTestHelper helper) {
        BlockPos shelfPos = new BlockPos(0, 1, 0);
        BlockPos lecternPos = new BlockPos(2, 1, 2);

        helper.setBlock(shelfPos,
                EBBlocks.OAK_BOOKSHELF.get().defaultBlockState().setValue(BookShelfBlock.FACING, Direction.NORTH));
        helper.setBlock(lecternPos, EBBlocks.OAK_LECTERN.get().defaultBlockState());

        BlockEntity blockEntity = helper.getBlockEntity(shelfPos);
        GST.assertTrue(helper, "Bookshelf block entity was not created", blockEntity instanceof BookshelfBlockEntity);
        if (!(blockEntity instanceof BookshelfBlockEntity shelf)) return;

        GST.assertFalse(helper, "Bookshelf frame incorrectly occludes light as a full cube",
                shelf.getBlockState().canOcclude());
        shelf.setItem(0, RegistryUtils.spellBookItem(Spells.FIREBALL));

        List<Spell> spells = LecternBookshelfUtils.findAvailableSpells(helper.getLevel(), helper.absolutePos(lecternPos));
        GST.assertTrue(helper, "Lectern did not find Fireball in the nearby bookshelf", spells.contains(Spells.FIREBALL));

        BlockEntity lecternEntity = helper.getBlockEntity(lecternPos);
        GST.assertTrue(helper, "Lectern block entity was not created", lecternEntity instanceof MagicLecternBlockEntity);
        if (lecternEntity instanceof MagicLecternBlockEntity lectern) {
            lectern.setSpell(Spells.FIREBALL);
            GST.assertEquals(helper, "Lectern did not remember the selected spell", Spells.FIREBALL, lectern.getSpell());

            CompoundTag saved = lectern.saveWithoutMetadata();
            MagicLecternBlockEntity restored = new MagicLecternBlockEntity(
                    helper.absolutePos(lecternPos), lectern.getBlockState());
            restored.load(saved);
            GST.assertEquals(helper, "Lectern spell did not survive an NBT round trip",
                    Spells.FIREBALL, restored.getSpell());
        }

        shelf.removeItem(0, 1);
        List<Spell> spellsAfterRemoval = LecternBookshelfUtils.findAvailableSpells(
                helper.getLevel(), helper.absolutePos(lecternPos));
        GST.assertFalse(helper, "Lectern still found Fireball after it was removed from the bookshelf",
                spellsAfterRemoval.contains(Spells.FIREBALL));

        BookshelfBlockEntity clientCopy = new BookshelfBlockEntity(
                helper.absolutePos(shelfPos), shelf.getBlockState());
        clientCopy.setItem(0, RegistryUtils.spellBookItem(Spells.FIREBALL));
        clientCopy.load(shelf.getUpdateTag());
        GST.assertTrue(helper, "An empty bookshelf update did not clear the previous client-side contents",
                clientCopy.getItem(0).isEmpty());

        BlockPos workbenchPos = new BlockPos(1, 1, 0);
        helper.setBlock(workbenchPos, EBBlocks.ARCANE_WORKBENCH.get().defaultBlockState());
        BlockEntity workbenchEntity = helper.getBlockEntity(workbenchPos);
        GST.assertTrue(helper, "Arcane workbench block entity was not created",
                workbenchEntity instanceof ArcaneWorkbenchBlockEntity);
        if (!(workbenchEntity instanceof ArcaneWorkbenchBlockEntity workbench)) return;

        Player player = GST.mockPlayer(helper, new Vec3(1.5, 1, 0.5));
        workbench.setItem(ArcaneWorkbenchMenu.CENTRE_SLOT, EBItems.NOVICE_WAND.get().getDefaultInstance());
        ArcaneWorkbenchMenu menu = new ArcaneWorkbenchMenu(0, player.getInventory(), workbench);
        player.containerMenu = menu;

        ItemStack manuallyPlaced = RegistryUtils.spellBookItem(Spells.FIREBALL);
        manuallyPlaced.setCount(16);
        menu.setCarried(manuallyPlaced);
        menu.clicked(0, 0, ClickType.PICKUP, player);
        GST.assertEquals(helper, "Manual placement did not put exactly one book in the workbench slot",
                1, workbench.getItem(0).getCount());
        GST.assertEquals(helper, "Manual placement did not leave the rest of the book stack on the cursor",
                15, menu.getCarried().getCount());

        workbench.setItem(0, ItemStack.EMPTY);
        menu.setCarried(ItemStack.EMPTY);
        ItemStack quickMoved = RegistryUtils.spellBookItem(Spells.FIREBALL);
        quickMoved.setCount(16);
        player.getInventory().setItem(0, quickMoved);
        menu.clicked(11, 0, ClickType.QUICK_MOVE, player);
        GST.assertEquals(helper, "Quick move did not put one book in the workbench slot",
                1, workbench.getItem(0).getCount());
        GST.assertEquals(helper, "Quick move did not store the remaining books as one bookshelf stack",
                15, shelf.getItem(0).getCount());
        GST.assertTrue(helper, "Quick move left books behind in the player inventory",
                player.getInventory().getItem(0).isEmpty());
        GST.assertEquals(helper, "Quick move duplicated spell books",
                16, workbench.getItem(0).getCount() + shelf.getItem(0).getCount());

        workbench.setItem(0, ItemStack.EMPTY);
        shelf.setItem(0, ItemStack.EMPTY);
        menu.setCarried(RegistryUtils.spellBookItem(Spells.FIREBALL));

        GST.assertTrue(helper, "Workbench bookshelf panel did not store the carried spell book",
                menu.storeCarriedBookInNearbyBookshelf(null, -1));
        GST.assertTrue(helper, "Workbench bookshelf panel did not clear the cursor after storing the book",
                menu.getCarried().isEmpty());
        GST.assertTrue(helper, "Stored spell book was not added to the nearby bookshelf",
                RegistryUtils.getSpell(shelf.getItem(0)) == Spells.FIREBALL);

        ItemStack replacement = RegistryUtils.spellBookItem(Spells.ICE_SHARD);
        replacement.setCount(4);
        menu.setCarried(replacement);
        GST.assertTrue(helper, "Workbench bookshelf panel did not swap with an occupied bookshelf slot",
                menu.storeCarriedBookInNearbyBookshelf(shelf.getBlockPos(), 0));
        GST.assertTrue(helper, "Replacement stack was not stored in the selected bookshelf slot",
                RegistryUtils.getSpell(shelf.getItem(0)) == Spells.ICE_SHARD && shelf.getItem(0).getCount() == 4);
        GST.assertTrue(helper, "Previous bookshelf stack was not returned to the cursor after swapping",
                RegistryUtils.getSpell(menu.getCarried()) == Spells.FIREBALL);

        menu.setCarried(ItemStack.EMPTY);
        GST.assertTrue(helper, "Workbench bookshelf panel did not return the spell book to the cursor",
                menu.takeBookToCursor(shelf, 0));
        GST.assertTrue(helper, "Returned spell book was not placed on the cursor",
                RegistryUtils.getSpell(menu.getCarried()) == Spells.ICE_SHARD);
        GST.assertEquals(helper, "Taking a book did not leave the rest of its stack in the bookshelf",
                3, shelf.getItem(0).getCount());

        helper.succeed();
    }
}
