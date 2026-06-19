package com.binaris.wizardry.api.client.util;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.RegistryUtils;
import com.binaris.wizardry.client.gui.screens.SpellBookScreen;
import com.binaris.wizardry.content.data.SpellGlyphData;
import com.binaris.wizardry.core.mixin.accessor.MerchantMenuAccessor;
import com.binaris.wizardry.core.networking.s2c.SpellGlyphPacketS2C;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffers;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * A utility class for client-side operations. Normally used for quick and easy formatting of components.
 */
public final class ClientUtils {
    /**
     * Checks if a spell should be displayed (name, description and any other data) in the item stack.
     *
     * @param spell The spell to check.
     * @param stack The item stack associated with the spell.
     * @return true if the spell should be displayed as discovered, false otherwise.
     */
    public static boolean shouldDisplayDiscovered(Spell spell, @Nullable ItemStack stack) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return false;

        if (Minecraft.getInstance().screen instanceof MerchantScreen) {
            MerchantOffers recipes = ((MerchantScreen) Minecraft.getInstance().screen).getMenu().getOffers();
            if (recipes.stream().anyMatch(r -> r.getResult() == stack)) {
                return true;
            }
        }

        if (player.containerMenu instanceof MerchantMenu merchantMenu) {
            MerchantContainer tradeContainer = ((MerchantMenuAccessor) merchantMenu).getTradeContainer();
            if (tradeContainer.getItem(2) == stack) {
                return true;
            }
        }

        if (player.isCreative()) return true;
        return Services.OBJECT_DATA.getSpellManagerData(player).hasSpellBeenDiscovered(spell);
    }

    /**
     * Returns the current local player. (Safety wrapper for Minecraft.getInstance().player)
     *
     * @return The current local player.
     */
    public static LocalPlayer getPlayer() {
        return Minecraft.getInstance().player;
    }

    /**
     * Handles a glyph data packet received from the server by the networking layer.
     *
     * @param message The glyph data packet received from the server.
     */
    public static void handleGlyphDataPacket(SpellGlyphPacketS2C message) {
        SpellGlyphData data = GlyphClientHandler.INSTANCE.getGlyphData();
        data.randomNames = new HashMap<>();
        data.randomDescriptions = new HashMap<>();

        for (Spell spell : Services.REGISTRY_UTIL.getSpells()) {
            ResourceLocation spellId = spell.getLocation();
            String name = message.getNames().get(spellId);
            String description = message.getDescriptions().get(spellId);

            if (name != null) data.randomNames.put(spell, name);
            if (description != null) data.randomDescriptions.put(spell, description);
        }
    }

    /**
     * Util class to get the translatable name of a scroll with a saved spell in it, also checks if the spell is discovered.
     *
     * @param scroll The scroll item stack.
     * @return The translatable name of the scroll.
     */
    public static Component getScrollDisplayName(ItemStack scroll) {
        Spell spell = RegistryUtils.getSpell(scroll);
        boolean discovered = ClientUtils.shouldDisplayDiscovered(spell, scroll);
        Component name = discovered ? spell.getDescriptionFormatted() :
                SpellGlyphData.getGlyphNameFormatted(spell, GlyphClientHandler.INSTANCE.getGlyphData());
        return Component.translatable("item.ebwizardry.scroll", name);
    }

    /**
     * Util class to get the translatable name of a spell book with a saved spell in it, also checks if the spell is discovered.
     *
     * @param book The spell book item stack.
     * @return The translatable name of the spell book.
     */
    public static Component getBookDisplayName(ItemStack book) {
        Spell spell = RegistryUtils.getSpell(book);
        if (spell == Spells.NONE) return Component.translatable("item.ebwizardry.spell_book.empty");
        boolean discovered = ClientUtils.shouldDisplayDiscovered(spell, book);
        Component name = discovered ? spell.getDescriptionFormatted() :
                SpellGlyphData.getGlyphNameFormatted(spell, GlyphClientHandler.INSTANCE.getGlyphData());
        return Component.translatable("item.ebwizardry.spell_book", name);
    }

    /**
     * Util class to open a spell book screen.
     *
     * @param stack The spell book item stack.
     */
    public static void openSpellBook(ItemStack stack) {
        Minecraft.getInstance().setScreen(new SpellBookScreen(stack));
    }

    private ClientUtils() {
    }
}
