package com.electroblob.wizardry.setup.registries.client;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.event.EBClientTickEvent;
import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.client.SpellGUIDisplay;
import com.electroblob.wizardry.content.item.WandItem;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.core.networking.c2s.ControlInputPacketC2S;
import com.electroblob.wizardry.core.networking.c2s.SpellAccessPacketC2S;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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
                = new KeyMapping("key." + WizardryMainMod.MOD_ID + ".spell_" + (i + 1), InputConstants.Type.KEYSYM, InputConstants.KEY_1 + i, CATEGORY));
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
        ControlInputPacketC2S msg = new ControlInputPacketC2S(ControlInputPacketC2S.ControlType.NEXT_SPELL_KEY);
        Services.NETWORK_HELPER.sendToServer(msg);
        ((ISpellCastingItem) wand.getItem()).selectNextSpell(wand);
        SpellGUIDisplay.playSpellSwitchAnimation(true);

        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(EBSounds.ITEM_WAND_SWITCH_SPELL.get(), 1));
    }

    public static void selectPreviousSpell(ItemStack wand) {
        ControlInputPacketC2S msg = new ControlInputPacketC2S(ControlInputPacketC2S.ControlType.PREVIOUS_SPELL_KEY);
        Services.NETWORK_HELPER.sendToServer(msg);
        ((ISpellCastingItem) wand.getItem()).selectPreviousSpell(wand);
        SpellGUIDisplay.playSpellSwitchAnimation(false);

        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(EBSounds.ITEM_WAND_SWITCH_SPELL.get(), 1));
    }

    private static void selectSpell(ItemStack wand, int index) {
        if (((ISpellCastingItem) wand.getItem()).selectSpell(wand, index)) {
            SpellAccessPacketC2S msg = new SpellAccessPacketC2S(index);
            Services.NETWORK_HELPER.sendToServer(msg);

            SpellGUIDisplay.playSpellSwitchAnimation(true);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(EBSounds.ITEM_WAND_SWITCH_SPELL.get(), 1));
        }
    }
}
