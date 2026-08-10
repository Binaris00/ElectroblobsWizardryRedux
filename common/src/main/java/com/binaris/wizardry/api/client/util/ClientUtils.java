package com.binaris.wizardry.api.client.util;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.RegistryUtils;
import com.binaris.wizardry.client.gui.screens.SpellBookScreen;
import com.binaris.wizardry.content.data.SpellGlyphData;
import com.binaris.wizardry.core.mixin.accessor.MerchantMenuAccessor;
import com.binaris.wizardry.core.networking.s2c.SpellGlyphPacketS2C;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffers;

import javax.annotation.Nullable;
import java.util.HashMap;

/// A utility class for client-side operations. Normally used for quick and easy formatting of components.
public final class ClientUtils {
    /// Checks if a spell should be displayed (name, description and any other data) in the item stack.
    ///
    /// @param spell The spell to check.
    /// @param stack The item stack associated with the spell.
    /// @return true if the spell should be displayed as discovered, false otherwise.
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

    /// Returns the current local player. (Safety wrapper for Minecraft.getInstance().player)
    ///
    /// @return The current local player.
    public static LocalPlayer getPlayer() {
        return Minecraft.getInstance().player;
    }

    /// Handles a glyph data packet received from the server by the networking layer.
    ///
    /// @param message The glyph data packet received from the server.
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

    /// Util class to get the translatable name of a scroll with a saved spell in it, also checks if the spell is discovered.
    ///
    /// @param scroll The scroll item stack.
    /// @return The translatable name of the scroll.
    public static Component getScrollDisplayName(ItemStack scroll) {
        Spell spell = RegistryUtils.getSpell(scroll);
        boolean discovered = ClientUtils.shouldDisplayDiscovered(spell, scroll);
        Component name = discovered ? spell.getDescriptionFormatted() :
                SpellGlyphData.getGlyphNameFormatted(spell, GlyphClientHandler.INSTANCE.getGlyphData());
        return Component.translatable("item.ebwizardry.scroll", name);
    }

    /// Util class to get the translatable name of a spell book with a saved spell in it, also checks if the spell is discovered.
    ///
    /// @param book The spell book item stack.
    /// @return The translatable name of the spell book.
    public static Component getBookDisplayName(ItemStack book) {
        Spell spell = RegistryUtils.getSpell(book);
        if (spell == Spells.NONE) return Component.translatable("item.ebwizardry.spell_book.empty");
        boolean discovered = ClientUtils.shouldDisplayDiscovered(spell, book);
        Component name = discovered ? spell.getDescriptionFormatted() :
                SpellGlyphData.getGlyphNameFormatted(spell, GlyphClientHandler.INSTANCE.getGlyphData());
        return Component.translatable("item.ebwizardry.spell_book", name);
    }

    /// Util class to open a spell book screen.
    ///
    /// @param stack The spell book item stack.
    public static void openSpellBook(ItemStack stack) {
        Minecraft.getInstance().setScreen(new SpellBookScreen(stack));
    }

    /// Calculates a smooth scaling factor over time, commonly used for visual fading/scaling animations
    /// (such as particles or UI elements) based on remaining or elapsed lifetime.
    ///
    /// @param lifetime     The total lifetime of the element (in ticks). If negative, it handles infinite lifetime scaling up.
    /// @param ticksExisted The total full ticks the element has been alive.
    /// @param partialTicks The fractional tick time for smooth frame interpolation.
    /// @param startLength  The duration (in ticks) of the introductory scale-up phase.
    /// @param endLength    The duration (in ticks) of the concluding scale-down phase.
    /// @return A smooth scale factor clamped between 0.0F and 1.0F.
    public static float smoothScaleFactor(int lifetime, int ticksExisted, float partialTicks, int startLength, int endLength) {
        float age = ticksExisted + partialTicks;
        float s = Mth.clamp(age < startLength || lifetime < 0 ? age / startLength : (lifetime - age) / endLength, 0, 1);
        s = (float) Math.pow(s, 0.4);
        return s;
    }

    /// Injects an alpha/opacity channel into an existing RGB hex color integer.
    ///
    /// @param color   The raw RGB color code (e.g., 0xFFFFFF).
    /// @param opacity The alpha percentage ranging from 0.0F (fully transparent) to 1.0F (fully opaque).
    /// @return An ARGB format color integer.
    public static int makeTranslucentColor(int color, float opacity) {
        return color | ((int)(opacity * 0xFF) << 24);
    }

    /// Blends two RGB colors together based on a specified proportional bias.
    ///
    /// @param color1     The starting base color.
    /// @param color2     The target color to blend into the base.
    /// @param proportion The blend factor where 0.0F yields color1 and 1.0F yields color2.
    /// @return The mixed RGB color code integer.
    public static int mixColor(int color1, int color2, float proportion) {
        proportion = Mth.clamp(proportion, 0, 1);

        int r1 = color1 >> 16 & 255;
        int g1 = color1 >> 8 & 255;
        int b1 = color1 & 255;
        int r2 = color2 >> 16 & 255;
        int g2 = color2 >> 8 & 255;
        int b2 = color2 & 255;

        int r = (int) (r1 + (r2 - r1) * proportion);
        int g = (int) (g1 + (g2 - g1) * proportion);
        int b = (int) (b1 + (b2 - b1) * proportion);

        return (r << 16) + (g << 8) + b;
    }

    private ClientUtils() {
    }
}
