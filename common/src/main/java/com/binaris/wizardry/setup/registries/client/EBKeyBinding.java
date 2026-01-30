package com.binaris.wizardry.setup.registries.client;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.event.EBClientTickEvent;
import com.binaris.wizardry.api.content.item.ISpellCastingItem;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.client.SpellGUIDisplay;
import com.binaris.wizardry.content.item.WandItem;
import com.binaris.wizardry.core.EBConfig;
import com.binaris.wizardry.core.networking.c2s.ControlInputPacketC2S;
import com.binaris.wizardry.core.networking.c2s.SpellAccessPacketC2S;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.Spells;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.stream.IntStream;

public final class EBKeyBinding {
    public static final String CATEGORY = "key.categories." + WizardryMainMod.MOD_ID;

    public static final KeyMapping NEXT_SPELL = new KeyMapping("key." + WizardryMainMod.MOD_ID + ".next_spell", InputConstants.Type.KEYSYM, InputConstants.KEY_N, CATEGORY);
    public static final KeyMapping PREVIOUS_SPELL = new KeyMapping("key." + WizardryMainMod.MOD_ID + ".previous_spell", InputConstants.Type.KEYSYM, InputConstants.KEY_B, CATEGORY);
    public static final KeyMapping[] SPELL_QUICK_ACCESS = new KeyMapping[WandItem.BASE_SPELL_SLOTS + EBConfig.UPGRADE_STACK_LIMIT];

    static boolean nextSpellKeyPressed = false;
    static boolean previousSpellKeyPressed = false;
    static boolean[] quickAccessKeyPressed = new boolean[SPELL_QUICK_ACCESS.length];

    static {
        IntStream.range(0, SPELL_QUICK_ACCESS.length).forEach(i -> SPELL_QUICK_ACCESS[i]
                = new KeyMapping("key." + WizardryMainMod.MOD_ID + ".spell_" + (i + 1), InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY));
    }

    private EBKeyBinding() {
    }

    public static void onClientTick(EBClientTickEvent event) {
        Player player = event.getMinecraft().player;
        if (player == null) return;

        ItemStack wand = EntityUtil.getWandInUse(player);
        if (wand == null) return;

        if (NEXT_SPELL.isDown() && Minecraft.getInstance().mouseHandler.isMouseGrabbed()) {
            if (!nextSpellKeyPressed) {
                nextSpellKeyPressed = true;
                selectNextSpell(wand);
            }
        } else {
            nextSpellKeyPressed = false;
        }

        if (PREVIOUS_SPELL.isDown() && Minecraft.getInstance().mouseHandler.isMouseGrabbed()) {
            if (!previousSpellKeyPressed) {
                previousSpellKeyPressed = true;
                selectPreviousSpell(wand);
            }
        } else {
            previousSpellKeyPressed = false;
        }

        for (int i = 0; i < SPELL_QUICK_ACCESS.length; i++) {
            if (SPELL_QUICK_ACCESS[i].isDown() && Minecraft.getInstance().mouseHandler.isMouseGrabbed()) {
                if (!quickAccessKeyPressed[i]) {
                    quickAccessKeyPressed[i] = true;
                    selectSpell(wand, i);
                }
            } else {
                quickAccessKeyPressed[i] = false;
            }
        }
    }

    public static void selectNextSpell(ItemStack wand) {
        ISpellCastingItem item = (ISpellCastingItem) wand.getItem();
        if (item.getCurrentSpell(wand) == Spells.NONE && item.getNextSpell(wand) == Spells.NONE) return;

        ControlInputPacketC2S msg = new ControlInputPacketC2S(ControlInputPacketC2S.ControlType.NEXT_SPELL_KEY);
        Services.NETWORK_HELPER.sendToServer(msg);
        if (Services.PLATFORM.getPlatformName().equals("Fabric")) item.selectNextSpell(wand);
        SpellGUIDisplay.playSpellSwitchAnimation(true);

        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(EBSounds.ITEM_WAND_SWITCH_SPELL.get(), 1));
    }

    public static void selectPreviousSpell(ItemStack wand) {
        ISpellCastingItem item = (ISpellCastingItem) wand.getItem();
        if (item.getCurrentSpell(wand) == Spells.NONE && item.getPreviousSpell(wand) == Spells.NONE) return;

        ControlInputPacketC2S msg = new ControlInputPacketC2S(ControlInputPacketC2S.ControlType.PREVIOUS_SPELL_KEY);
        Services.NETWORK_HELPER.sendToServer(msg);
        if (Services.PLATFORM.getPlatformName().equals("Fabric")) item.selectPreviousSpell(wand);
        SpellGUIDisplay.playSpellSwitchAnimation(false);

        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(EBSounds.ITEM_WAND_SWITCH_SPELL.get(), 1));
    }

    private static void selectSpell(ItemStack wand, int index) {
        ISpellCastingItem item = (ISpellCastingItem) wand.getItem();
        if (item.getCurrentSpell(wand) == Spells.NONE && item.getSpells(wand).length == 0) return;

        if (item.selectSpell(wand, index)) {
            SpellAccessPacketC2S msg = new SpellAccessPacketC2S(index);
            Services.NETWORK_HELPER.sendToServer(msg);

            SpellGUIDisplay.playSpellSwitchAnimation(true);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(EBSounds.ITEM_WAND_SWITCH_SPELL.get(), 1));
        }
    }
}
