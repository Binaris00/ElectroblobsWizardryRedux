package com.electroblob.wizardry.setup.registries.client;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.event.EBClientTickEvent;
import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.content.item.WandItem;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class EBKeyBinding {
    public static final String CATEGORY = "key.categories." + WizardryMainMod.MOD_ID;

    public static final KeyMapping NEXT_SPELL = new KeyMapping("key." + WizardryMainMod.MOD_ID + ".next_spell", InputConstants.Type.KEYSYM, InputConstants.KEY_N, CATEGORY);
    public static final KeyMapping PREVIOUS_SPELL = new KeyMapping("key." + WizardryMainMod.MOD_ID + ".previous_spell", InputConstants.Type.KEYSYM, InputConstants.KEY_B, CATEGORY);
    public static final KeyMapping[] SPELL_QUICK_ACCESS = new KeyMapping[WandItem.BASE_SPELL_SLOTS + EBConfig.UPGRADE_STACK_LIMIT];

    static boolean NkeyPressed = false;
    static boolean BkeyPressed = false;
    static boolean[] quickAccessKeyPressed = new boolean[SPELL_QUICK_ACCESS.length];

    static {
        for (int i = 0; i < SPELL_QUICK_ACCESS.length; i++) {
            SPELL_QUICK_ACCESS[i] = new KeyMapping("key." + WizardryMainMod.MOD_ID + ".spell_" + (i + 1), InputConstants.Type.KEYSYM, InputConstants.KEY_1 + i, CATEGORY);
        }
    }

    public static void onClientTick(EBClientTickEvent event){
        Player player = Minecraft.getInstance().player;

        if (player != null) {
            ItemStack wand = getWandInUse(player);
            if (wand == null) return;

            if (NEXT_SPELL.isDown() && Minecraft.getInstance().mouseHandler.isMouseGrabbed()) {
                if (!NkeyPressed) {
                    NkeyPressed = true;
                    selectNextSpell(wand);
                }
            } else {
                NkeyPressed = false;
            }

            if (PREVIOUS_SPELL.isDown() && Minecraft.getInstance().mouseHandler.isMouseGrabbed()) {
                if (!BkeyPressed) {
                    BkeyPressed = true;
                    selectPreviousSpell(wand);
                }
            } else {
                BkeyPressed = false;
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
    }

    private static ItemStack getWandInUse(Player player) {
        ItemStack wand = player.getMainHandItem();

        if (!(wand.getItem() instanceof ISpellCastingItem) || ((ISpellCastingItem) wand.getItem()).getSpells(wand).length < 2) {
            wand = player.getOffhandItem();
            if (!(wand.getItem() instanceof ISpellCastingItem) || ((ISpellCastingItem) wand.getItem()).getSpells(wand).length < 2)
                return null;
        }

        return wand;
    }

    private static void selectNextSpell(ItemStack wand) {
        //PacketControlInput msg = new PacketControlInput(PacketControlInput.ControlType.NEXT_SPELL_KEY);
        //WizardryPacketHandler.net.sendToServer(msg);
        ((ISpellCastingItem) wand.getItem()).selectNextSpell(wand);
        //GuiSpellDisplay.playSpellSwitchAnimation(true);
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(EBSounds.ITEM_WAND_SWITCH_SPELL.get(), 1));
    }

    private static void selectPreviousSpell(ItemStack wand) {
        //PacketControlInput msg = new PacketControlInput(PacketControlInput.ControlType.PREVIOUS_SPELL_KEY);
        //WizardryPacketHandler.net.sendToServer(msg);
        ((ISpellCastingItem) wand.getItem()).selectPreviousSpell(wand);
        //GuiSpellDisplay.playSpellSwitchAnimation(false);
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(EBSounds.ITEM_WAND_SWITCH_SPELL.get(), 1));
    }

    private static void selectSpell(ItemStack wand, int index) {
        if (((ISpellCastingItem) wand.getItem()).selectSpell(wand, index)) {
            //PacketSpellQuickAccess msg = new PacketSpellQuickAccess(index);
            //WizardryPacketHandler.net.sendToServer(msg);

            //GuiSpellDisplay.playSpellSwitchAnimation(true);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(EBSounds.ITEM_WAND_SWITCH_SPELL.get(), 1));
        }
    }
}
