package com.binaris.wizardry.core.networking.c2s;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.core.EBLogger;
import com.binaris.wizardry.api.content.item.ISpellCastingItem;
import com.binaris.wizardry.content.menu.ArcaneWorkbenchMenu;
import com.binaris.wizardry.core.networking.abst.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ControlInputPacketC2S implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("control_input");
    private final ControlType controlType;

    public ControlInputPacketC2S(ControlType type) {
        this.controlType = type;
    }

    public ControlInputPacketC2S(FriendlyByteBuf buf) {
        this.controlType = ControlType.values()[buf.readInt()];
    }

    @Override
    public void encode(FriendlyByteBuf pBuf) {
        pBuf.writeInt(controlType.ordinal());
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void handleServer(MinecraftServer server, ServerPlayer player) {
        if (player == null) return;
        ItemStack wand = player.getMainHandItem();

        if (!(wand.getItem() instanceof ISpellCastingItem)) {
            wand = player.getOffhandItem();
        }

        switch (controlType) {
            case NEXT_SPELL_KEY:
                if (wand.getItem() instanceof ISpellCastingItem castItem) {
                    castItem.selectNextSpell(wand);
                    player.stopUsingItem();
                }
                break;
            case PREVIOUS_SPELL_KEY:
                if (wand.getItem() instanceof ISpellCastingItem castItem) {
                    castItem.selectPreviousSpell(wand);
                    player.stopUsingItem();
                }
                break;
            case APPLY_BUTTON:
                if (!(player.containerMenu instanceof ArcaneWorkbenchMenu menu)) {
                    EBLogger.warn("Received a ControlInputPacketC2S, but the player that sent it was not currently using an arcane workbench. This should not happen!");
                } else {
                    menu.onApplyButtonPressed(player);
                }
                break;
            case CLEAR_BUTTON:
                if (!(player.containerMenu instanceof ArcaneWorkbenchMenu menu)) {
                    EBLogger.warn("Received a ControlInputPacketC2S, but the player that sent it was not currently using an arcane workbench. This should not happen!");
                } else {
                    menu.onClearButtonPressed(player);
                }
        }
    }

    public enum ControlType {
        APPLY_BUTTON, NEXT_SPELL_KEY,
        PREVIOUS_SPELL_KEY, RESURRECT_BUTTON,
        CLEAR_BUTTON
    }
}
