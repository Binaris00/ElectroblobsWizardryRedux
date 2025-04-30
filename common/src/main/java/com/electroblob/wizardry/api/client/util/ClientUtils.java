package com.electroblob.wizardry.api.client.util;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.content.data.SpellGlyphData;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.core.mixin.accessor.MerchantMenuAccessor;
import com.electroblob.wizardry.core.networking.s2c.SpellGlyphPacketS2C;
import com.electroblob.wizardry.core.platform.Services;
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

public class ClientUtils {

    public static boolean isFirstPerson(Entity entity) {
        return entity == Minecraft.getInstance().getCameraEntity() && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON;
    }

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

        if (!EBConfig.discoveryMode) return true;
        if (player.isCreative()) return true;
        return Services.WIZARD_DATA.getWizardData(player, player.clientLevel).hasSpellBeenDiscovered(spell);
    }

    public static LocalPlayer getPlayer(){
        return Minecraft.getInstance().player;
    }

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

    public static Component getScrollDisplayName(ItemStack scroll) {
        Spell spell = SpellUtil.getSpell(scroll);
        return Component.translatable(String.format("item." + WizardryMainMod.MOD_ID + ".scroll", String.format("spell." + spell.getDescriptionId())).trim());
    }
}
