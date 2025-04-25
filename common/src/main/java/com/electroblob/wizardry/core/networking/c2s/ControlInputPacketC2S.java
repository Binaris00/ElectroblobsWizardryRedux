package com.electroblob.wizardry.core.networking.c2s;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.content.menu.ArcaneWorkbenchMenu;
import com.electroblob.wizardry.core.networking.abst.Message;
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

    // TODO MISSING SOME KEYS...
    @Override
    public void handleServer(MinecraftServer server, ServerPlayer player) {
        if(player == null) return;
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
        CANCEL_RESURRECT, POSSESSION_PROJECTILE,
        CLEAR_BUTTON
    }
}

                /*case RESURRECT_BUTTON:
                    if(!player.isAlive() && Resurrection.getRemainingWaitTime(player.deathTime) == 0)
                    {
                        ItemStack stack = InventoryUtils.getHotbar(player).stream().filter(s -> Resurrection.canStackResurrect(s, player)).findFirst().orElse(null);

                        if(stack != null)
                        {
                            if(MinecraftForge.EVENT_BUS.post(new ResurrectionEvent(player, player))) break;
                            ((ISpellCastingItem)stack.getItem()).cast(stack, Spells.resurrection, player, InteractionHand.MAIN_HAND, 0, new SpellModifiers());
                            break;
                        }
                    }
                    Wizardry.logger.warn("Received a resurrect button packet, but the player that sent it was not" + " currently able to resurrect. This should not happen!");
                    break;
                case CANCEL_RESURRECT:
                    if(player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) break;
                    if(!player.isAlive())
                    {
                        ItemStack stack = InventoryUtils.getHotbar(player).stream().filter(s -> Resurrection.canStackResurrect(s, player)).findFirst().orElse(null);
                        if(stack != null)
                        {
                            player.drop(stack, true, false);
                            player.getInventory().removeItem(stack);
                            break;
                        }
                        Wizardry.logger.warn("Received a cancel resurrect packet, but the player that sent it was not" + " holding a wand with the resurrection spell. This should not happen!");
                    }

                    Wizardry.logger.warn("Received a cancel resurrect packet, but the player that sent it was not" + " currently dead. This should not happen!");
                    break;*/
//            case POSSESSION_PROJECTILE:
//                if (!Possession.isPossessing(player))
//                    Wizardry.logger.warn("Received a possession projectile packet, " + "but the player that sent it is not currently possessing anything!");
//                Possession.shootProjectile(player);
//                break;
//                break;
